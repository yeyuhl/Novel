package io.github.yeyuhl.novel.core.auth;

import lombok.experimental.UtilityClass;

/**
 * 用户信息持有类
 * UtilityClass注解常用于工具类
 * 它的作用是为工具类中的所有成员属性和方法都加上static关键字，使其能直接通过类名调用
 * 此外，Lombok还会为该工具类生成一个私有的空构造函数
 *
 * @author yeyuhl
 * @date 2023/5/9
 */
@UtilityClass
public class UserHolder {

    /**
     * 当前线程用户ID
     */
    private static final ThreadLocal<Long> userIdTL = new ThreadLocal<>();

    /**
     * 当前线程作家ID
     */
    private static final ThreadLocal<Long> authorIdTL = new ThreadLocal<>();

    public void setUserId(Long userId) {
        userIdTL.set(userId);
    }

    public Long getUserId() {
        return userIdTL.get();
    }

    public void setAuthorId(Long authorId) {
        authorIdTL.set(authorId);
    }

    public Long getAuthorId() {
        return authorIdTL.get();
    }

    public void clear() {
        userIdTL.remove();
        authorIdTL.remove();
    }

}
