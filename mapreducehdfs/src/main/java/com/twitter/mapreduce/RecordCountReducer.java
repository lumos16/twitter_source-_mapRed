package com.twitter.mapreduce;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class RecordCountReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

	public void reduce(Text key, Iterator<IntWritable> records, OutputCollector<Text, IntWritable> output,
			Reporter reporter) throws IOException {
		int sum =0;
		while (records.hasNext()) {
			sum += records.next().get();
		}
		output.collect(key, new IntWritable(sum));		
	}

}
