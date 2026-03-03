package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Aspect//切面类
@Slf4j//日志
@Component//组件，交给spring管理
//还需要一个切点，切点就是一个表达式，表示在哪些方法上进行增强
public class AutoFillAspect {

    //    切入点 表达式，表示在com.sky.service.impl包下的所有方法上进行增强，并且这些方法需要被@AutoFill注解标注
//为什么要加@annotation(com.sky.annotation.AutoFill)？因为我们只想在那些需要自动填充公共字段的方法上进行增强，而不是在所有方法上都进行增强，这样就可以避免不必要的性能开销。
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    //    前置通知，在切入点方法执行之前执行
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {

        log.info("自动填充公共字段...");

        //获取到当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取到被拦截的方法的签名信息
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取到被拦截的方法上的@AutoFill注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型
        //获取到当前被拦截方法上的参数-实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;//没有参数，直接返回
        }
        Object entity = args[0];


        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前被拦截方法上的数据库操作类型，来为不同的属性根据反射进行不同的赋值
        if (operationType == OperationType.INSERT) {
            //新增操作，填充createTime、updateTime、createUser、updateUser
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
//                Method setCreateTime = entity.getClass().getDeclaredMethod("SetCreateTime", LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
//                Method setCreateUser = entity.getClass().getDeclaredMethod("SetCreateUser", Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

//                setCreateTime.invoke(entity, now);
//                setCreateUser.incoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}