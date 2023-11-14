package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.map.GpsInfoDO;
import com.cogent.system.mapper.GpsInfoMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Repository
public class GpsInfoDao extends ServiceImpl<GpsInfoMapper, GpsInfoDO> {

    @Resource
    private GpsInfoMapper gpsInfoMapper;

    public List<GpsInfoDO> selectByTime(String sn, Long startTimestamp, Long endTimestamp) {
        LambdaQueryWrapper<GpsInfoDO> query = Wrappers.lambdaQuery();
        query.eq(GpsInfoDO::getSn, sn)
                .gt(GpsInfoDO::getCreateTime, new Date(startTimestamp))
                .lt(GpsInfoDO::getCreateTime, new Date(endTimestamp));
        return gpsInfoMapper.selectList(query);
    }
}
