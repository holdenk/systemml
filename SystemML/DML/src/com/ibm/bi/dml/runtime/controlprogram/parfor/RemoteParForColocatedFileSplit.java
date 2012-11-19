package com.ibm.bi.dml.runtime.controlprogram.parfor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.NLineInputFormat;

import com.ibm.bi.dml.runtime.controlprogram.parfor.Task.TaskType;
import com.ibm.bi.dml.runtime.instructions.CPInstructions.IntObject;


/**
 * Wrapper for arbitrary input split in order to attach our co-location information.
 * 
 */
public class RemoteParForColocatedFileSplit extends FileSplit
{
	private String _fname = null;
	private int    _blen  = 1;
	
	/**
	 * Required because hadoop explicitly accesses this private constructor
	 * via reflection (since private not inherited from FileSplit).
	 */
	private RemoteParForColocatedFileSplit()
	{
		super( null, -1, -1, new String[]{} );	
	}
	
	public RemoteParForColocatedFileSplit( FileSplit split, String fname, int blen ) 
		throws IOException 
	{
		super( split.getPath(), split.getStart(), split.getLength(), split.getLocations() );
		
		_fname = fname;
		_blen = blen;
	}

	/**
	 * Get the list of hostnames where the input split is located.
	 */
	@Override
	public String[] getLocations() throws IOException
	{
		//Timing time = new Timing();
		//time.start();
		
		JobConf job = new JobConf();
		
		//read task string
		LongWritable key = new LongWritable();
		Text value = new Text();
		RecordReader<LongWritable,Text> reader = new NLineInputFormat().getRecordReader(this, new JobConf(), Reporter.NULL);
		reader.next(key, value);
		reader.close();
		
		//parse task
		Task t = Task.parseCompactString( value.toString() );
		
		//get all locations
		HashMap<String, Integer> hosts = new HashMap<String,Integer>();
		
		if( t.getType() == TaskType.SET )
		{
			for( IntObject val : t.getIterations() )
			{
				String fname = _fname+"/"+String.valueOf((val.getIntValue()/_blen));
				FileSystem fs = FileSystem.get(job);
				FileStatus status = fs.getFileStatus(new Path(fname)); 
				BlockLocation[] tmp1 = fs.getFileBlockLocations(status, 0, status.getLen());
				for( BlockLocation bl : tmp1 )
					countHosts(hosts, bl.getHosts());
			}
		}
		else //TaskType.RANGE
		{
			int lFrom  = t.getIterations().get(0).getIntValue();
			int lTo    = t.getIterations().get(1).getIntValue();
			int lIncr  = t.getIterations().get(2).getIntValue();				
			for( int i=lFrom; i<=lTo; i+=lIncr )
			{
				String fname = _fname+"/"+String.valueOf( (i/_blen) );
				FileSystem fs = FileSystem.get(job);
				FileStatus status = fs.getFileStatus(new Path(fname)); 
				BlockLocation[] tmp1 = fs.getFileBlockLocations(status, 0, status.getLen());
				for( BlockLocation bl : tmp1 )
					countHosts(hosts, bl.getHosts());
			}
		}

		//System.out.println("Get locations in "+time.stop()+"ms.");
		
		//majority consensus on top host
		return new String[]{getTopHost(hosts)};
	}

	
	/**
	 * 
	 * @param hosts
	 * @param names
	 */
	private void countHosts( HashMap<String,Integer> hosts, String[] names )
	{
		for( String name : names )
		{
			Integer tmp = hosts.get(name);
			if( tmp != null )
				hosts.put(name, tmp+1);
			else
				hosts.put(name, 1);
		}
	}
	
	/**
	 * 
	 * @param hosts
	 * @return
	 */
	private String getTopHost( HashMap<String,Integer> hosts )
	{
		int max = Integer.MIN_VALUE;
		String maxName = null;
		
		for( Entry<String,Integer> e : hosts.entrySet() )
			if( e.getValue() > max )
			{
				max = e.getValue();
				maxName = e.getKey();
			}
		
		return maxName;
	}
}