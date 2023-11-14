package com.cogent.system.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceUpgradeServiceImplTest {

    @Test
    void testVersion() throws Exception {
        String version = "1.2.3.2023";
        String[] split = version.split("\\.");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
    }

}