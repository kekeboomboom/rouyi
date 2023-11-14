package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/6/30
 * {@code @description:}
 */
@Data
public class EndSpeakReq {
    @NotNull
    private Integer groupNumber;
    @NotBlank
    private String memberNumber;
}
