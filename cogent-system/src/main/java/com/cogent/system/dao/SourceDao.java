package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.route.SourceDO;
import com.cogent.system.mapper.RouteMapper;
import com.cogent.system.mapper.SourceMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class SourceDao extends ServiceImpl<SourceMapper, SourceDO> {

    @Resource
    private RouteMapper routeMapper;
    @Resource
    private SourceMapper sourceMapper;


    public void deleteByRouteId(Integer id) {

    }

    public SourceDO selectByName(String name) {
        LambdaQueryWrapper<SourceDO> query = Wrappers.lambdaQuery();
        query.eq(SourceDO::getSourceName, name);
        return sourceMapper.selectOne(query);
    }

    public void deleteByName(String sn) {
        LambdaQueryWrapper<SourceDO> query = Wrappers.lambdaQuery();
        query.eq(SourceDO::getSourceName, sn);
        sourceMapper.delete(query);
    }

    public void updateByName(SourceDO sourceDO) {
        LambdaUpdateWrapper<SourceDO> update = Wrappers.lambdaUpdate();
        update.eq(SourceDO::getSourceName, sourceDO.getSourceName());
        sourceMapper.update(sourceDO, update);
    }
}
