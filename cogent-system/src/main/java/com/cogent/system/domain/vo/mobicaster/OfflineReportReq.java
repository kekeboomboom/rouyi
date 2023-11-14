package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/11
 * {@code @description:}
 */
@Data
public class OfflineReportReq {

    @NotBlank(message = "androidId不能为空")
    private String androidId;
}
