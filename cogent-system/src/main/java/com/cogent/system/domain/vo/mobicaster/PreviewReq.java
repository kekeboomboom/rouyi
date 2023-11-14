package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/17
 * {@code @description:}
 */
@Data
public class PreviewReq {

    @NotBlank
    private String androidID;
    @NotNull
    private Boolean previewEnable;
    @NotBlank
    private String streamAppName;
}
