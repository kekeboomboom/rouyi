package com.cogent.system.domain.DO.bag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author keboom
 * @Date 2023-06-07 11:21
 */
@Data
@TableName("media_current_cfg")
public class MediaCurrentCfgDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String sn;
    private Integer liveDelay;
    private Integer liveRate;
    private Integer videoFormat;
    private Integer videoSource;
    private Integer videoBitDepth;
    private Integer videoCodingStandard;
    private Integer bitrateEncodingMode;
    private Integer audio1Src;
    private Integer audio1Codec;
    private Integer audio1Bitrate;
    private Integer audio2Src;
    private Integer audio2Codec;
    private Integer audio2Bitrate;
    private Integer audio3Src;
    private Integer audio3Codec;
    private Integer audio3Bitrate;
    private Integer audio4Src;
    private Integer audio4Codec;
    private Integer audio4Bitrate;
}
