package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.dao.MinioObjectDao;
import com.cogent.system.domain.DO.bag.BagDO;
import com.cogent.system.domain.DO.fileUpload.MinioObjectDO;
import com.cogent.system.domain.vo.fileUpload.ListObjectVO;
import com.cogent.system.mapper.BagMapper;
import com.cogent.system.service.IFileUploadService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/28
 * {@code @description:}
 */
@Slf4j
@Service
public class FileUploadService implements IFileUploadService {

    @Resource
    private MinioObjectDao minioObjectDao;
    @Resource
    private BagMapper bagMapper;


    @SneakyThrows
    @Override
    public void putObject(String key, JSONObject jsonObject) {
        JSONObject record = jsonObject.getJSONArray("Records").getJSONObject(0);
        // docker 中的时间少八个小时下，即使宿主机设置了东八区，但是docker里面并不是这样
        //  下面的写法不可靠，如果docker容器时区不差八小时了，那么下面这个直接加八小时的操作就有问题了
//        String eventTime = record.getString("eventTime");
//        Date date = DateUtils.parseDate(eventTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        Date dateAddEightHour = DateUtils.addHours(date, 8);
        Integer size = record.getJSONObject("s3").getJSONObject("object").getInteger("size");

        MinioObjectDO aDo = new MinioObjectDO();
        aDo.setKeyName(key);
        aDo.setSize(size);
        aDo.setUploadTime(new Date());
        String[] split = StringUtils.split(key, "/");
        aDo.setObjectName(split[split.length - 1]);
        minioObjectDao.save(aDo);
    }

    @Override
    public void deleteObject(String key) {
        minioObjectDao.removeByKeyName(key);
    }

    @Override
    public List<ListObjectVO> list(String bucket, String name, String device, String format, Long startTime, Long endTime) {
        Date start = null;
        Date end = null;
        if (startTime != null) {
            start = new Date(startTime);
        }
        if (endTime != null) {
            end = new Date(endTime);
        }
        List<MinioObjectDO> minioObjectDOS = minioObjectDao.selectList(bucket, name, device, format, start, end);
        ArrayList<ListObjectVO> res = new ArrayList<>(minioObjectDOS.size());
        for (MinioObjectDO aDo : minioObjectDOS) {
            ListObjectVO vo = new ListObjectVO();
            String objectName = aDo.getObjectName();
            String[] objectSplit = StringUtils.split(objectName, "_");
            if (objectSplit.length == 1) {
                vo.setSn("");
                vo.setDevName("");
            } else {
                String sn = objectSplit[0];
                // So far at least, device sn length is 11.
                if (sn.length() == 11) {
                    vo.setSn(sn);
                    BagDO bagBySN = bagMapper.getBagBySN(sn);
                    if (bagBySN == null) {
                        vo.setDevName("");
                    } else {
                        vo.setDevName(bagBySN.getDevName());
                    }
                } else {
                    vo.setSn("");
                    vo.setDevName("");
                }
            }
            String[] objectFormat = StringUtils.split(objectName, ".");
            // 如果根据 . 做分割，得到的长度只有1，那么则认为此文件没有格式
            // 当然，仅仅根据 . 去判断文件类型，肯定不严谨，但暂时没想到其他方法
            // 也可以之规定几种格式，比如mp4 jpg 等等，除此之外的一概不识别
            if (objectFormat.length == 1) {
                vo.setFormat("");
            } else {
                vo.setFormat(objectFormat[objectFormat.length - 1]);
            }
            String keyName = aDo.getKeyName();
            String fullPath = keyName.substring(keyName.indexOf("/") + 1);
            vo.setFullPath(fullPath);
            vo.setName(objectName);
            vo.setSize(aDo.getSize());
            vo.setUploadTime(aDo.getUploadTime());
            res.add(vo);
        }
        return res;
    }

    @Override
    public int total(String bucket, String name, String device, String format, Long startTime, Long endTime) {
        Date start = null;
        Date end = null;
        if (startTime != null) {
            start = new Date(startTime);
        }
        if (endTime != null) {
            end = new Date(endTime);
        }
        return minioObjectDao.countList(bucket, name, device, format, start, end);
    }

    @Override
    public MinioObjectDO getObjectInfo(String bucket, String fullPathName) {
        return minioObjectDao.getByKeyName(bucket + "/" + fullPathName);
    }
}
