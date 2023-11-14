package com.cogent.system.common;

import com.google.common.collect.MapMaker;
import com.google.common.util.concurrent.RateLimiter;
import lombok.SneakyThrows;
import net.sf.jsqlparser.statement.select.KSQLWindow;
import org.apache.catalina.Session;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/26
 * {@code @description:}
 */
public class RateLimiterTest {

    @SneakyThrows
    @Test
    void rateLimiter() {
        long startTime = ZonedDateTime.now().getSecond();
        RateLimiter rateLimiter = RateLimiter.create(3);
        for (int i = 0; i < 10; i++) {
            boolean b = rateLimiter.tryAcquire(1, 1, TimeUnit.MILLISECONDS);
            if (!b) {
                System.out.println("get token failed");
                Thread.sleep(100);
                continue;
            }
            System.out.println("get token success");
        }


        long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;
        System.out.println(elapsedTimeSeconds);
    }
}
