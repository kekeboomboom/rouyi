#!/bin/sh

# get java version
#java_version=$(java -version 2>&1 | head -1 | awk '{print $3}' | awk -F '.' '{print $2}')

tar -zxvf jre-8u381-linux-x64.tar.gz

mkdir -p /usr/local/jdk1.8

sudo mv jdk1.8.0_231  /usr/local/jdk1.8

{
  echo "export JAVA_HOME=/usr/local/jdk1.8"
  echo "export JRE_HOME=${JAVA_HOME}/jre"
  echo "export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib"
  echo "export PATH=.:${JAVA_HOME}/bin:$PATH"
} >> /etc/profile


sudo sh -c  'source /etc/profile'