package com.qiaomu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by qiaomu on 17/10/9.
 */
@Target(ElementType.METHOD)
public @interface PermissionGrant {
    int value();
}
