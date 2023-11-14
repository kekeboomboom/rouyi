package com.cogent.system.domain.vo.bag;

import lombok.Data;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 13:44
 * @Description:
 */
@Data
public class Media {

    /**
     * 设备名称
     */
    private String alias;
    /**
     * 音轨数：1~4
     */
    private Integer audioCount;
    /**
     * 音频码率：0/64/128/256/384，单位kbps
     */
    private Integer audioRate;
    /**
     * 音频采样率：48000，单位hz
     */
    private Long audioSamplingRate;
    /**
     * 延时：600~29000单位ms
     */
    private Integer delay;
    /**
     * 编码模式：h264/h265
     */
    private String encodeMode;
    /**
     * 编码格式：auto
     */
    private String encodeFormat;
    /**
     * 加密：true表示开启，false表示关闭
     */
    private Boolean encryption;
    /**
     * 网络协议：udp/tcp
     */
    private String networkProtocol;
    /**
     * OSD：true表示开启，false表示关闭
     */
    private Boolean osd;
    /**
     * 下采样：可选值full或者1/2
     */
    private String subSampling;
    /**
     * 视频最大码率：单位kbps
     */
    private Integer videoMaxRate;

    private String videoFormat;

    private Integer packetLossRate;

    private Integer videoRate;

    private Integer liveState;

    private Integer volume1LeftChannel;
    private Integer volume1RightChannel;
    private Integer volume2LeftChannel;
    private Integer volume2RightChannel;

    private Integer foldbackDelay;
}
