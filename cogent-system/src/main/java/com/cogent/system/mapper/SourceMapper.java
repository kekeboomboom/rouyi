package com.cogent.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cogent.system.domain.DO.route.SourceDO;

import java.util.List;

public interface SourceMapper extends BaseMapper<SourceDO> {

    int insertSource(SourceDO sourceDO);

    int deleteSourceByIds(List<Long> sourceIds);

    int updateSource(SourceDO sourceDO);

    SourceDO selectSourceById(Long id);

    List<SourceDO> selectSourceByIds(List<Long> ids);

    List<SourceDO> selectSourceList(SourceDO sourceDO);

    int insertOrUpdateSource(SourceDO source);
}
