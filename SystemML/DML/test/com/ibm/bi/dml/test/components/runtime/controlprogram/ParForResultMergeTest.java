package com.ibm.bi.dml.test.components.runtime.controlprogram;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.ibm.bi.dml.api.DMLScript;
import com.ibm.bi.dml.parser.Expression.ValueType;
import com.ibm.bi.dml.runtime.controlprogram.CacheableData;
import com.ibm.bi.dml.runtime.controlprogram.parfor.ResultMerge;
import com.ibm.bi.dml.runtime.controlprogram.parfor.util.IDSequence;
import com.ibm.bi.dml.runtime.instructions.CPInstructions.MatrixObjectNew;
import com.ibm.bi.dml.runtime.matrix.MatrixCharacteristics;
import com.ibm.bi.dml.runtime.matrix.MatrixFormatMetaData;
import com.ibm.bi.dml.runtime.matrix.io.InputInfo;
import com.ibm.bi.dml.runtime.matrix.io.MatrixBlock;
import com.ibm.bi.dml.runtime.matrix.io.OutputInfo;
import com.ibm.bi.dml.utils.CacheException;
import com.ibm.bi.dml.utils.DMLRuntimeException;
import com.ibm.bi.dml.utils.configuration.DMLConfig;

//TODO: tests for sparse representation.
public class ParForResultMergeTest 
{
	private static final int _par = 4;
	private static final int _dim = 10;	
	private IDSequence _seq = new IDSequence();

	@Test
	public void testSerialInMemoryResultMerge() 
		throws DMLRuntimeException, ParserConfigurationException, SAXException, IOException 
	{ 
		runResultMerge( false, true );
	}
	
	@Test
	public void testSerialFileBasedResultMerge() 
		throws DMLRuntimeException, ParserConfigurationException, SAXException, IOException 
	{ 
		runResultMerge( false, false );
	}
	
	@Test
	public void testParallelInMemoryResultMerge() 
		throws DMLRuntimeException, ParserConfigurationException, SAXException, IOException 
	{ 
		runResultMerge( true, true );
	}
	
	@Test
	public void testParallelFileBasedResultMerge() 
		throws DMLRuntimeException, ParserConfigurationException, SAXException, IOException 
	{ 
		runResultMerge( true, false );
	}
	
	private void runResultMerge(boolean parallel, boolean inMem) 
		throws ParserConfigurationException, SAXException, IOException, DMLRuntimeException
	{
		//init cache
		CacheableData.createCacheDir();
		
		//init input, output, comparison obj
		MatrixObjectNew[] in = new MatrixObjectNew[ _par ];
		for( int i=0; i<_par; i++ )
		{
			in[i] = createMatrixObject( _dim, true );
		}
		MatrixObjectNew out = createMatrixObject( _dim, true );
		MatrixObjectNew ref = createMatrixObject( _dim, true );
		
		//populate inputs and comparison object
		generateData( ref, in );			
		if( !inMem )
			for( MatrixObjectNew mo : in )
			{
				//write and delete in-memory data
				if(!inMem)
				{
					mo.exportData();
					mo.clearData();
				}
			}
		
		//run result merge
		ResultMerge rm = new ResultMerge(out, in, "./out", _par);
		if( parallel )
			out = rm.executeParallelMerge();
		else
			out = rm.executeSerialMerge();
		
		//compare result
		if( !checkOutput( ref, out ) )
			Assert.fail("Wrong result matrix.");	
		
		//cleanup
		for( MatrixObjectNew inMO : in )
			inMO.clearData();
		out.clearData();
		ref.clearData();
		
		CacheableData.cleanupCacheDir();
	}

	private MatrixObjectNew createMatrixObject(int dim, boolean withData) 
		throws ParserConfigurationException, SAXException, IOException, CacheException 
	{
		
		DMLConfig conf = null;
		try {
			conf = new DMLConfig(DMLScript.DEFAULT_SYSTEMML_CONFIG_FILEPATH);
		} catch (Exception e){
			System.out.println("ERROR: could not create DMLConfig from config file " + DMLScript.DEFAULT_SYSTEMML_CONFIG_FILEPATH);
		}
		
		String dir = null;
		try {
			dir = conf.getTextValue(DMLConfig.SCRATCH_SPACE);
		} catch (Exception e){
			System.out.println("ERROR: could not retrieve parameter " + DMLConfig.SCRATCH_SPACE + " from DMLConfig");
		}
		
		long id = _seq.getNextID();
		String fname = dir+"/"+String.valueOf(id);
		
		MatrixCharacteristics mc = new MatrixCharacteristics(dim, dim, dim, dim);
		MatrixFormatMetaData md = new MatrixFormatMetaData(mc, OutputInfo.BinaryBlockOutputInfo, InputInfo.BinaryBlockInputInfo);
		MatrixObjectNew mo = new MatrixObjectNew(ValueType.DOUBLE, fname, md);
		mo.setVarName( String.valueOf(id) );
		
		if( withData )
		{
			MatrixBlock mb = new MatrixBlock(dim,dim,false);
			mb.setValue(0, 0, 7d); // base data to check if div works
			mo.acquireModify(mb);
			mo.release();
		}
	
		return mo;
	}
	
	private void generateData(MatrixObjectNew ref, MatrixObjectNew[] in) 
		throws DMLRuntimeException 
	{
		long rows = ref.getNumRows();
		long cols = ref.getNumColumns();
		int index = 0;
		int subSize = (int) Math.ceil( rows * cols / in.length );
		double value; //dynamically assigned
		
		//set input data
		MatrixBlock refData = ref.acquireModify();
		MatrixBlock inData = in[ index ].acquireModify();
		
		for( int i=0; i<rows; i++ ) 
			for( int j=0; j<cols; j++ )
			{
				value = i*cols+(j+1);
				refData.setValue( i, j, value );				
				inData.setValue(i, j, value);
				if( value % subSize == 0 && index != in.length-1 )
				{
					in[ index ].release();
					index++;
					inData = in[ index ].acquireModify();
				}
			}
		
		ref.release();
		in[ index ].release();
	}
	
	private boolean checkOutput(MatrixObjectNew ref, MatrixObjectNew out) 
		throws CacheException 
	{
		boolean ret = true;
		
		MatrixBlock refMB = ref.acquireRead();
		MatrixBlock outMB = out.acquireRead();
		

		if(    refMB.getNumRows() != outMB.getNumRows() 
			|| refMB.getNumColumns() != outMB.getNumColumns() )
		{
			ret = false; 
		}
		else
		{
			int rows = refMB.getNumRows();
			int cols = refMB.getNumColumns();
			
			for( int i=0; i<rows; i++ )
				for( int j=0; j<cols; j++ )
					if( refMB.getValue(i, j) != outMB.getValue(i, j) )
					{
						System.out.println(refMB.getValue(i, j)+" vs "+outMB.getValue(i, j));
						ret=false;
						i=rows; break;
					}
		}

		
		ref.release();
		out.release();
		
		return ret;
	}
}
