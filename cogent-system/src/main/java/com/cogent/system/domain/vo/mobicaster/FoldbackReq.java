package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/26
 * {@code @description:}
 */
@Data
public class FoldbackReq {

    private Integer port;
    private String streamId;
    private String androidID;
    private Boolean action;
    private String streamName;
}
