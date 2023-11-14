package com.cogent.system.domain.vo.route;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/9
 * {@code @description:}
 */
@Data
public class DestListReq {

    private String name;
    private String protocol;
    private String addr;
    private String isSdi;
}
