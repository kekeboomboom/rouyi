package com.cogent.system.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/3
 * {@code @description:}
 */
class FoldbackServiceImplTest {

    @Test
    void generateStreamId() {
        for (int i = 0; i <10; i++) {
            String s = RandomStringUtils.randomNumeric(8);
            System.out.println(s);
        }

    }
}
