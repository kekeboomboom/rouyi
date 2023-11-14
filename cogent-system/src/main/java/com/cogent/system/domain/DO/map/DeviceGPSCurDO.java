package com.cogent.system.domain.DO.map;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Data
@TableName("device_gps_cur")
public class DeviceGPSCurDO {

    private String sn;
    private BigDecimal lat;
    private BigDecimal lon;
    private Date updateTime;
}
