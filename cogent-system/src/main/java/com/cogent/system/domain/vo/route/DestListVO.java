package com.cogent.system.domain.vo.route;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/9
 * {@code @description:}
 */
@Data
public class DestListVO {

    private Long destId;
    private String addr;
    private Boolean isSdi;

    private String name;

    private Integer port;

    private String protocol;

    private String protocolType;

    private Integer state;
}
