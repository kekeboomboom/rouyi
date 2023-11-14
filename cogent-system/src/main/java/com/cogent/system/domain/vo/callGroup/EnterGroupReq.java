package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EnterGroupReq {
    @NotNull
    private Integer groupNumber;
    @NotBlank
    private String memberNumber;
}
