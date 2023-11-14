package com.cogent.system.domain.vo.sdi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/11/6
 * {@code @description:}
 */
@Data
public class OpenSDIReq {

    /**
     * 内网是可以指定不同内网ip。
     */
    @NotBlank(message = "ip不能为空")
    private String ip;
    @NotNull
    private Integer port;
    /**
     * srt 或者 udp，内网是udp
     */
    @NotBlank(message = "协议不能为空")
    private String protocol;
    /**
     * SDI名称，比如SDI-1，SDI-2，SDI-3
     */
    @NotBlank(message = "SDI名称不能为空")
    @JsonProperty("SDIName")
    private String SDIName;
    @NotBlank(message = "sn不能为空")
    private String sn;
}
