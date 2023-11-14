package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.bag.MediaHistoryInfoDO;
import com.cogent.system.mapper.MediaHistoryInfoMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @Author keboom
 * @Date 2023-06-06 17:07
 */
@Repository
public class MediaHistoryInfoDao extends ServiceImpl<MediaHistoryInfoMapper, MediaHistoryInfoDO> {

    @Resource
    private MediaHistoryInfoMapper mapper;

    public MediaHistoryInfoDO selectBySn(String sn) {
        LambdaQueryWrapper<MediaHistoryInfoDO> query = Wrappers.lambdaQuery();
        query.eq(MediaHistoryInfoDO::getSn, sn);
        return mapper.selectOne(query);
    }
}
