package com.ibm.bi.dml.runtime.instructions.MRInstructions;

import com.ibm.bi.dml.hops.Hops.DataGenMethod;
import com.ibm.bi.dml.runtime.instructions.Instruction;
import com.ibm.bi.dml.runtime.instructions.InstructionUtils;
import com.ibm.bi.dml.runtime.matrix.io.MatrixValue;
import com.ibm.bi.dml.runtime.matrix.mapred.CachedValueMap;
import com.ibm.bi.dml.runtime.matrix.mapred.IndexedMatrixValue;
import com.ibm.bi.dml.runtime.matrix.operators.Operator;
import com.ibm.bi.dml.utils.DMLRuntimeException;
import com.ibm.bi.dml.utils.DMLUnsupportedOperationException;


public class SeqInstruction extends DataGenMRInstruction
{
	public double fromValue;
	public double toValue;
	public double incrValue;
	
	public SeqInstruction ( Operator op, byte in, byte out, long rows, long cols, int rpb, int cpb, double fromValue, double toValue,
				double incrValue, String baseDir, String istr ) {
		super(op, DataGenMethod.SEQ, in, out, rows, cols, rpb, cpb, baseDir);
		mrtype = MRINSTRUCTION_TYPE.Seq;
		this.fromValue = fromValue;
		this.toValue = toValue;
		this.incrValue = incrValue;
		instString = istr;
	}
	
	public static Instruction parseInstruction(String str) throws DMLRuntimeException 
	{
		InstructionUtils.checkNumFields ( str, 10 );

		String[] parts = InstructionUtils.getInstructionParts ( str );
		
		Operator op = null;
		byte input = Byte.parseByte(parts[1]);
		byte output = Byte.parseByte(parts[2]);
		long rows = Double.valueOf(parts[3]).longValue();
		long cols = Double.valueOf(parts[4]).longValue();
		int rpb = Integer.parseInt(parts[5]);
		int cpb = Integer.parseInt(parts[6]);
		double fromValue = Double.parseDouble(parts[7]);
		double toValue = Double.parseDouble(parts[8]);
		double incrValue = Double.parseDouble(parts[9]);
		String baseDir = parts[10];
		
		return new SeqInstruction(op, input, output, rows, cols, rpb, cpb, fromValue, toValue, incrValue, baseDir, str);
	}

	@Override
	public void processInstruction(Class<? extends MatrixValue> valueClass,
			CachedValueMap cachedValues, IndexedMatrixValue tempValue,
			IndexedMatrixValue zeroInput, int blockRowFactor, int blockColFactor)
			throws DMLUnsupportedOperationException, DMLRuntimeException {
		// TODO Auto-generated method stub
		
	}
	
}