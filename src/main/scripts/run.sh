#! /bin/bash

export jar=/var/demo/target/scala-2.11/exampleApp.jar
export HADOOP_CLASSPATH=$jar

echo "================="
echo "Loading Test Data"

hadoop fs -mkdir /tmp/input
hadoop fs -put /var/demo/src/test/resources/input/input1 /tmp/input/

echo "Running Job"
echo "==========="

hadoop jar $jar com.twitter.scalding.Tool \
  com.scalding.examples.Example1 \
  --input /tmp/input/ \
  --output /tmp/output \
  --hdfs

if [[ $? -eq 0 ]]; then
  echo "Job ran successfully"
  exit 0
else
  echo "Job Failed"
  exit -1
fi