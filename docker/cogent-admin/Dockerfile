FROM adoptopenjdk/openjdk8-openj9:alpine-slim

LABEL cogent.cogent-admin.image.authors="keboom"

COPY . /opt/cogent-admin

WORKDIR /opt/cogent-admin

# 时区设置
RUN echo "Asia/shanghai" > /etc/timezone

ENV JAVA_OPTS="-server -Xms512m -Xmx512m"

EXPOSE 8080

ENTRYPOINT exec java ${JAVA_OPTS} -jar cogent-admin.jar