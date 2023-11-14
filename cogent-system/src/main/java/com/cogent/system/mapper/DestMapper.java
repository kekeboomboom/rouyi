package com.cogent.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cogent.system.domain.DO.route.DestDO;

import java.util.List;

public interface DestMapper extends BaseMapper<DestDO> {

    long insertDest(DestDO destDO);

    int deleteDestByIds(List<Long> ids);

    int updateDest(DestDO destDO);

    int updateDestByName(DestDO destDO);

    DestDO selectDestById(Long id);

    List<DestDO> selectDestList(DestDO destDO);

    List<DestDO> selectDestByIds(List<Long> dests);

    int updateDestRouteReleaseByDestIds(List<Long> destIds);

    int insertOrUpdateDest(DestDO destDO);
}
