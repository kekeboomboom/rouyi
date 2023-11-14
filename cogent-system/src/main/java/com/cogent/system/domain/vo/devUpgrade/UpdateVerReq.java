package com.cogent.system.domain.vo.devUpgrade;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/16
 * {@code @description:}
 */
@Data
public class UpdateVerReq {

    /**
     * 版本说明
     */
    private String description;
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
     * 修改的版本编号
     */
    @NotNull
    private Integer id;
}
