package io.github.yeyuhl.novel.core.annotation;

import io.github.yeyuhl.novel.core.common.constant.ErrorCodeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 分布式锁 注解
 *
 * @author yeyuhl
 * @date 2023/5/8
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Lock {
    // 必需元素，前缀
    String prefix();
    // 是否等待，默认否
    boolean isWait() default false;
    // 等待时间，默认3L
    long waitTime() default 3L;
    // 错误代码，默认OK
    ErrorCodeEnum failCode() default ErrorCodeEnum.OK;

}
