package com.cogent.system.common;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/11/6
 * {@code @description:}
 */
public enum DestState {

    NO_ROUTE_CONNECTED(0, "与任何路由都没有联系"),
    ROUTE_CONNECTED_CLOSE(1, "与某个路由有联系，并且目的地为关闭状态"),
    ROUTE_CONNECTED_OPEN(2, "与某个路由有联系，并且目的地为打开状态");

    private Integer code;
    private String desc;

    DestState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }
}
