package com.cogent.system.dao;

import com.cogent.system.domain.DO.map.DeviceGPSCurDO;
import com.cogent.system.mapper.DeviceGpsCurMapper;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Repository
public class DeviceGpsCurDao extends ServiceImpl<DeviceGpsCurMapper, DeviceGPSCurDO>{

    @Resource
    private DeviceGpsCurMapper deviceGpsCurMapper;


}
