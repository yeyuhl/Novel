package io.github.yeyuhl.novel.core.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 分布式锁-Key 注解
 * Documented意味着当使用javadoc工具生成文档时，它将包含在文档中
 * Retention(RUNTIME)意味着该注解在运行时可用
 * Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
 * 意味着它可以应用于方法、字段和参数
 *
 * @author yeyuhl
 * @date 2023/5/8
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Key {
    // 该元素可以用来指定分布式锁键的表达式，默认值为空字符串
    String expr() default "";

}
