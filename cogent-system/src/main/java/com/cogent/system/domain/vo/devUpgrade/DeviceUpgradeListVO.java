package com.cogent.system.domain.vo.devUpgrade;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/17
 * {@code @description:}
 */
@Data
public class DeviceUpgradeListVO {
    private Integer id;
    private String checkCode;
    private String description;
    private String devType;
    private Boolean enable;
    private Boolean forcedUpgrade;
    private String versionNum;
}
