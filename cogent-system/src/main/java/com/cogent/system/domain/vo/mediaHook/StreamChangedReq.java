package com.cogent.system.domain.vo.mediaHook;

import lombok.Data;

import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/20
 * {@code @description:}
 */
@Data
public class StreamChangedReq {

    private Boolean regist;
    private Integer aliveSecond;
    private String app;
    private Integer bytesSpeed;
    private Long createStamp;
    private String mediaServerId;
    private OriginSock originSock;
    private Integer originType;
    private String originTypeStr;
    private String originUrl;
    private Integer readerCount;
    private String schema;
    private String stream;
    private Integer totalReaderCount;
    private List<Tracks> tracks;
    private String vhost;
}
