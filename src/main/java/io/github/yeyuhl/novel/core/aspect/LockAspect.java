package io.github.yeyuhl.novel.core.aspect;

import io.github.yeyuhl.novel.core.annotation.Key;
import io.github.yeyuhl.novel.core.annotation.Lock;
import io.github.yeyuhl.novel.core.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 *
 * @author yeyuhl
 * @date 2023/5/9
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
    
    private final RedissonClient redissonClient;

    private static final String KEY_PREFIX = "Lock";

    private static final String KEY_SEPARATOR = "::";

    /**
     * Around注解的value属性指定了该通知应用于哪些方法，即带有@Lock注解的方法
     * 而SneakyThrows注解可以在不声明throw子句的情况下抛出已检查的异常
     */
    @Around(value = "@annotation(io.github.yeyuhl.novel.core.annotation.Lock)")
    @SneakyThrows
    public Object doAround(ProceedingJoinPoint joinPoint) {
        // 获取目标方法的签名和@Lock注解对象
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        Lock lock = targetMethod.getAnnotation(Lock.class);
        // 调用buildLockKey方法构建锁的Key
        String lockKey = KEY_PREFIX + buildLockKey(lock.prefix(), targetMethod, joinPoint.getArgs());
        // 使用getLock方法获取锁对象
        RLock rLock = redissonClient.getLock(lockKey);
        // 查看lock是否要等待，如果要等待就在等待时间之后去获取锁，如果不用就直接获取锁
        if (lock.isWait() ? rLock.tryLock(lock.waitTime(), TimeUnit.SECONDS) : rLock.tryLock()) {
            // 如果上锁成功
            try {
                // 调用proceed方法来执行目标方法
                return joinPoint.proceed();
            } finally {
                // 执行完后释放锁
                rLock.unlock();
            }
        }
        // 如果上锁失败就则抛出一个业务异常
        throw new BusinessException(lock.failCode());
    }

    /**
     * 根据@Lock注解中的前缀、和@Key的目标方法和方法参数构建锁的Key
     *
     * @param prefix 注解前缀
     * @param method 目标方法对象
     * @param args   方法参数值数组
     * @return 返回Key字符串
     */
    private String buildLockKey(String prefix, Method method, Object[] args) {
        StringBuilder builder = new StringBuilder();
        // 如果有前缀，在分割符"::"后面添加前缀
        if (StringUtils.hasText(prefix)) {
            builder.append(KEY_SEPARATOR).append(prefix);
        }
        Parameter[] parameters = method.getParameters();
        // 遍历目标方法的所有参数
        for (int i = 0; i < parameters.length; i++) {
            builder.append(KEY_SEPARATOR);
            // 检查每个参数是否有@Key注解
            if (parameters[i].isAnnotationPresent(Key.class)) {
                // 如果有@Key注解，就调用parseKeyExpr方法解析@Key注解中的表达式
                Key key = parameters[i].getAnnotation(Key.class);
                builder.append(parseKeyExpr(key.expr(), args[i]));
            }
        }
        return builder.toString();
    }

    /**
     * 用于解析@Key注解中的表达式
     *
     * @param expr 表达式的字符串
     * @param arg  方法参数的值
     * @return 返回一个字符串
     */
    private String parseKeyExpr(String expr, Object arg) {
        // 表达字符串为空，则直接返回参数值的字符串表示
        if (!StringUtils.hasText(expr)) {
            return arg.toString();
        }
        // SpEL是Spring表达式语言的缩写，它支持非常多的语法
        ExpressionParser parser = new SpelExpressionParser();
        // 使用TemplateParserContext来支持#{...}形式的表达式
        Expression expression = parser.parseExpression(expr, new TemplateParserContext());
        return expression.getValue(arg, String.class);
    }

}
