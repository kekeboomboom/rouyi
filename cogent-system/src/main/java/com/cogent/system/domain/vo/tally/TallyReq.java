package com.cogent.system.domain.vo.tally;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/21
 * {@code @description:}
 */
@Data
public class TallyReq {

    @NotEmpty
    private String sn;
    @NotNull
    private Integer tallyState;
}
