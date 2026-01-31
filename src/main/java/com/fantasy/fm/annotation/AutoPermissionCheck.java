package com.fantasy.fm.annotation;

import com.fantasy.fm.enums.OperationPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoPermissionCheck {
    OperationPermission value();
}
