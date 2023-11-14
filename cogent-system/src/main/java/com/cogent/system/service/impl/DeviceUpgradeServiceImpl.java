package com.cogent.system.service.impl;

import cn.hutool.core.io.FileUtil;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.bean.BeanUtils;
import com.cogent.system.dao.DeviceUpgradeDao;
import com.cogent.system.domain.DO.devUpgrade.DeviceUpgradeDO;
import com.cogent.system.domain.vo.devUpgrade.AddDevVerReq;
import com.cogent.system.domain.vo.devUpgrade.CheckUpgradeReq;
import com.cogent.system.domain.vo.devUpgrade.DeviceUpgradeListVO;
import com.cogent.system.domain.vo.devUpgrade.UpdateVerReq;
import com.cogent.system.service.IDeviceUpgradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/16
 * {@code @description:}
 */
@Slf4j
@Service
public class DeviceUpgradeServiceImpl implements IDeviceUpgradeService {

    @Resource
    private DeviceUpgradeDao deviceUpgradeDao;

    @Override
    public void addVersion(AddDevVerReq req) {
        DeviceUpgradeDO aDo = new DeviceUpgradeDO();
        BeanUtils.copyProperties(req, aDo);
        // 解析versionNum，放到数据库三个字段 majorVersion,subVersion,stagedVersion
        String versionNum = req.getVersionNum();
        String[] split = versionNum.split("\\.");
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                aDo.setMajorVersion(Integer.valueOf(split[i]));
            }else if (i == 1) {
                aDo.setSubVersion(Integer.valueOf(split[i]));
            }else if (i == 2) {
                aDo.setStageVersion(Integer.valueOf(split[i]));
            }
        }

        // 根据文件上传路径规则，拼接处文件路径
        String canonicalPath = FileUtil.getCanonicalPath(new File(".."));
        aDo.setFileUrl(canonicalPath + "/device-upgrade-pack-repo/" + versionNum);
        deviceUpgradeDao.save(aDo);
    }

    @Override
    public void updateVersion(UpdateVerReq req) {
        DeviceUpgradeDO aDo = new DeviceUpgradeDO();
        BeanUtils.copyProperties(req, aDo);
        deviceUpgradeDao.updateById(aDo);
    }

    @Override
    public void deleteVersion(Integer id) {
        DeviceUpgradeDO daoById = deviceUpgradeDao.getById(id);
        if (daoById == null) {
            throw new ServiceException("非法的id，数据库中找不到此id");
        }
        String canonicalPath = FileUtil.getCanonicalPath(new File(".."));
        String directory = canonicalPath + "/device-upgrade-pack-repo/" + daoById.getVersionNum();
        // 删除目录
        boolean del = FileUtil.del(directory);
        if (del) {
            deviceUpgradeDao.removeById(id);
        } else {
            throw new ServiceException("删除目录失败" + daoById.getFileUrl());
        }
    }

    @Override
    public DeviceUpgradeDO getInfo(Integer id) {
        return deviceUpgradeDao.getById(id);
    }

    @Override
    public List<DeviceUpgradeListVO> list(String versionNum, String devType) {
        List<DeviceUpgradeDO> deviceUpgradeDOS = deviceUpgradeDao.selectList(versionNum, devType);

        ArrayList<DeviceUpgradeListVO> vos = new ArrayList<>(deviceUpgradeDOS.size());
        for (DeviceUpgradeDO aDo : deviceUpgradeDOS) {
            DeviceUpgradeListVO vo = new DeviceUpgradeListVO();
            BeanUtils.copyProperties(aDo, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public int total() {
        long count = deviceUpgradeDao.count();
        return (int) count;
    }

    /**
     * 现在版本的对比，只比较“前三位”
     * @param req
     * @return
     */
    @Override
    public DeviceUpgradeDO checkUpgrade(CheckUpgradeReq req) {
        // 获得当前设备类型启用的最新版本
        DeviceUpgradeDO latestVersion = deviceUpgradeDao.getLatestVersion(req.getDevType());

        return latestVersion;
    }
}
