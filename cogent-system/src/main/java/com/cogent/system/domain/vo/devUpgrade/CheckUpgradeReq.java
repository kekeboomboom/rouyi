package com.cogent.system.domain.vo.devUpgrade;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/18
 * {@code @description:}
 */
@Data
public class CheckUpgradeReq {

    @NotEmpty
    private String sn;
    @NotEmpty
    private String devType;
    @NotEmpty
    private String versionNum;
}
