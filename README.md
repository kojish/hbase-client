This is an HBase client example for both HBase 1.x and 2.x. It is tested with HBase version 1.1.2 (HDInsight 3.6) and 2.1.6 (HDInsight 4.0). There are two source codes that are HBaseClientv1.java for HBase 1.x and HBaseClientv2.java for HBase 2.x. The both applications use hbase java client library from org.apache.hbase, and you can change the library's version in the pom.xml apropriately that works for your cluster version.

You should install the following before you begin:
1. JDK 1.8.x
2. Maven 3.x
3. HBase cluster needs to be up and running if you want to run the example application.
Note: the application needs to run on a head node of HBase cluster or a VM which resides in the same VNET to get an access to the cluster.

Here is how to compile and run the example.
```command line
>git clone https://github.com/kojish/hbase-client.git
>cd hbase-client
# Set the proper zookeeper quorum to ZOOKEEPER_QUORUM in the code.
# The value of ZOOKEEPER_QUORUM can be obtained from HBase's configuration screen in Ambari or hbase-site.xml.
>mvn package
>java -cp ./target/hbase-client-example-0.0.1-SNAPSHOT.jar mscs.HBaseClientv2
```
Then, you will see the following log.
```
log4j:WARN No appenders could be found for logger (org.apache.hadoop.metrics2.lib.MutableMetricsFactory).
log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
Creating new table... Done.
Row key: B1234-32-1494133008, Column Family: cp, Qualifier: a, Value : 9
Row key: B1234-32-1494133008, Column Family: cp, Qualifier: id, Value : XE3025
Row key: B1234-32-1494133008, Column Family: ct, Qualifier: a, Value : 29
Row key: B1234-32-1494133008, Column Family: ct, Qualifier: t, Value : 25
Row key: E3039-21-1494133010, Column Family: cp, Qualifier: a, Value : 14
Row key: E3039-21-1494133010, Column Family: cp, Qualifier: id, Value : MU8367
Row key: E3039-21-1494133010, Column Family: ct, Qualifier: a, Value : 16
Row key: E3039-21-1494133010, Column Family: ct, Qualifier: t, Value : 23
Row key: G4119-90-1494133009, Column Family: cp, Qualifier: a, Value : 11
Row key: G4119-90-1494133009, Column Family: cp, Qualifier: id, Value : RG8021
Row key: G4119-90-1494133009, Column Family: ct, Qualifier: a, Value : 32
Row key: G4119-90-1494133009, Column Family: ct, Qualifier: t, Value : 30
```
Note that I only got this to work on linux but it should work on Windows envifonment.
