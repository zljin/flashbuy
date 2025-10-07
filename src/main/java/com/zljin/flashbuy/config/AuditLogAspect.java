package com.zljin.flashbuy.config;


import com.zljin.flashbuy.model.AuditLog;
import com.zljin.flashbuy.util.CommonUtil;
import com.zljin.flashbuy.util.UserInfoHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;


/**
 * todo 改进点：注意性能&注意隐私安全，不打敏感信息
 *
 *
 */
@Slf4j
@Aspect
@Component
public class AuditLogAspect {


    /**
     * AOP通知：
     * 1. 前置通知：在方法调用之前执行
     * 2. 后置通知：在方法正常调用之后执行
     * 3. @Around 环绕通知：在方法调用之前和之后，都分别可以执行的通知
     * 4. 异常通知：如果在方法调用过程中发生异常，则通知
     * 5. 最终通知：在方法调用之后执行
     */

    /**
     * 切面表达式：
     * execution 代表所要执行的表达式主体
     * 第一处 * 代表方法返回类型 *代表所有类型
     * 第二处 包名代表aop监控的类所在的包
     * 第三处 .. 代表该包以及其子包下的所有类方法
     * 第四处 * 代表类名，*代表所有类
     * 第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.zljin.flashbuy.controller..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        AuditLog auditLog = new AuditLog();

        auditLog.setRequestClass(joinPoint.getTarget().getClass().toString());
        auditLog.setRequestMethod(joinPoint.getSignature().getName());
        auditLog.setCreateTime(CommonUtil.dateFormat(new Date()));

        long begin = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();
        StringBuilder argsStr = new StringBuilder();
        int index = 0;
        for (Object arg : args) {
            argsStr.append("arg[").append(index).append("]:").append(arg).append(";");
            index++;
        }
        auditLog.setRequestParams(argsStr.toString());

        // 获取HttpServletRequest
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            auditLog.setRequestPath(request.getRequestURI());
            auditLog.setHttpMethod(request.getMethod());
            auditLog.setRequestIp(request.getHeader("HTTP_CLIENT_IP"));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }

        //否则为匿名用户
        if(UserInfoHolder.getUser() != null) {
            auditLog.setGuid(UserInfoHolder.getUser().getId());
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            auditLog.setExecuteResult(true);
        } catch (Exception e) {
            auditLog.setExecuteResult(false);
            throw e;
        } finally {
            long takeTime = System.currentTimeMillis() - begin;
            auditLog.setResponseData(CommonUtil.getGsonInstance().toJson(result));
            auditLog.setCostTime(takeTime);
            log.info(CommonUtil.getGsonInstance().toJson(auditLog));
        }
        return result;
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

}
