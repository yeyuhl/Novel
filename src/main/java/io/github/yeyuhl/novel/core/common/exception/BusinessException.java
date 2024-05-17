package io.github.yeyuhl.novel.core.common.exception;

import io.github.yeyuhl.novel.core.common.constant.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常，用于处理用户请求时，业务错误时抛出
 *
 * @author yeyuhl
 * @date 2023/4/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private final ErrorCodeEnum errorCodeEnum;

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        // 不调用父类Throwable的fillInStackTrace() 方法生成栈追踪信息，提高应用性能
        // 构造器间的调用必须在第一行
        // 参数类型：String message,Throwable cause,boolean enableSuppression,boolean writableStackTrac
        super(errorCodeEnum.getMessage(), null, false, false);
        this.errorCodeEnum = errorCodeEnum;
    }

}
