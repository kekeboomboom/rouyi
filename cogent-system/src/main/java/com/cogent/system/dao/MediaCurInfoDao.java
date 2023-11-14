package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.common.LiveState;
import com.cogent.system.domain.DO.bag.MediaCurrentInfoDO;
import com.cogent.system.mapper.MediaCurrentInfoMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-14 15:23
 */
@Repository
public class MediaCurInfoDao extends ServiceImpl<MediaCurrentInfoMapper, MediaCurrentInfoDO> {
    @Resource
    private MediaCurrentInfoMapper mediaCurrentInfoMapper;

    public MediaCurrentInfoDO selectBySn(String sn) {
        LambdaQueryWrapper<MediaCurrentInfoDO> query = Wrappers.lambdaQuery();
        query.eq(MediaCurrentInfoDO::getSn, sn);
        return mediaCurrentInfoMapper.selectOne(query);
    }

    public void updateBySn(MediaCurrentInfoDO mediaCurrentInfoDO) {
        LambdaUpdateWrapper<MediaCurrentInfoDO> update = Wrappers.lambdaUpdate();
        update.eq(MediaCurrentInfoDO::getSn, mediaCurrentInfoDO.getSn());
        mediaCurrentInfoMapper.update(mediaCurrentInfoDO, update);
    }

    public List<MediaCurrentInfoDO> selectLivingDevices() {
        LambdaQueryWrapper<MediaCurrentInfoDO> query = Wrappers.lambdaQuery();
        query.eq(MediaCurrentInfoDO::getStaLiveState, LiveState.LIVE.getCode());
        return mediaCurrentInfoMapper.selectList(query);
    }
}
