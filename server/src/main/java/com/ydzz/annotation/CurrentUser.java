package com.ydzz.annotation;

import io.swagger.v3.oas.annotations.Hidden;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 获取当前登录用户信息的注解
 * 可以标注在Controller方法的参数上，自动注入当前用户
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Hidden
public @interface CurrentUser {
}
