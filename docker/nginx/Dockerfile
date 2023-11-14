# 基础镜像
FROM nginx
# author
LABEL Author="keboom"

# 挂载目录
VOLUME /opt/java-server/nginx/html
# 创建目录
RUN mkdir -p /opt/java-server/nginx/html
# 指定路径
WORKDIR /opt/java-server/nginx/html
# 复制conf文件到路径
COPY ./conf/nginx.conf /etc/nginx/nginx.conf
# 复制html文件到路径
COPY ./html/dist /opt/java-server/nginx/html