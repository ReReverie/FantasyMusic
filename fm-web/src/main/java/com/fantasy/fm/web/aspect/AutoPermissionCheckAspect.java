package com.fantasy.fm.web.aspect;

import com.fantasy.fm.common.annotation.AutoPermissionCheck;
import com.fantasy.fm.common.constant.SystemConstant;
import com.fantasy.fm.common.context.BaseContext;
import com.fantasy.fm.common.enums.OperationPermission;
import com.fantasy.fm.common.enums.UserLevelEnum;
import com.fantasy.fm.common.exception.OperationPermissionDeniedException;
import com.fantasy.fm.service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AutoPermissionCheckAspect {

    private final UserService userService;

    // 指定切入点
    @Pointcut("@annotation(com.fantasy.fm.common.annotation.AutoPermissionCheck)")
    public void autoPermissionCheck() {
    }

    /**
     * 定义前置通知,在执行方法前进行权限校验
     */
    @Before("autoPermissionCheck()")
    public void permissionCheck(JoinPoint joinPoint) {
        log.info("执行权限校验...");

        //获取被拦截的方法上的注解对象
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //从方法签名中获取方法上的注解
        AutoPermissionCheck annotation = signature.getMethod().getAnnotation(AutoPermissionCheck.class);
        OperationPermission operationPermission = annotation.value();

        //获取当前操作用户ID
        Long userId = BaseContext.getCurrentId();
        //根据用户ID查询用户权限信息
        UserLevelEnum levelEnum = userService.getById(userId).getUserLevelValue();
        log.info("用户:{} 的权限等级:{}", userId, levelEnum);
        //判断用户权限是否足够
        if (!hasPermission(operationPermission, levelEnum)) {
            log.error("用户:{} 权限不足,无法执行该操作:{}", userId, operationPermission);
            throw new OperationPermissionDeniedException(SystemConstant.PERMISSION_DENIED);
        }
    }

    private Boolean hasPermission(OperationPermission operationPermission, UserLevelEnum levelEnum) {
        return switch (operationPermission) {
            case DOWNLOAD -> UserLevelEnum.isVipOrAbove(levelEnum);
            case MUSIC_DELETE, MUSIC_UPLOAD -> UserLevelEnum.isAdmin(levelEnum);
        };
    }
}
