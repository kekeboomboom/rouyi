package com.cogent.system.domain.vo.mobicaster;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/11
 * {@code @description:}
 */
@Data
public class AndroidStatusReq {

    private Integer liveStatus;
    private Boolean foldBackStatus;
    private String androidID;
}
