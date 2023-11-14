package com.cogent.system.domain.vo.map;

import lombok.Data;

import java.math.BigDecimal;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Data
public class MarkerReq {

    private Integer id;
    private BigDecimal lat;
    private BigDecimal lon;
    private String description;
}
