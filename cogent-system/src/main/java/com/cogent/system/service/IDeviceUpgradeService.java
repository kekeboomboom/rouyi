package com.cogent.system.service;

import com.cogent.system.domain.DO.devUpgrade.DeviceUpgradeDO;
import com.cogent.system.domain.vo.devUpgrade.AddDevVerReq;
import com.cogent.system.domain.vo.devUpgrade.CheckUpgradeReq;
import com.cogent.system.domain.vo.devUpgrade.DeviceUpgradeListVO;
import com.cogent.system.domain.vo.devUpgrade.UpdateVerReq;

import java.util.List;

public interface IDeviceUpgradeService {
    void addVersion(AddDevVerReq req);

    void updateVersion(UpdateVerReq req);

    void deleteVersion(Integer id);

    DeviceUpgradeDO getInfo(Integer id);

    List<DeviceUpgradeListVO> list(String versionNum, String devType);

    int total();

    DeviceUpgradeDO checkUpgrade(CheckUpgradeReq req);
}
