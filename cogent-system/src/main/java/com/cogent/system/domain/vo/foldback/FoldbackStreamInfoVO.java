package com.cogent.system.domain.vo.foldback;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/18
 * {@code @description:}
 */
@Data
public class FoldbackStreamInfoVO {

    private Audio audio;
    private Video video;

    @Data
    public static class Audio {
        /**
         * 音频编码器
         */
        private String encode;
        /**
         * 采样深度
         */
        private Integer sampleBit;
        /**
         * 采样率
         */
        private Integer sampleRate;
    }

    @Data
    public static class Video {
        /**
         * 视频编码器：H264/H265
         */
        private String encode;
        /**
         * 视频帧率
         */
        private Float fps;
        /**
         * gop,单位ms
         */
        private Integer gop;
        /**
         * 丢包率
         */
        private Integer loss;
        /**
         * 视频分辨率
         */
        private String resolution;
    }
}
