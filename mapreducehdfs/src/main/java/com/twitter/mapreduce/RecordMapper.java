package com.twitter.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class RecordMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable> {
	
	static IntWritable intObj = new IntWritable(1);
	static Text positive = new Text(Constants.POSITIVE);
	static Text negative = new Text(Constants.NEGATIVE);
	static Text neutral = new Text(Constants.NEUTRAL);
	
	public void map(LongWritable key, Text record, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String[] polarity = record.toString().split(":");
		Float compound = Float.valueOf(polarity[0]);
		Text result = neutral;
		if(compound < 0.5 && compound >= -0.5) {}
		else if(compound >= 0.5)
			result = positive;
		else if(compound <= -0.5)
			result = negative;
		output.collect(result, intObj);
	}

}
