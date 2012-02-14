package dml.meta;
//<Arun>
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import dml.runtime.matrix.io.MatrixBlock;
import dml.runtime.matrix.io.MatrixIndexes;

public abstract class BlockJoinMapperMethodIDTable {
	MatrixIndexes mi = new MatrixIndexes() ;
	PartitionParams pp ;
	MultipleOutputs multipleOutputs ;
	
	public BlockJoinMapperMethodIDTable () {
		mi = null;
		pp = null;
	}
	
	public BlockJoinMapperMethodIDTable(PartitionParams pp, MultipleOutputs multipleOutputs) {
		this.pp = pp ;
		this.multipleOutputs = multipleOutputs ;
	}
	
	abstract void execute(LongWritable key, WritableLongArray value, Reporter reporter, OutputCollector out) 
	throws IOException ;
		
	public MatrixBlock getSubRowBlock(MatrixBlock blk, int rownum) {
		int ncols = blk.getNumColumns();
		MatrixBlock thissubrowblk = new MatrixBlock(1, ncols, true);	//presume sparse
		//populate subrowblock
		for(int c=0; c<ncols; c++) {
			thissubrowblk.setValue(rownum, c, blk.getValue(rownum, c));
		}
		thissubrowblk.examSparsity();	//refactor based on sparsity
		return thissubrowblk;
	}
}
//</Arun>