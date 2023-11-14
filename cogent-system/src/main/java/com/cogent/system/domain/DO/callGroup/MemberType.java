package com.cogent.system.domain.DO.callGroup;

/**
 * @Author keboom
 * @Date 2023-06-26 14:43
 */
public enum MemberType {

    webuser("web用户"),
    backpack("背包设备");

    private String value;

    MemberType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
