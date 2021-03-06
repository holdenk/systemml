/**
 * (C) Copyright IBM Corp. 2010, 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.ibm.bi.dml.test.integration.functions.binary.matrix;

import java.util.HashMap;

import org.junit.Test;

import com.ibm.bi.dml.api.DMLScript;
import com.ibm.bi.dml.api.DMLScript.RUNTIME_PLATFORM;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.runtime.matrix.MatrixCharacteristics;
import com.ibm.bi.dml.runtime.matrix.data.MatrixValue.CellIndex;
import com.ibm.bi.dml.test.integration.AutomatedTestBase;
import com.ibm.bi.dml.test.integration.TestConfiguration;
import com.ibm.bi.dml.test.utils.TestUtils;

/**
 * TODO: extend test by various binary operator - unary aggregate operator combinations.
 * 
 */
public class UaggOuterChainTest extends AutomatedTestBase 
{
	
	private final static String TEST_NAME1 = "UaggOuterChain";
	private final static String TEST_DIR = "functions/binary/matrix/";
	private final static double eps = 1e-8;
	
	private final static int rows = 1468;
	private final static int cols1 = 73; //single block
	private final static int cols2 = 1052; //multi block
	
	private final static double sparsity1 = 0.5; //dense 
	private final static double sparsity2 = 0.1; //sparse
	
	public enum Type{
		GREATER,
		LESS,
		EQUALS,
		NOT_EQUALS,
		GREATER_EQUALS,
		LESS_EQUALS,
	}
	
	public enum SumType{ROW_SUM, COL_SUM, SUM_ALL}
		
	@Override
	public void setUp() 
	{
		TestUtils.clearAssertionInformation();
		addTestConfiguration(TEST_NAME1, new TestConfiguration(TEST_DIR, TEST_NAME1, new String[] { "C" })); 
	}
	
	// Less Uagg RowSums -- MR
	@Test
	public void testUaggOuterChainRowSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainRowSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainRowSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainRowSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	// Greater Uagg RowSums -- MR
	@Test
	public void testGreaterUaggOuterChainRowSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainRowSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainRowSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainRowSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	// LessEquals Uagg RowSums -- MR
	@Test
	public void testLessEqualsUaggOuterChainRowSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainRowSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainRowSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainRowSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	// GreaterEquals Uagg RowSums -- MR
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	// Equals Uagg RowSums -- MR
	@Test
	public void testEqualsUaggOuterChainRowSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainRowSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainRowSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainRowSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	// NotEquals Uagg RowSums -- MR
	@Test
	public void testNotEqualsUaggOuterChainRowSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainRowSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainRowSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.ROW_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainRowSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, true, SumType.ROW_SUM, false, ExecType.MR);
	}
	

	// -------------------------

	// Less Uagg RowSums -- SP
	@Test
	public void testLessUaggOuterChainRowSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.LESS, true, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainRowSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainRowSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainRowSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	// Greater Uagg RowSums -- SP
	@Test
	public void testGreaterUaggOuterChainRowSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.GREATER, true, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainRowSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainRowSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainRowSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}

	
	// LessEquals Uagg RowSums -- SP
	@Test
	public void testLessEqualsUaggOuterChainRowSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainRowSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainRowSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainRowSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	
	// GreaterThanEquals Uagg RowSums -- SP
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	
	// Equals Uagg RowSums -- SP
	@Test
	public void testEqualsUaggOuterChainRowSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.EQUALS, true, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainRowSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainRowSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainRowSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	


	// NotEquals Uagg RowSums -- SP
	@Test
	public void testNotEqualsUaggOuterChainRowSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainRowSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainRowSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainRowSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, true, SumType.ROW_SUM, false, ExecType.SPARK);
	}
	
	
	// ----------------------
	// Column Sums
	
	// Less Uagg ColumnSums -- MR
	@Test
	public void testUaggOuterChainColSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainColSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainColSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainColSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	
	// GreaterThanEquals Uagg ColumnSums -- MR
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	
	// Greater Uagg ColumnSums -- MR
	@Test
	public void testGreaterUaggOuterChainColSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainColSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainColSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainColSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	
	// LessThanEquals Uagg ColumnSums -- MR
	@Test
	public void testLessEqualsUaggOuterChainColSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainColSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainColSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainColSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	
	// Equals Uagg ColumnSums -- MR
	@Test
	public void testEqualsUaggOuterChainColSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainColSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainColSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainColSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	
	// NotEquals Uagg ColumnSums -- MR
	@Test
	public void testNotEqualsUaggOuterChainColSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.COL_SUM, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, true, SumType.COL_SUM, false, ExecType.MR);
	}
	
	
	// -------------------------
	// ColSums

	// Less Uagg ColSums -- SP
	@Test
	public void testLessUaggOuterChainColSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.LESS, true, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainColSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainColSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainColSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	// GreaterThanEquals Uagg ColSums -- SP
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainColSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	
	// Greater Uagg ColSums -- SP
	@Test
	public void testGreaterUaggOuterChainColSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.GREATER, true, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainColSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainColSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainColSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, true, SumType.COL_SUM, false, ExecType.SPARK);
	}

	
	// LessEquals Uagg ColSums -- SP
	@Test
	public void testLessEqualsUaggOuterChainColSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainColSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainColSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainColSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	

	// Equals Uagg ColSums -- SP
	@Test
	public void testEqualsUaggOuterChainColSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.EQUALS, true, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainColSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainColSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainColSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	


	// NotEquals Uagg ColSums -- SP
	@Test
	public void testNotEqualsUaggOuterChainColSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.COL_SUM, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, true, SumType.COL_SUM, false, ExecType.SPARK);
	}
	

	// Empty Block (Data with 0.0 values)
	
	@Test
	public void testLessUaggOuterChainRowSumsEmptyMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.ROW_SUM, true, ExecType.MR);
	}
	
	@Test
	public void testLessUaggOuterChainColSumsEmptyMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.COL_SUM, true, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainRowSumsEmptyMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.ROW_SUM, true, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainColSumsEmptyMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.COL_SUM, true, ExecType.SPARK);
	}
	
	//---------------------------------------------------
	// Sums operation test cases.
	//---------------------------------------------------
	
	// Less Uagg Sums -- MR
	@Test
	public void testUaggOuterChainSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testUaggOuterChainSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	
	// GreaterThanEquals Uagg ColumnSums -- MR
	@Test
	public void testGreaterEqualsUaggOuterChainSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	
	// Greater Uagg ColumnSums -- MR
	@Test
	public void testGreaterUaggOuterChainSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testGreaterUaggOuterChainSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	
	// LessThanEquals Uagg ColumnSums -- MR
	@Test
	public void testLessEqualsUaggOuterChainSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	
	// Equals Uagg ColumnSums -- MR
	@Test
	public void testEqualsUaggOuterChainSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testEqualsUaggOuterChainSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	
	// NotEquals Uagg ColumnSums -- MR
	@Test
	public void testNotEqualsUaggOuterChainSumsSingleDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainSumsSingleSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainSumsMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainSumsMultiSparseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, true, SumType.SUM_ALL, false, ExecType.MR);
	}
	
	
	// Less Uagg Sums -- SP
	@Test
	public void testLessUaggOuterChainSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.LESS, true, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, true, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessUaggOuterChainSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	// GreaterThanEquals Uagg Sums -- SP
	@Test
	public void testGreaterEqualsUaggOuterChainSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, true, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	
	// Greater Uagg Sums -- SP
	@Test
	public void testGreaterUaggOuterChainSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.GREATER, true, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, true, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testGreaterUaggOuterChainSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER, false, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}

	
	// LessEquals Uagg Sums -- SP
	@Test
	public void testLessEqualsUaggOuterChainSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, true, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testLessEqualsUaggOuterChainSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS_EQUALS, false, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	

	// Equals Uagg Sums -- SP
	@Test
	public void testEqualsUaggOuterChainSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.EQUALS, true, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, true, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testEqualsUaggOuterChainSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.EQUALS, false, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	


	// NotEquals Uagg Sums -- SP
	@Test
	public void testNotEqualsUaggOuterChainSumsSingleDenseSP() 
	{
		 runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainSumsSingleSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, true, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainSumsMultiDenseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, false, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	@Test
	public void testNotEqualsUaggOuterChainSumsMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.NOT_EQUALS, false, true, SumType.SUM_ALL, false, ExecType.SPARK);
	}
	
	

	// Empty Block Sums All (Data with 0.0 values)
	
	@Test
	public void testLessUaggOuterChainSumsEmptyMultiDenseMR() 
	{
		runBinUaggTest(TEST_NAME1, Type.LESS, false, false, SumType.SUM_ALL, true, ExecType.MR);
	}
	
	@Test
	public void testGreaterEqualsUaggOuterChainSumsEmptyMultiSparseSP() 
	{
		runBinUaggTest(TEST_NAME1, Type.GREATER_EQUALS, false, true, SumType.SUM_ALL, true, ExecType.SPARK);
	}
	

	/**
	 * 
	 * @param sparseM1
	 * @param sparseM2
	 * @param instType
	 */
	private void runBinUaggTest( String testname, Type type, boolean singleBlock, boolean sparse, SumType sumType, boolean bEmptyBlock, ExecType instType)
	{
		//rtplatform for MR
		RUNTIME_PLATFORM platformOld = rtplatform;
		switch( instType ){
			case MR: rtplatform = RUNTIME_PLATFORM.HADOOP; break;
			case SPARK: rtplatform = RUNTIME_PLATFORM.SPARK; break;
			default: rtplatform = RUNTIME_PLATFORM.HYBRID; break;
		}
	
		boolean sparkConfigOld = DMLScript.USE_LOCAL_SPARK_CONFIG;
		if( rtplatform == RUNTIME_PLATFORM.SPARK )
			DMLScript.USE_LOCAL_SPARK_CONFIG = true;

		try
		{
			String TEST_NAME = testname;
			TestConfiguration config = getTestConfiguration(TEST_NAME);
			
			/* This is for running the junit test the new way, i.e., construct the arguments directly */

			String suffix = "";
			
			switch (type) {
				case GREATER:
					suffix = "Greater";
					break;
				case LESS:
					suffix = "";
					break;
				case EQUALS:
					suffix = "Equals";
					break;
				case NOT_EQUALS:
					suffix = "NotEquals";
					break;
				case GREATER_EQUALS:
					suffix = "GreaterEquals";
					break;
				case LESS_EQUALS:
					suffix = "LessEquals";
					break;
			}

			String strSumTypeSuffix = null; 
			switch (sumType) {
				case ROW_SUM:
					strSumTypeSuffix = new String("");
					break;
				case COL_SUM:
					strSumTypeSuffix = new String("ColSums");
					break;
				case SUM_ALL:
					strSumTypeSuffix = new String("Sums");
					break;
			}
			
			String HOME = SCRIPT_DIR + TEST_DIR;			
			fullDMLScriptName = HOME + TEST_NAME + suffix + strSumTypeSuffix + ".dml";
			programArgs = new String[]{"-explain","-args", HOME + INPUT_DIR + "A",
					                            HOME + INPUT_DIR + "B",
					                            HOME + OUTPUT_DIR + "C"};
			fullRScriptName = HOME + TEST_NAME + suffix + strSumTypeSuffix +".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + 
			       HOME + INPUT_DIR + " " + HOME + EXPECTED_DIR;
			
			loadTestConfiguration(config);
	
			double dAMinVal = -1, dAMaxVal = 1, dBMinVal = -1, dBMaxVal = 1;
			
			if(bEmptyBlock)
				if(sumType == SumType.ROW_SUM) {
					dAMinVal = 0;
					dAMaxVal = 0;
				} else {
					dBMinVal = 0;
					dBMaxVal = 0;
				}
				
					
			//generate actual datasets
			double[][] A = getRandomMatrix(rows, 1, dAMinVal, dAMaxVal, sparse?sparsity2:sparsity1, 235);
			writeInputMatrixWithMTD("A", A, true);
			double[][] B = getRandomMatrix(1, singleBlock?cols1:cols2, dBMinVal, dBMaxVal, sparse?sparsity2:sparsity1, 124);
			writeInputMatrixWithMTD("B", B, true);
			
			runTest(true, false, null, -1); 
			runRScript(true); 
			
			//compare matrices 
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromHDFS("C");
			HashMap<CellIndex, Double> rfile  = readRMatrixFromFS("C");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "Stat-DML", "Stat-R");
			
			if(sumType == SumType.ROW_SUM)
				checkDMLMetaDataFile("C", new MatrixCharacteristics(rows,1,1,1)); //rowsums
			else if(sumType == SumType.COL_SUM)
				checkDMLMetaDataFile("C", new MatrixCharacteristics(1,singleBlock?cols1:cols2,1,1)); //colsums
			if(sumType == SumType.SUM_ALL)
				checkDMLMetaDataFile("C", new MatrixCharacteristics(1,1,1,1)); //sums
			
			//check compiled/executed jobs
			if( rtplatform != RUNTIME_PLATFORM.SPARK ) {
				int expectedNumCompiled = 2; //reblock+gmr if uaggouterchain; otherwise 3
				if(sumType == SumType.SUM_ALL)
					expectedNumCompiled = 3;  // scaler to matrix conversion.
				int expectedNumExecuted = expectedNumCompiled; 
				checkNumCompiledMRJobs(expectedNumCompiled); 
				checkNumExecutedMRJobs(expectedNumExecuted); 	
			}
		}
		finally
		{
			rtplatform = platformOld;
			DMLScript.USE_LOCAL_SPARK_CONFIG = sparkConfigOld;
		}
	}

}