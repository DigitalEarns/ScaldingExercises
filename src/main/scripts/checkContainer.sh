#! /bin/bash

#
#  Checks if the container is ready to be used
#
#  It might take a while for all HDFS and MAPRED services to be up and running
#
interval=10
counter=0

while [[ true ]]
do
  hadoop fs -ls /
  hdfsCheck=$(hadoop fs -ls / | wc -l)
  if [[ $hdfsCheck -gt 1 ]]; then
    echo "Container HDFS is ready to use... would wait for some more time to start mapred services"
    sleep 60
    exit 0
  else
    echo "container not ready yet, waiting..."
    sleep $interval
    counter=$(($counter + 1))
    if [[ $counter -gt 10 ]]; then
      echo "Container does not seem to be ready yet, maybe failed to start. need to exit..."
      exit -1
    fi
  fi
done