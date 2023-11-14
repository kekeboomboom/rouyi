#!/bin/sh

# 安装docker 离线安装
# 安装 docker 镜像
mkdir -p /opt/java-server/dockerBackup
cp mysql-backup.tar /opt/java-server/dockerBackup
cp redis-backup.tar /opt/java-server/dockerBackup
cp docker-compose.yml /opt/java-server/dockerBackup

# load docker images
docker load < mysql-backup.tar
docker load < redis-backup.tar

# docker start mysql redis
cd /opt/java-server/dockerBackup || exit
docker compose up -d mysql redis

# install jre 8
sudo apt update
apt install openjdk-8-jre-headless

cp cogent-admin.tar.gz /opt/java-server/
cp mediakit.tar.gz /opt/java-server/

cd /opt/java-server || exit
tar -xzvf cogent-admin.tar.gz
tar -xzvf mediakit.tar.gz

# start java service
cd /opt/java-server/cogent-admin || exit
./ry.sh start

# start mediakit
cd ..
cd mediakit || exit
./refreshConf.sh
