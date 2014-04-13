/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2014
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.test.integration.functions.tertiary;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.bi.dml.api.DMLScript.RUNTIME_PLATFORM;
import com.ibm.bi.dml.hops.TertiaryOp;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.runtime.matrix.io.MatrixValue.CellIndex;
import com.ibm.bi.dml.test.integration.AutomatedTestBase;
import com.ibm.bi.dml.test.integration.TestConfiguration;
import com.ibm.bi.dml.test.utils.TestUtils;
import com.ibm.bi.dml.utils.Statistics;

/**
 * This test investigates the specific Hop-Lop rewrite ctable(seq(1,nrow(X)),X).
 * 
 * NOTE: table in R treats every distinct value of X as a specific value, while
 * we cast those double values to long. Hence, we need to round the generated 
 * dataset.
 * 
 */
public class CTableSequenceTest extends AutomatedTestBase 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2014\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	private final static String TEST_NAME1 = "CTableSequenceLeft";
	private final static String TEST_NAME2 = "CTableSequenceRight";
	
	private final static String TEST_DIR = "functions/tertiary/";
	private final static double eps = 1e-10;
	
	private final static int rows = 2407;
	private final static int maxVal = 7; 
	
	
	@Override
	public void setUp() 
	{
		addTestConfiguration(TEST_NAME1, new TestConfiguration(TEST_DIR, TEST_NAME1, new String[] { "B" })   ); 
		addTestConfiguration(TEST_NAME2, new TestConfiguration(TEST_DIR, TEST_NAME2, new String[] { "B" })   ); 
	}

	
	@Test
	public void testCTableSequenceLeftNoRewriteCP() 
	{
		runCTableSequenceTest(false, true, ExecType.CP);
	}
	
	@Test
	public void testCTableSequenceLeftRewriteCP() 
	{
		runCTableSequenceTest(true, true, ExecType.CP);
	}
	
	@Test
	public void testCTableSequenceLeftNoRewriteMR() 
	{
		runCTableSequenceTest(false, true, ExecType.MR);
	}
	
	@Test
	public void testCTableSequenceLeftRewriteMR() 
	{
		runCTableSequenceTest(true, true, ExecType.MR);
	}
	
	@Test
	public void testCTableSequenceRightNoRewriteCP() 
	{
		runCTableSequenceTest(false, false, ExecType.CP);
	}
	
	@Test
	public void testCTableSequenceRightRewriteCP() 
	{
		runCTableSequenceTest(true, false, ExecType.CP);
	}
	
	@Test
	public void testCTableSequenceRightNoRewriteMR() 
	{
		runCTableSequenceTest(false, false, ExecType.MR);
	}
	
	@Test
	public void testCTableSequenceRightRewriteMR() 
	{
		runCTableSequenceTest(true, false, ExecType.MR);
	}

	/**
	 * 
	 * @param sparseM1
	 * @param sparseM2
	 * @param instType
	 */
	private void runCTableSequenceTest( boolean rewrite, boolean left, ExecType et)
	{
		String TEST_NAME = left ? TEST_NAME1 : TEST_NAME2;
		
		//rtplatform for MR
		RUNTIME_PLATFORM platformOld = rtplatform;
		boolean rewriteOld = TertiaryOp.ALLOW_CTABLE_SEQUENCE_REWRITE;
		
		rtplatform = (et==ExecType.MR) ? RUNTIME_PLATFORM.HADOOP : RUNTIME_PLATFORM.HYBRID;
		TertiaryOp.ALLOW_CTABLE_SEQUENCE_REWRITE = rewrite;
		
		try
		{
			TestConfiguration config = getTestConfiguration(TEST_NAME);
			
			/* This is for running the junit test the new way, i.e., construct the arguments directly */
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + TEST_NAME + ".dml";
			programArgs = new String[]{"-explain","-args", HOME + INPUT_DIR + "A",
					                        Integer.toString(rows),
					                        Integer.toString(1),
					                        HOME + OUTPUT_DIR + "B"};
			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + 
			       HOME + INPUT_DIR + " " + HOME + EXPECTED_DIR;
			
			loadTestConfiguration(config);
	
			
			//generate actual dataset (always dense because values <=0 invalid)
			double[][] A = floor(getRandomMatrix(rows, 1, 1, maxVal, 1.0, 7), rows, 1); 
			writeInputMatrix("A", A, true);
	
			runTest(true, false, null, -1); 
			runRScript(true); 
			
			//compare matrices 
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromHDFS("B");
			HashMap<CellIndex, Double> rfile  = readRMatrixFromFS("B");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "Stat-DML", "Stat-R");
			
			//5 instead of 4 for rewrite because we dont pull it into the map task yet.
			int expectedNumCompiled = ((et==ExecType.CP) ? 2 : (rewrite ? 5 : 6));
			Assert.assertEquals("Unexpected number of compiled MR jobs.", expectedNumCompiled, Statistics.getNoOfCompiledMRJobs()); 
			
		}
		finally
		{
			rtplatform = platformOld;
			TertiaryOp.ALLOW_CTABLE_SEQUENCE_REWRITE = rewriteOld;
		}
	}

	/**
	 * 
	 * @param X
	 * @param rows
	 * @param cols
	 * @return
	 */
	private double[][] floor( double[][] X, int rows, int cols )
	{
		for( int i=0; i<rows; i++ )
			for( int j=0; j<cols; j++ )
				X[i][j] = Math.floor(X[i][j]);
		return X;
	}
}