package com.cogent.system.domain.vo.bag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author keboom
 * @Date 2023-06-08 9:58
 */
@Data
public class ConfigReportReq implements Serializable {
    private static final long serialVersionUID = -8422068007665014388L;

    private String sn;
    private SubConfigReportReq config;

    @Data
    public class SubConfigReportReq {

        @JsonProperty("Media_cfg_LiveDelay")
        Integer MediaCfgLiveDelay;
        @JsonProperty("Media_cfg_LiveRate")
        Integer MediaCfgLiveRate;
        @JsonProperty("Media_cfg_VideoFormat")
        Integer MediaCfgVideoFormat;
        @JsonProperty("Media_cfg_VideoSource")
        Integer MediaCfgVideoSource;
        @JsonProperty("Media_cfg_VideoBitDepth")
        Integer MediaCfgVideoBitDepth;
        @JsonProperty("Media_cfg_VideoCodingStrandard")
        Integer MediaCfgVideoCodingStrandard;
        @JsonProperty("Media_cfg_BitrateEncodingMode")
        Integer MediaCfgBitrateEncodingMode;
        @JsonProperty("Media_cfg_Audio1Src")
        Integer MediaCfgAudio1Src;
        @JsonProperty("Media_cfg_Audio1Codec")
        Integer MediaCfgAudio1Codec;
        @JsonProperty("Media_cfg_Audio1Bitrate")
        Integer MediaCfgAudio1Bitrate;
        @JsonProperty("Media_cfg_Audio2Src")
        Integer MediaCfgAudio2Src;
        @JsonProperty("Media_cfg_Audio2Codec")
        Integer MediaCfgAudio2Codec;
        @JsonProperty("Media_cfg_Audio2Bitrate")
        Integer MediaCfgAudio2Bitrate;
        @JsonProperty("Media_cfg_Audio3Src")
        Integer MediaCfgAudio3Src;
        @JsonProperty("Media_cfg_Audio3Codec")
        Integer MediaCfgAudio3Codec;
        @JsonProperty("Media_cfg_Audio3Bitrate")
        Integer MediaCfgAudio3Bitrate;
        @JsonProperty("Media_cfg_Audio4Src")
        Integer MediaCfgAudio4Src;
        @JsonProperty("Media_cfg_Audio4Codec")
        Integer MediaCfgAudio4Codec;
        @JsonProperty("Media_cfg_Audio4Bitrate")
        Integer MediaCfgAudio4Bitrate;
    }
}
