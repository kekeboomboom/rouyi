package com.cogent.system.domain.vo.record;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/23
 * {@code @description:}
 */
@Data
public class RecordSwitchReq {
    @NotNull
    private String sn;
    @NotNull
    private Boolean recordSwitch;
}
