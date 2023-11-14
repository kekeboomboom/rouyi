package com.cogent.system.common;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/23
 * {@code @description:}
 */
public enum RecordType {

    HLS(0, "hls"),
    MP4(1, "mp4");

    private int code;
    private String type;

    RecordType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }
}
