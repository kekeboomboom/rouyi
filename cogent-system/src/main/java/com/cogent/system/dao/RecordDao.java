package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.record.RecordDO;
import com.cogent.system.mapper.RecordMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/24
 * {@code @description:}
 */
@Repository
public class RecordDao extends ServiceImpl<RecordMapper, RecordDO> {

    @Resource
    private RecordMapper recordMapper;

    public RecordDO selectByFilePath(String filePath) {
        LambdaQueryWrapper<RecordDO> query = Wrappers.lambdaQuery();
        query.eq(RecordDO::getFilePath, filePath);
        return recordMapper.selectOne(query);
    }

    public List<RecordDO> list(String sn, String name, Long startTime, Long endTime) {
        LambdaQueryWrapper<RecordDO> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sn)) {
            query = query.like(RecordDO::getStream, sn);
        }
        if (StringUtils.isNotEmpty(name)) {
            query = query.like(RecordDO::getFileAlias, name);
        }
        if (startTime != null) {
            query = query.ge(RecordDO::getStartTime, new Date(startTime));
        }
        if (endTime != null) {
            query = query.le(RecordDO::getStartTime, new Date(endTime));
        }
        query.orderByDesc(RecordDO::getStartTime);
        return recordMapper.selectList(query);
    }
}
