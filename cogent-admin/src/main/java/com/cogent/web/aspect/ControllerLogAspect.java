package com.cogent.web.aspect;

import com.alibaba.fastjson2.JSON;
import com.cogent.common.utils.StringUtils;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@Aspect
public class ControllerLogAspect {


    @Pointcut("execution(public * com.cogent.web.controller.system.*.*(..))")
    public void log() {
    }

    @Around("log()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Stopwatch started = Stopwatch.createStarted();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        Object result;
        result = point.proceed();

        String requestParam = JSON.toJSONString(Arrays.stream(point.getArgs())
                .filter(param -> !(param instanceof HttpServletRequest)
                        && !(param instanceof HttpServletResponse)
                        && !(param instanceof MultipartFile)
                        && !(param instanceof MultipartFile[])
                ).collect(Collectors.toList()));

        started.stop();
        log.info(" url:{} " +
                        "RequestMethod:{} " +
                        "requestParam:{} " +
                        "response:{} " +
                        "cost:{}ms",
                request.getRequestURL(),
                request.getMethod(),
                requestParam,
                StringUtils.substring(JSON.toJSONString(result), 0, 600),
                started.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

}
