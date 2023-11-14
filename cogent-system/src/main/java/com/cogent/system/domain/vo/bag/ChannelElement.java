package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.util.List;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 11:41
 * @Description:
 */
@Data
public class ChannelElement {

    /**
     * 链路是否开启，0表示关闭，1表示开启
     */
    private Long enable;
    /**
     * 链路网关
     */
    private String gateway;
    /**
     * 链路编号：唯一标识，从0开始
     */
    private Long id;
    /**
     * 同种链路类型的序号，如5G的index值有1和2
     */
    private Long index;
    /**
     * 链路ip地址
     */
    private String ip;
    /**
     * 除SIM卡外的链路共有；是否dhcp，0表示关闭dhcp，1表示开启dhcp
     */
    private Long isdhcp;
    /**
     * type字段4和5有效；mac地址
     */
    private String mac;
    /**
     * 链路子网掩码
     */
    private String mask;
    /**
     * type字段为3、4、5时有效；LAN/WAN模式切换，0表示WAN模式，1表示LAN模式
     */
    private Integer service_mode;
    /**
     * 链路是否在线，0表示不在线，1表示在线
     */
    private Long online;
    /**
     * 链路下行码率，单位Kbps
     */
    private Long ratedown;
    /**
     * 链路上行码率，单位Kbps
     */
    private Long rateup;
    /**
     * 链路rtt，单位ms
     */
    private Long rtt;
    /**
     * SIM卡特有(type字段为0和1)；APN
     */
    private String apn;
    /**
     * SIM卡特有(type字段为0和1)；加密方式：0表示无密码，1表示PAP，2表示CHAP，3表示自动
     */
    private Long authType;
    /**
     * SIM卡特有(type字段为0和1)；频率
     */
    private Long frequency;
    /**
     * SIM卡特有(type字段为0和1)；频段
     */
    private String frequencyRange;
    /**
     * SIM卡特有(type字段为0和1)；ICCID
     */
    private String iccid;
    /**
     * SIM卡特有(type字段为0和1)；是否支持多卡，1表示支持，0表示不支持
     */
    private Long isMultiSlot;
    /**
     * SIM卡特有(type字段为0和1)；运营商信息，0移动，1联通，2电信，3广电，100总链路，其他值显示未知
     */
    private Long isp;
    /**
     * SIM卡特有(type字段为0和1)；获取入网方式：0表示SA优先，1表示SA，2表示NSA
     */
    private Long net;
    /**
     * SIM卡特有(type字段为0和1)；加密密码
     */
    private String password;
    /**
     * SIM卡特有(type字段为0和1)；RSRP
     */
    private Long rsrp;
    /**
     * SIM卡特有(type字段为0和1)；RSRQ
     */
    private Long rsrq;
    /**
     * SIM卡特有(type字段为0和1)；RSSI
     */
    private Long rssi;
    /**
     * SIM卡特有(type字段为0和1)；SINR
     */
    private Long sinr;
    /**
     * SIM卡特有(type字段为0和1)；多卡选择，1表示主卡，2表示副卡，3表示自动选择
     */
    private Long slot;
    /**
     * SIM卡特有(type字段为0和1)；用户名称
     */
    private String user;
    /**
     * 链路类型：0表示5G，1表示4G，2表示USB，3表示ETH网口，4表示WIFI，5表示USB口用作WIFI
     */
    private Long type;
    /**
     * type字段为4和5有时；wifi带宽,0表示20M，1表示20/40M，2表示20/40M/80M
     */
    private Long wifiBandwidth;
    /**
     * type字段为4和5有时；wifi信道
     */
    private Long wifiChannel;
    /**
     * type字段为4和5有时；wifi信道可设置项，全部可设置项[0,36,40,44,48,149,153,157,161,165]，0表示自动选择
     */
    private List<Long> wifiChannelList;
    /**
     * type字段为0和1有时；wifi加密方式，0表示无密码，1表示WPA-PSK，2表示WPA2-PSK
     */
    private Long wifiEncryption;
    /**
     * type字段为4和5有时；wifi是否隐藏，0表示不隐藏，1表示隐藏
     */
    private Long wifiHidden;
    /**
     * type字段为4和5有时；wifi频段，0表示2.4G，1表示5G
     */
    private Long wifiMode;
    /**
     * type字段为4和5有时；wifi名称
     */
    private String wifiName;
    /**
     * type字段为4和5有时；wifi密码
     */
    private String wifiPassword;
}
