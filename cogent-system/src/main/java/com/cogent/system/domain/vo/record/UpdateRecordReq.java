package com.cogent.system.domain.vo.record;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/25
 * {@code @description:}
 */
@Data
public class UpdateRecordReq {

    @NotNull
    private Integer id;
    @NotBlank
    private String name;
}
