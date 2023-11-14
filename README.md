# rouyi

首先声明这是由[rouyi](https://gitee.com/y_project/RuoYi)项目改造过来的，请无视项目中cogent等字段！！！

此项目使用者只需关注比rouyi项目新增的部分即可。

## 日志

### 接口出入日志

request json和response json日志打印。

cogent-admin模块下com.cogent.web.aspect.ControllerLogAspect

### 日志过滤

某些接口打印大量设备状态数据日志，这其实没必要打印，量太大了，且重复。

cogent-admin模块下com.cogent.web.aspect.FilterLog

## SSE

创建一个群组可以进行推拉音频流。由于不像聊天室频繁的推拉，我们群组主要是推，因此使用SSE实现。

cogent-admin模块com.cogent.web.controller.system.CallGroupController

cogent-system模块com.cogent.system.service.impl.CallGroupServiceImpl

## 文件上传下载

上传使用hutools，创建目录，解压缩，读取yml文件，校验md5值等操作。

下载支持分片下载。

cogent-admin模块com.cogent.web.controller.system.DeviceUpgradeController

## MinIO

调用MinIO的Java SDK实现上传下载查询删除，event hook等。

**注意**：MinIO的Java SDK或者说MinIO的提供的SDK不支持文件的断点续传等操作，如果要实现MinIO的断点续传功能，可以使用Amazon S3的SDK，参考项目https://gitee.com/Gary2016/minio-upload

关于分片上传（注意可不是断点续传！！）有一种方式，https://yunyanchengyu.blog.csdn.net/article/details/123429986。这种方式我试过了，他会创建一个文件夹并将文件名修改掉，并不好用。并且当你断开网络，停止上传后，然后再次点击上传他并不会从已经上传的位置开始接着上传。

cogent-amdin模块com.cogent.web.controller.system.FileUploadController

## ZLMediakit

关于Mediakit录像，通过on_stream_changed hook来监听流注册事件，再根据业务逻辑去判断是否开启此流录像。通过监听on_record_mp4事件，来获取录像截图并存储录像视频文件相关信息。

监听on_stream_none_reader事件来实现按需推流

cogent-amdin模块com.cogent.web.controller.system.MediakitHookController

cogent-amdin模块com.cogent.web.controller.system.RecordController

## liquibase

cogentadmin模块resouces---db.changelog目录查看liquibase整合

## Mybatis-plus

cogent-system模块com.cogent.system.dao
