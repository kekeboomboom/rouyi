package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.util.List;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 11:05
 * @Description:
 */
@Data
public class BagDto {

    /**
     * 基础信息数据，同获取列表接口
     */
    private Base base;
    /**
     * 链路信息数据，同获取列表接口
     */
    private Channel channel;
    /**
     * 图表信息数据
     */
    private Chart chart;
    /**
     * 媒体流信息
     */
    private Media media;
    /**
     * 子网路由信息数据，同获取列表接口
     */
    private Network network;
}
