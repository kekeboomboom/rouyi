package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.util.List;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 11:53
 * @Description:
 */
@Data
public class ChartChannel {

    /**
     * 链路ID(0~9对应10条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel0;
    /**
     * 链路ID(0~9对应10条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel1;
    /**
     * 链路ID(0~9对应10条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel2;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel3;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel4;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel5;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel6;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel7;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel8;
    /**
     * 链路ID(0~9对应9条链路)作为字段名，没有使用的链路数据均为0，以下同理
     */
    private ChartChannelInfo channel9;

    @Data
    public class ChartChannelInfo {
        List<Integer> rateDown;
        List<Integer> rateUp;
        List<Integer> rtt;
    }


}

