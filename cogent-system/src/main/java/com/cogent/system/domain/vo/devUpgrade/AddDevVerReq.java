package com.cogent.system.domain.vo.devUpgrade;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/16
 * {@code @description:}
 */
@Data
public class AddDevVerReq {

    /**
     * 校验码：MD5
     */
    @NotEmpty
    private String checkCode;

    private String description;
    /**
     * 版本适用设备类型：T-60/T-80
     */
    @NotEmpty
    private String devType;
    /**
     * 是否启用
     */
    @NotNull
    private Boolean enable;
    /**
     * 是否强制升级
     */
    @NotNull
    private Boolean forcedUpgrade;
    /**
     * 版本号
     */
    @NotEmpty
    private String versionNum;
}
