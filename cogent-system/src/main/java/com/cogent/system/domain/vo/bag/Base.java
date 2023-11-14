package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 11:39
 * @Description:
 */
@Data
public class Base {

    /**
     * 设备名称
     */
    private String alias;
    /**
     * 背包编号：数据库中的编号
     */
    private Long backpackId;
    /**
     * gps信息
     */
    private Gps gps;
    /**
     * 端口映射，暂不需要
     */
    private List<BaseMapping> mapping;
    /**
     * 当前码率，单位Kbps
     */
    private Long rateup;

    private Long ratedown;
    /**
     * 序列号
     */
    private String sn;
    /**
     * 状态：0表示离线，1在线没有开启直播，2直播中
     */
//    private Long state;
    /**
     * 设备类型：T60/T80
     */
    private String type;

    private String state;
}
