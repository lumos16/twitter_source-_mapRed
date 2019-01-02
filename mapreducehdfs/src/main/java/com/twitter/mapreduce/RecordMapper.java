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
		String polarity = record.toString();
		int pos = polarity.indexOf('\\');
		Float compound = Float.valueOf(polarity.substring(0, pos));
		Text result = neutral;
		if(compound < 0.7 && compound >= -0.7) {}
		else if(compound >= 0.7)
			result = positive;
		else if(compound <= -0.7)
			result = negative;
		output.collect(result, intObj);
	}

}
