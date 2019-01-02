A VADER sentimental analysis of twitter tweets by aggregating real time twitter tweets using Apache Flume and analyzing it through a map reduce processing of Apache Hadoop.

# Set up
Set up Hadoop and Flume environments with the given configuration files or use the cloudera CDH distribution. The jar files of the customized twitter tweets sourcer and the map reduce program is in their respective /config folders. Download the necessary twitter4j-core, twitter4j-stream, twitter4j-media-support, lucene-core and the flume tweets sources program jars with the desired appropriate versions and place them in the /lib folder of the apache flume directory.<br />
Run Hadoop and confirm that the namenode, datanode, resource manager and the node manager services are running by issuing the 'jps' command.<br /> 
Start your data aggregation by running the following flume command - <br/>
&nbsp;&nbsp; flume-ng agent -n TwitterAgent -f {path to your apache flume dir}/conf/flume.conf<br/>
The data must be aggregated and placed in your hadoop file systems. You can check it by running -<br />
&nbsp;&nbsp; hadoop fs -ls {path to your files in hadoop} <br />
Run the map reduce program to get the output with the following command -<br />
&nbsp;&nbsp; hadoop jar twitterMapReduce.jar com.twitter.mapreduce.RowCount {your hadoop input file path from the previous step}&nbsp;&nbsp; &nbsp;&nbsp;  {output file path to write to}
