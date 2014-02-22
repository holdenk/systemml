/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2014
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.runtime.controlprogram.parfor.opt;

import com.ibm.bi.dml.hops.OptimizerUtils;
import com.ibm.bi.dml.lops.LopProperties;
import com.ibm.bi.dml.parser.ParForStatementBlock;
import com.ibm.bi.dml.runtime.DMLRuntimeException;
import com.ibm.bi.dml.runtime.DMLUnsupportedOperationException;
import com.ibm.bi.dml.runtime.controlprogram.ExecutionContext;
import com.ibm.bi.dml.runtime.controlprogram.LocalVariableMap;
import com.ibm.bi.dml.runtime.controlprogram.ParForProgramBlock;
import com.ibm.bi.dml.runtime.controlprogram.ParForProgramBlock.PDataPartitioner;
import com.ibm.bi.dml.runtime.controlprogram.ParForProgramBlock.PExecMode;
import com.ibm.bi.dml.runtime.controlprogram.ParForProgramBlock.POptMode;
import com.ibm.bi.dml.runtime.controlprogram.ParForProgramBlock.PResultMerge;
import com.ibm.bi.dml.runtime.controlprogram.ParForProgramBlock.PTaskPartitioner;
import com.ibm.bi.dml.runtime.controlprogram.parfor.opt.OptNode.ExecType;
import com.ibm.bi.dml.runtime.controlprogram.parfor.opt.OptNode.ParamType;
import com.ibm.bi.dml.runtime.controlprogram.parfor.opt.PerfTestTool.TestMeasure;
import com.ibm.bi.dml.runtime.controlprogram.parfor.stat.InfrastructureAnalyzer;

/**
 * Rule-Based ParFor Optimizer (time: O(n)):
 * 
 * Applied rule-based rewrites:
 * - see base class.
 * 
 * 
 * Checked constraints:
 * - 1) rewrite set data partitioner (incl. recompile RIX)
 * - 3) rewrite set execution strategy
 * - 8) rewrite set degree of parallelism
 * - 9) rewrite set task partitioner
 * - 10) rewrite set result merge 		 		
 * 	 
 * TODO generalize for nested parfor (currently only awareness of top-level constraints, if present leave child as they are)
 * 
 */
public class OptimizerConstrained extends OptimizerRuleBased
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2014\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	@Override
	public POptMode getOptMode() 
	{
		return POptMode.CONSTRAINED;
	}
	
	
	/**
	 * Main optimization procedure.
	 * 
	 * Transformation-based heuristic (rule-based) optimization
	 * (no use of sb, direct change of pb).
	 */
	@Override
	public boolean optimize(ParForStatementBlock sb, ParForProgramBlock pb, OptTree plan, CostEstimator est, ExecutionContext ec) 
		throws DMLRuntimeException, DMLUnsupportedOperationException 
	{
		LOG.debug("--- "+getOptMode()+" OPTIMIZER -------");

		OptNode pn = plan.getRoot();
		double M = -1, M2 = -1; //memory consumption
		
		//early abort for empty parfor body 
		if( pn.isLeaf() )
			return true;
		
		//ANALYZE infrastructure properties
		_N     = Integer.parseInt(pn.getParam(ParamType.NUM_ITERATIONS)); 
		_Nmax  = pn.getMaxProblemSize(); 
		_lk    = InfrastructureAnalyzer.getLocalParallelism();
		_lkmaxCP = (int) Math.ceil( PAR_K_FACTOR * _lk ); 
		_lkmaxMR = (int) Math.ceil( PAR_K_MR_FACTOR * _lk );
		_rnk   = InfrastructureAnalyzer.getRemoteParallelNodes();  
		_rk    = InfrastructureAnalyzer.getRemoteParallelMapTasks(); 
		_rkmax = (int) Math.ceil( PAR_K_FACTOR * _rk ); 
		_lm   = OptimizerUtils.getMemBudget(true);
		_rm   = OptimizerUtils.MEM_UTIL_FACTOR * InfrastructureAnalyzer.getRemoteMaxMemory(); //Hops.getMemBudget(false); 
		
		_cost = est;
		
		//debug and warnings output
		LOG.debug(getOptMode()+" OPT: Optimize with local_max_mem="+toMB(_lm)+" and remote_max_mem="+toMB(_rm)+")." );
		if( _rnk<=0 || _rk<=0 )
			LOG.warn(getOptMode()+" OPT: Optimize for inactive cluster (num_nodes="+_rnk+", num_map_slots="+_rk+")." );
		
		//ESTIMATE memory consumption 
		ExecType oldET = pn.getExecType();
		int oldK = pn.getK();
		pn.setSerialParFor(); //for basic mem consumption 
		M = _cost.getEstimate(TestMeasure.MEMORY_USAGE, pn);
		pn.setExecType(oldET);
		pn.setK(oldK);
		LOG.debug(getOptMode()+" OPT: estimated mem (serial exec) M="+toMB(M) );
		
		//OPTIMIZE PARFOR PLAN
		
		// rewrite 1: data partitioning (incl. log. recompile RIX)
		rewriteSetDataPartitioner( pn, ec.getVariables() );
		M = _cost.getEstimate(TestMeasure.MEMORY_USAGE, pn); //reestimate
		
		// rewrite 2: rewrite result partitioning (incl. log/phy recompile LIX) 
		boolean flagLIX = super.rewriteSetResultPartitioning( pn, M, ec.getVariables() );
		M = _cost.getEstimate(TestMeasure.MEMORY_USAGE, pn); //reestimate 
		M2 = _cost.getEstimate(TestMeasure.MEMORY_USAGE, pn, LopProperties.ExecType.CP);
		LOG.debug(getOptMode()+" OPT: estimated new mem (serial exec) M="+toMB(M) );
		LOG.debug(getOptMode()+" OPT: estimated new mem (serial exec, all CP) M="+toMB(M2) );
		
		// rewrite 3: execution strategy
		boolean flagRecompMR = rewriteSetExecutionStategy( pn, M, M2, flagLIX );
		
		//exec-type-specific rewrites
		if( pn.getExecType() == ExecType.MR )
		{
			if( flagRecompMR ){
				//rewrite 4: set operations exec type
				rewriteSetOperationsExecType( pn, flagRecompMR );
				M = _cost.getEstimate(TestMeasure.MEMORY_USAGE, pn); //reestimate 		
			}
			
			// rewrite 5: data colocation
			super.rewriteDataColocation( pn, ec.getVariables() );
			
			// rewrite 6: rewrite set partition replication factor
			super.rewriteSetPartitionReplicationFactor( pn, ec.getVariables() );
			
			// rewrite 7: rewrite set partition replication factor
			super.rewriteSetExportReplicationFactor( pn, ec.getVariables() );
			
			// rewrite 8: nested parallelism (incl exec types)	
			boolean flagNested = super.rewriteNestedParallelism( pn, M, flagLIX );
			
			// rewrite 9: determine parallelism
			rewriteSetDegreeOfParallelism( pn, M, flagNested );
			
			// rewrite 10: task partitioning 
			rewriteSetTaskPartitioner( pn, flagNested, flagLIX );
		}
		else //if( pn.getExecType() == ExecType.CP )
		{
			// rewrite 9: determine parallelism
			rewriteSetDegreeOfParallelism( pn, M, false );
			
			// rewrite 10: task partitioning
			rewriteSetTaskPartitioner( pn, false, false ); //flagLIX always false 
		}	
		
		//rewrite 11: set result merge
		rewriteSetResultMerge( pn, ec.getVariables(), true );
		
		//rewrite 12: set local recompile memory budget
		super.rewriteSetRecompileMemoryBudget( pn );
		
		///////
		//Final rewrites for cleanup / minor improvements
		
		// rewrite 13: parfor (in recursive functions) to for
		super.rewriteRemoveRecursiveParFor( pn, ec.getVariables() );
		
		// rewrite 14: parfor (par=1) to for 
		super.rewriteRemoveUnnecessaryParFor( pn );
		
		//info optimization result
		_numEvaluatedPlans = 1;
		return true;
	}

	
	///////
	//REWRITE set data partitioner
	///
	
	/**
	 * 
	 * @param n
	 * @throws DMLRuntimeException 
	 */
	protected boolean rewriteSetDataPartitioner(OptNode n, LocalVariableMap vars) 
		throws DMLRuntimeException
	{
		boolean blockwise = false;
		
		// constraint awareness
		if( !n.getParam(ParamType.DATA_PARTITIONER).equals(PDataPartitioner.UNSPECIFIED.toString()) )
		{
			Object[] o = OptTreeConverter.getAbstractPlanMapping().getMappedProg(n.getID());
			ParForProgramBlock pfpb = (ParForProgramBlock) o[1];
			pfpb.setDataPartitioner(PDataPartitioner.valueOf(n.getParam(ParamType.DATA_PARTITIONER)));
			LOG.debug(getOptMode()+" OPT: forced 'set data partitioner' - result="+n.getParam(ParamType.DATA_PARTITIONER) );
		}	
		else
			super.rewriteSetDataPartitioner(n, vars);
		
		return blockwise;
	}
	
	
	///////
	//REWRITE set execution strategy
	///
	
	/**
	 * 
	 * 
	 * @param n
	 * @param M
	 * @throws DMLRuntimeException 
	 */
	protected boolean rewriteSetExecutionStategy(OptNode n, double M, double M2, boolean flagLIX) 
		throws DMLRuntimeException
	{
		boolean ret = false;
		
		// constraint awareness
		if( n.getExecType() != null  )
		{
			ParForProgramBlock pfpb = (ParForProgramBlock) OptTreeConverter
                    .getAbstractPlanMapping().getMappedProg(n.getID())[1];
			PExecMode mode = (n.getExecType()==ExecType.CP)? PExecMode.LOCAL : PExecMode.REMOTE_MR;
			pfpb.setExecMode( mode );	
			LOG.debug(getOptMode()+" OPT: forced 'set execution strategy' - result="+mode );	
		}
		else
			ret = super.rewriteSetExecutionStategy(n, M, M2, flagLIX);
		
		return ret;
	}

		
	///////
	//REWRITE set degree of parallelism
	///
		
	/**
	 * 
	 * @param n
	 * @param M
	 * @param kMax
	 * @param mMax  (per node)
	 * @param nested
	 */
	protected void rewriteSetDegreeOfParallelism(OptNode n, double M, boolean flagNested) 
	{
		// constraint awareness
		if( n.getK()>0 )
		{
			ParForProgramBlock pfpb = (ParForProgramBlock) OptTreeConverter
					.getAbstractPlanMapping().getMappedProg(n.getID())[1];
			pfpb.setDegreeOfParallelism(n.getK());
			LOG.debug(getOptMode()+" OPT: forced 'set degree of parallelism' - result=(see EXPLAIN)" );	
		}
		else 
			super.rewriteSetDegreeOfParallelism(n, M, flagNested);
	}
	
		
	///////
	//REWRITE set task partitioner
	///
	
	/**
	 * 
	 * @param n
	 * @param partitioner
	 */
	protected void rewriteSetTaskPartitioner(OptNode pn, boolean flagNested, boolean flagLIX) 
	{
		// constraint awareness
		if( !pn.getParam(ParamType.TASK_PARTITIONER).equals(PTaskPartitioner.UNSPECIFIED.toString()) )
		{
			ParForProgramBlock pfpb = (ParForProgramBlock) OptTreeConverter
                    .getAbstractPlanMapping().getMappedProg(pn.getID())[1];
			pfpb.setTaskPartitioner(PTaskPartitioner.valueOf(pn.getParam(ParamType.TASK_PARTITIONER)));
			String tsExt = "";
			if( pn.getParam(ParamType.TASK_SIZE)!=null )
			{
				pfpb.setTaskSize( Integer.parseInt(pn.getParam(ParamType.TASK_SIZE)) ); 
				tsExt+= "," + pn.getParam(ParamType.TASK_SIZE);
			}
			LOG.debug(getOptMode()+" OPT: forced 'set task partitioner' - result="+pn.getParam(ParamType.TASK_PARTITIONER)+tsExt );	
		}		
		else
		{
			 if( pn.getParam(ParamType.TASK_SIZE)!=null )
				LOG.warn("Cannot force task size without forcing task partitioner.");
				
			super.rewriteSetTaskPartitioner(pn, flagNested, flagLIX);
		}
	}
	
	
	///////
	//REWRITE set result merge
	///
	
	/**
	 *
	 * 
	 * @param n
	 * @throws DMLRuntimeException 
	 */
	protected void rewriteSetResultMerge( OptNode n, LocalVariableMap vars, boolean inLocal ) 
		throws DMLRuntimeException
	{
		// constraint awareness
		if( !n.getParam(ParamType.RESULT_MERGE).equals(PResultMerge.UNSPECIFIED.toString()) )
		{
			ParForProgramBlock pfpb = (ParForProgramBlock) OptTreeConverter
				    .getAbstractPlanMapping().getMappedProg(n.getID())[1];
			pfpb.setResultMerge(PResultMerge.valueOf(n.getParam(ParamType.RESULT_MERGE)));
			LOG.debug(getOptMode()+" OPT: force 'set result merge' - result="+n.getParam(ParamType.RESULT_MERGE) );
		}
		else
			super.rewriteSetResultMerge(n, vars, inLocal);	
	}		
}