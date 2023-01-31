#!/bin/bash
jar_name=`ls *.jar`
pwd=`pwd`
nohup java -jar $pwd/$jar_name >> $pwd/logs.log 2>&1 &
tail -f ./logs.log
