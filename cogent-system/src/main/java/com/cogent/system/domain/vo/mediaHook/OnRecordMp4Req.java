package com.cogent.system.domain.vo.mediaHook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/20
 * {@code @description:}
 */
@Data
public class OnRecordMp4Req {
    private String mediaServerId;
    private String app;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("file_path")
    private String filePath;
    @JsonProperty("file_size")
    private Long fileSize;
    private String folder;
    @JsonProperty("start_time")
    private Long startTime;
    private String stream;
    @JsonProperty("time_len")
    private Integer timeLen;
    private String url;
    private String vhost;
}
