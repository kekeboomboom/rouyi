package com.cogent.system.common;

public enum RedisConstant {
    STREAM_ID_CALL_GROUP_SET("callGroupStreamIdSet"),
    STREAM_ID_FOLDBACK_SET("foldbackStreamIdSet"),;

    String value;

    RedisConstant() {
    }

    RedisConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
