package com.cogent.system.domain.vo.bag;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/4
 * {@code @description:}
 */
@Data
public class GPSInfoReq {

    @NotEmpty(message = "sn不能为空")
    private String sn;
    @NotNull(message = "gps不能为空")
    private Gps gps;

}
