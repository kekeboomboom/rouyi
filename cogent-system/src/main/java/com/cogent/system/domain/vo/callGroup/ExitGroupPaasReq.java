package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/3
 * {@code @description:}
 */
@Data
public class ExitGroupPaasReq {
    @NotNull
    private Integer groupNumber;
    @NotBlank
    private String memberNumber;
}
