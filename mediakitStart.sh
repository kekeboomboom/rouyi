#!/bin/sh



start()
{
  #./MediaServer -h
  #以守护进程模式启动
  #./MediaServer -d &
  #./MediaServer -s default.pem -c config.ini -d > /dev/null 2>&1 &
    PID=`ps -ef |grep MediaServer|grep -v grep|awk '{print $2}'`

	if [ x"$PID" != x"" ]; then
	    echo "$AppName is running..."
	else
		nohup java $JVM_OPTS -jar $AppName --interface=$interface --GB_ENABLE=$GB_ENABLE --HOST_IP=$HOST_IP > /dev/null 2>&1 &
		echo "Start $AppName success..."
	fi
}

stop()
{

  PID=$(ps -ef |grep MediaServer|grep -v grep|tail -1 | awk '{print $2}')
  kill $PID
  PID=$(ps -ef |grep MediaServer|grep -v grep|tail -1 | awk '{print $2}')
  kill $PID



    echo "Stop $AppName"

	PID=""
	query(){
		PID=`ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}'`
	}

	query
	if [ x"$PID" != x"" ]; then
		kill -TERM $PID
		echo "$AppName (pid:$PID) exiting..."
		while [ x"$PID" != x"" ]
		do
			sleep 1
			query
		done
		echo "$AppName exited."
	else
		echo "$AppName already stopped."
	fi
}
