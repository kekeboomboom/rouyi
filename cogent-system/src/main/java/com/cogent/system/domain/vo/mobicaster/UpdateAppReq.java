package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/12
 * {@code @description:}
 */
@Data
public class UpdateAppReq {

    @NotNull
    private Integer id;
    @NotBlank
    private String sn;
    @NotNull
    private AppInfo device;
}
