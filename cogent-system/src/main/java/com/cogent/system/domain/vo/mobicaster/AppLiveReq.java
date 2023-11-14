package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/13
 * {@code @description:}
 */
@Data
public class AppLiveReq {

    @NotNull
    private String androidID;
    @NotNull
    private Boolean live;
    private String requestId;
    private String ip;
    private Integer port;
}
