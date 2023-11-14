package com.cogent.system.domain.vo.mediaHook;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author keboom
 * @Date 2023-06-25 9:32
 */
@Data
public class NoReadReq {

    private String mediaServerId;
    @NotBlank
    private String app;
    private String schema;
    @NotBlank
    private String stream;
    private String vhost;

}
