package com.twitter.mapreduce;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class RowCount extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new RowCount(), args);
		System.exit(exitCode);
	}

	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf(getClass());
		conf.setJobName("---twitter data sentimental analysis map reduce---");
		conf.setJarByClass(getClass());
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		conf.setMapperClass(RecordMapper.class);
		conf.setReducerClass(RecordCountReducer.class);
		conf.setCombinerClass(RecordCountReducer.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		JobClient.runJob(conf);
		return 0;
	}

}
