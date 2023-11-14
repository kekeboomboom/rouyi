package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/22
 * {@code @description:}
 */
@Data
public class DeviceInfoReq {

    @NotBlank(message = "设备ID不能为空")
    private String androidID;

    // 安卓版本不能为空，后续通过此字段判断是设备表中，是背包还是手机
    @NotBlank(message = "android版本不能为空")
    private String androidVersion;

    // 制造商，会显示HUAWEI等公司
    @NotBlank(message = "制造商不能为空")
    private String manufacturer;

    // PHB110 这种的，并不是我们想想的P40，one plus 11 等等这种型号
    @NotBlank(message = "型号不能为空")
    private String model;
}
