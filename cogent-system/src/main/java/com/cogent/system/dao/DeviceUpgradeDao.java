package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.devUpgrade.DeviceUpgradeDO;
import com.cogent.system.mapper.DeviceUpgradeMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/17
 * {@code @description:}
 */
@Repository
public class DeviceUpgradeDao extends ServiceImpl<DeviceUpgradeMapper, DeviceUpgradeDO> {

    @Resource
    private DeviceUpgradeMapper deviceUpgradeMapper;


    public List<DeviceUpgradeDO> selectList(String versionNum, String devType) {
        LambdaQueryWrapper<DeviceUpgradeDO> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(versionNum)) {
            query = query.like(DeviceUpgradeDO::getVersionNum, versionNum);
        }
        if (StringUtils.isNotEmpty(devType)) {
            query = query.like(DeviceUpgradeDO::getDevType, devType);
        }
        return deviceUpgradeMapper.selectList(query);
    }

    public DeviceUpgradeDO getLatestVersion(String devType) {
        LambdaQueryWrapper<DeviceUpgradeDO> query = Wrappers.lambdaQuery();
        ArrayList<SFunction<DeviceUpgradeDO, ?>> versionQuery = new ArrayList<>(3);
        versionQuery.add(DeviceUpgradeDO::getMajorVersion);
        versionQuery.add(DeviceUpgradeDO::getSubVersion);
        versionQuery.add(DeviceUpgradeDO::getStageVersion);
        query.orderByDesc(versionQuery)
                .eq(DeviceUpgradeDO::getEnable, true)
                .eq(DeviceUpgradeDO::getDevType, devType)
                .last("limit 1");
        return deviceUpgradeMapper.selectOne(query);
    }
}
