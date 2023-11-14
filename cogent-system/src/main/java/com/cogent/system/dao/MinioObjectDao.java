package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.domain.DO.fileUpload.MinioObjectDO;
import com.cogent.system.mapper.MinioObjectMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/28
 * {@code @description:}
 */
@Repository
public class MinioObjectDao extends ServiceImpl<MinioObjectMapper, MinioObjectDO> {

    @Resource
    private MinioObjectMapper minioObjectMapper;

    public void removeByKeyName(String keyName) {
        LambdaQueryWrapper<MinioObjectDO> query = Wrappers.lambdaQuery();
        query.eq(MinioObjectDO::getKeyName, keyName);
        minioObjectMapper.delete(query);
    }

    public List<MinioObjectDO> selectList(String bucket, String name, String device, String format, Date start, Date end) {
        LambdaQueryWrapper<MinioObjectDO> query = Wrappers.lambdaQuery();
        query = query.likeRight(MinioObjectDO::getKeyName, bucket);
        if (StringUtils.isNotEmpty(name)) {
            query = query.like(MinioObjectDO::getObjectName, name);
        }
        if (StringUtils.isNotEmpty(device)) {
            query = query.like(MinioObjectDO::getObjectName, device);
        }
        if (StringUtils.isNotEmpty(format)) {
            query = query.likeLeft(MinioObjectDO::getObjectName, format);
        }
        if (start != null) {
            query = query.ge(MinioObjectDO::getUploadTime, start);
        }
        if (end != null) {
            query = query.le(MinioObjectDO::getUploadTime, end);
        }
        return minioObjectMapper.selectList(query);
    }

    public int countList(String bucket, String name, String device, String format, Date start, Date end) {
        LambdaQueryWrapper<MinioObjectDO> query = Wrappers.lambdaQuery();
        query = query.likeRight(MinioObjectDO::getKeyName, bucket);
        if (StringUtils.isNotEmpty(name)) {
            query = query.like(MinioObjectDO::getObjectName, name);
        }
        if (StringUtils.isNotEmpty(device)) {
            query = query.like(MinioObjectDO::getObjectName, device);
        }
        if (StringUtils.isNotEmpty(format)) {
            query = query.likeLeft(MinioObjectDO::getObjectName, format);
        }
        if (start != null) {
            query = query.ge(MinioObjectDO::getUploadTime, start);
        }
        if (end != null) {
            query = query.le(MinioObjectDO::getUploadTime, end);
        }
        return minioObjectMapper.selectCount(query).intValue();
    }

    public MinioObjectDO getByKeyName(String keyName) {
        LambdaQueryWrapper<MinioObjectDO> query = Wrappers.lambdaQuery();
        query.eq(MinioObjectDO::getKeyName, keyName);
        return minioObjectMapper.selectOne(query);
    }
}
