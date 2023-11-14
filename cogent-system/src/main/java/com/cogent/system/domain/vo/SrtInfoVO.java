package com.cogent.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author keboom
 * @Date 2023-06-08 17:29
 */
@Data
public class SrtInfoVO implements Serializable {
    private static final long serialVersionUID = 2501929016777895496L;

    private Integer uptime;
    private Integer connected;
    private Integer reconnection;
    private Integer bitrate;
    @JsonProperty("drop_pkt")
    private Integer dropPkt;
    @JsonProperty("drop_rate")
    private Integer dropRate;
    @JsonProperty("used_band_width")
    private Integer usedBandWidth;
    private Integer rtt;
    private Integer srtlatency;
    private Integer srtbuffer;
    @JsonProperty("loss_rate")
    private Integer lossRate;
}
