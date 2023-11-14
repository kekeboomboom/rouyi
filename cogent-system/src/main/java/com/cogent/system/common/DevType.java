package com.cogent.system.common;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/17
 * {@code @description:}
 */
public enum DevType {

    T60("T-60"),
    T80("T-80"),
    APP("App");

    private String name;

    DevType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
