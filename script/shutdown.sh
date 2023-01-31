if [ $# -eq 1 ];
then
cd $1
fi
jar_name=`ls *.jar`
pwd=`pwd`
ps aux | grep java | grep $pwd/$jar_name | grep -v "grep" | awk '{print "kill -9 " $2}' | sh
