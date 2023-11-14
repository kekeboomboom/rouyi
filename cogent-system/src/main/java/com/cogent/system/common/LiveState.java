package com.cogent.system.common;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/23
 * {@code @description:}
 */
public enum LiveState {

    NOT_READY(0, "未就绪"),
    READY(1, "就绪"),
    LIVE(2, "直播中"),
    ABNORMAL(3, "直播异常");

    private Integer code;
    private String desc;

    LiveState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
