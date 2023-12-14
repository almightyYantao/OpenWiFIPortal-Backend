package com.qunhe.its.networkportal.common.aspect;

import com.alibaba.druid.util.StringUtils;
import com.qunhe.its.networkportal.common.InvokeResult;
import com.qunhe.its.networkportal.common.annotation.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 接口统一异常处理注解的具体实现切面
 */
@Aspect
@Component
@Slf4j
public class ExceptionResponseAspect {


    @Around("@annotation(com.qunhe.its.networkportal.common.annotation.ExceptionResponse)")
    public Object onExceptionThrew(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final ExceptionResponse exceptionResponse = method.getAnnotation(ExceptionResponse.class);
        String message = exceptionResponse.value().trim();
        final String separator = ":";
        if (!StringUtils.isEmpty(message) && !message.endsWith(separator)) {
            message += separator;
        }
        try {
            return joinPoint.proceed();
        } catch (final Throwable e) {
            log.error(message, e);
            return InvokeResult.failure(message + ":" + e.getMessage());
        }
    }
}
