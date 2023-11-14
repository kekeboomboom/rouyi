package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/10
 * {@code @description:}
 */
@Data
public class SpecifySpeakReq {
    @NotBlank
    private String memberNumber;
    @NotNull
    private Integer groupNumber;
}
