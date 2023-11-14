package com.cogent.system.domain.vo.bag;

import lombok.Data;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 11:40
 * @Description:
 */
@Data
public class Gps {
    /**
     * 经度
     */
    private Long lat;
    /**
     * 纬度
     */
    private Long lon;
}
