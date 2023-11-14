package com.cogent.system.domain.vo.sdi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/11/7
 * {@code @description:}
 */
@Data
public class CloseSDIReq {

    @NotBlank(message = "sn不能为空")
    private String sn;
    @NotBlank(message = "SDI名称不能为空")
    @JsonProperty("SDIName")
    private String SDIName;
}
