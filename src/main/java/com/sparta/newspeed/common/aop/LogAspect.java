package com.sparta.newspeed.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "LogAspect :: ")
@Aspect
@Component
public class LogAspect {
    @Pointcut("execution(* com.sparta.newspeed.domain.*.controller..*(..))")
    private void controller() {}

    @Before("controller()")
    private void logging() throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURI();
        String method = request.getMethod();

        log.info("request url: {}, method: {}", url, method);
    }
}
