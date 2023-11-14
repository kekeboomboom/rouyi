package com.cogent.system.domain.vo.bag;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: keboom
 * @Date: 2023/5/16/016 10:19
 * @Description:
 */
@Data
public class BagSettingReq {

    @NotNull
    private Integer backpackId;

    @NotBlank
    private String sn;

    @NotNull
    private BagDto device;
}
