package com.qiaomu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by qiaomu on 17/10/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PermissionRationale {
    int value();
}
