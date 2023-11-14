package com.cogent.system.domain.vo.map;

import lombok.Data;

import java.math.BigDecimal;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/11
 * {@code @description:}
 */
@Data
public class DeviceGPSListVO {

    private String sn;
    private BigDecimal lat;
    private BigDecimal lon;
    private String name;
    private String type;

}
