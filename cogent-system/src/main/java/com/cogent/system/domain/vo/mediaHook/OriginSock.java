package com.cogent.system.domain.vo.mediaHook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/20
 * {@code @description:}
 */
@Data
public class OriginSock {
    private String identifier;
    @JsonProperty("local_ip")
    private String localIp;
    @JsonProperty("local_port")
    private int localPort;
    @JsonProperty("peer_ip")
    private String peerIp;
    @JsonProperty("peer_port")
    private int peerPort;
}
