package com.cogent.system.domain.DO.map;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/4
 * {@code @description:}
 */
@Data
@TableName("gps_info")
public class GpsInfoDO {

    private String sn;
    private BigDecimal lat;
    private BigDecimal lon;
    private Date createTime;
}
