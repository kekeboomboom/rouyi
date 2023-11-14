package com.cogent.system.domain.vo.map;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Data
public class DeviceGPSHisReq {

    @NotEmpty
    private String sn;
    @NotNull
    private Long startTimestamp;
    @NotNull
    private Long endTimestamp;
}
