package com.cogent.system.domain.vo.sys;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/25
 * {@code @description:}
 */
@Data
public class UpdateSystemInfoReq {

    @JsonProperty("local_ip")
    private String localIp;
    @JsonProperty("extern_ip")
    private String externIp;
    @JsonProperty("gb_switch")
    private boolean gbSwitch;
    @JsonProperty("default_interface")
    private String defaultInterface;
    @JsonProperty("sdi_switch")
    private boolean sdiSwitch;
    @JsonProperty("NMS_enable")
    private boolean nmsEnable;
    @JsonProperty("map_provider")
    private String mapProvider;
}
