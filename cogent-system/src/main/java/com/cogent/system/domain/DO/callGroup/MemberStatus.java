package com.cogent.system.domain.DO.callGroup;

/**
 * @Author keboom
 * @Date 2023-06-26 14:45
 */
public enum MemberStatus {

    offline("离线"),
    onlineIn("在线且在当前群组"),
    onlineNotIn("在线但不在当前群组");

    private String status;

    MemberStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
