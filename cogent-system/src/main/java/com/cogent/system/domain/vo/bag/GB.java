package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: keboom
 * @Date: 2023/5/16/016 11:44
 * @Description:
 */
@Data
public class GB implements Serializable {
    private static final long serialVersionUID = -2927496458686581652L;

    private Integer backpackId;
    /**
     * 设备国标编号，20位数字
     */
    private String devId;
    /**
     * 心跳周期，单位s
     */
    private long heartbeatCycle;
    /**
     * SIP用户认证密码
     */
    private String passwd;
    /**
     * SIP服务器域，10位数字
     */
    private String platformDomain;
    /**
     * SIP服务器ID，20位数字
     */
    private String platformId;
    /**
     * SIP服务器地址
     */
    private String platformIp;
    /**
     * SIP服务器端口号
     */
    private long platformPort;
    /**
     * SIP用户名
     */
    private String user;
    /**
     * 注册有效期，单位s
     */
    private long validityTime;
    /**
     * 视频通道编号，默认一个，20位数字
     */
    private String videoChannelId;
}
