package io.github.yeyuhl.novel.core.common.exception;

import io.github.yeyuhl.novel.core.common.constant.ErrorCodeEnum;
import io.github.yeyuhl.novel.core.common.resp.RestResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 通用的异常处理器
 * 由于@RestControllerAdvice被@ControllerAdvice和@ResponseBody注解进行了标注
 * 因此该handler既能处理任何controller，返回值还以响应体信息转换呈现，而不是HTML视图
 *
 * @author yeyuhl
 * @date 2023/4/16
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    /**
     * 处理数据校验异常
     * BindException：当绑定错误被认为是致命的时抛出。
     */
    @ExceptionHandler(BindException.class)
    public RestResp<Void> handlerBindException(BindException e) {
        log.error(e.getMessage(), e);
        return RestResp.fail(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public RestResp<Void> handlerBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return RestResp.fail(e.getErrorCodeEnum());
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public RestResp<Void> handlerException(Exception e) {
        log.error(e.getMessage(), e);
        return RestResp.error();
    }

}
