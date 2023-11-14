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
public class LiveReq {
    @NotBlank
    private String androidID;
    // start or stop
    @NotNull
    private Boolean liveAction;
}
