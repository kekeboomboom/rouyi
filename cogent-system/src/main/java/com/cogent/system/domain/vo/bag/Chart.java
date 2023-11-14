package com.cogent.system.domain.vo.bag;

import lombok.Data;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 11:41
 * @Description:
 */
@Data
public class Chart {

    /**
     * 各个链路的图表信息数据
     */
    private ChartChannel channel;
    /**
     * 时间戳，用于图表的横向时间轴
     */
    private Long timestamp;
    /**
     * 聚合链路的图表信息数据
     */
    private Total total;
}
