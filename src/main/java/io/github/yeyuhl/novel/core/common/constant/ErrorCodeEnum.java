package io.github.yeyuhl.novel.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举类。
 * 错误码是字符串类型，一共5位，错误产生来源+四位数字编号。
 * 错误产生来源：A/B/C。
 * A代表错误产生来源于用户，比如参数错误等；
 * B代表错误产生来源于当前系统，比如业务逻辑出错等；
 * C代表错误产生来源于第三方服务，如CDN服务出错等。
 * 大类之间的步长间距预留100。
 * <p>
 * 错误码分为一级错误码，二级错误码，三级错误码。
 * 在无法更加具体确定的错误场景中，可以使用一级错误码。
 *
 * @author yeyuhl
 * @date 2023/4/14
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    /**
     * 正确执行后的返回
     */
    OK("00000", "一切 ok"),

    /**
     * 一级错误码，用户端错误
     */
    USER_ERROR("A0001", "用户端错误"),

    /**
     * 二级错误码，用户注册错误
     */
    USER_REGISTER_ERROR("A0100", "用户注册错误"),

    /**
     * 用户未同意隐私协议
     */
    USER_NO_AGREE_PRIVATE_ERROR("A0101", "用户未同意隐私协议"),

    /**
     * 注册国家或地区受限
     */
    USER_REGISTER_AREA_LIMIT_ERROR("A0102", "注册国家或地区受限"),

    /**
     * 用户名已存在
     */
    USER_NAME_EXIST("A0111", "用户名已存在"),

    /**
     * 用户验证码错误
     */
    USER_VERIFY_CODE_ERROR("A0240", "用户验证码错误"),

    /**
     * 二级错误码，用户登录错误
     */
    USER_LOGIN_ERROR("A0200", "用户登录错误"),

    /**
     * 用户账号不存在
     */
    USER_ACCOUNT_NOT_EXIST("A0201", "用户账号不存在"),

    /**
     * 用户密码错误
     */
    USER_PASSWORD_ERROR("A0210", "用户密码错误"),

    /**
     * 用户登录已过期
     */
    USER_LOGIN_EXPIRED("A0230", "用户登录已过期"),

    /**
     * 访问未授权
     */
    USER_UN_AUTH("A0301", "访问未授权"),

    /**
     * 二级错误码，用户请求参数错误
     */
    USER_REQUEST_PARAM_ERROR("A0400", "用户请求参数错误"),

    /**
     * 二级错误码，用户请求服务异常
     */
    USER_REQ_EXCEPTION("A0500", "用户请求服务异常"),

    /**
     * 请求超出限制
     */
    USER_REQ_MANY("A0501", "请求超出限制"),

    /**
     * 二级错误码，用户评论异常
     */
    USER_COMMENT("A2000", "用户评论异常"),

    /**
     * 用户已发表评论
     */
    USER_COMMENTED("A2001", "用户已发表评论"),

    /**
     * 二级错误码，作家发布异常
     */
    AUTHOR_PUBLISH("A3000", "作家发布异常"),

    /**
     * 小说名已存在
     */
    AUTHOR_BOOK_NAME_EXIST("A3001", "小说名已存在"),

    /**
     * 二级错误码，用户上传文件异常
     */
    USER_UPLOAD_FILE_ERROR("A0700", "用户上传文件异常"),

    /**
     * 用户上传文件类型不匹配
     */
    USER_UPLOAD_FILE_TYPE_NOT_MATCH("A0701", "用户上传文件类型不匹配"),

    /**
     * 一级错误码，系统执行出错
     */
    SYSTEM_ERROR("B0001", "系统执行出错"),

    /**
     * 二级错误码，系统执行超时
     */
    SYSTEM_TIMEOUT_ERROR("B0100", "系统执行超时"),

    /**
     * 一级错误码，调用第三方服务出错
     */
    THIRD_SERVICE_ERROR("C0001", "调用第三方服务出错"),

    /**
     * 二级错误码，中间件服务出错
     */
    MIDDLEWARE_SERVICE_ERROR("C0100", "中间件服务出错");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 中文描述
     */
    private final String message;

}
