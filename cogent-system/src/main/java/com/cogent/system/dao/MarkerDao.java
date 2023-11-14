package com.cogent.system.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.map.MarkerDO;
import com.cogent.system.mapper.MarkerMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Repository
public class MarkerDao extends ServiceImpl<MarkerMapper, MarkerDO> {

    @Resource
    private MarkerMapper markerMapper;


}
