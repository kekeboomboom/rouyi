package com.cogent.system.service;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.system.domain.DO.fileUpload.MinioObjectDO;
import com.cogent.system.domain.vo.fileUpload.ListObjectVO;

import java.util.List;

public interface IFileUploadService {
    void putObject(String key, JSONObject jsonObject);

    void deleteObject(String key);

    List<ListObjectVO> list(String bucket, String name, String device, String format, Long startTime, Long endTime);

    int total(String bucket, String name, String device, String format, Long startTime, Long endTime);

    MinioObjectDO getObjectInfo(String bucket, String fullPathName);
}
