package com.cogent.system.common;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/11/7
 * {@code @description:}
 */
public enum SDIProtocolType {

    UDP("udp"),
    SRT("srt");

    private String protocol;

    SDIProtocolType(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public static SDIProtocolType getEnum(String protocol) {
        for (SDIProtocolType value : SDIProtocolType.values()) {
            if (value.getProtocol().equals(protocol)) {
                return value;
            }
        }
        return null;
    }
}
