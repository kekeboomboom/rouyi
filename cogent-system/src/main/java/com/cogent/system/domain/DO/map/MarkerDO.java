package com.cogent.system.domain.DO.map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/7
 * {@code @description:}
 */
@Data
@TableName("gps_marker")
public class MarkerDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private BigDecimal lat;
    private BigDecimal lon;
    private String description;
}
