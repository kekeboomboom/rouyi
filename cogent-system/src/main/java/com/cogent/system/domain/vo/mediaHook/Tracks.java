package com.cogent.system.domain.vo.mediaHook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/20
 * {@code @description:}
 */
@Data
public class Tracks {

    private Integer channels;
    @JsonProperty("codec_id")
    private Integer codecId;
    @JsonProperty("codec_id_name")
    private String codecIdName;
    @JsonProperty("codec_type")
    private Integer codecType;
    private Boolean ready;
    @JsonProperty("sample_bit")
    private Integer sampleBit;
    @JsonProperty("sample_rate")
    private Integer sampleRate;
}
