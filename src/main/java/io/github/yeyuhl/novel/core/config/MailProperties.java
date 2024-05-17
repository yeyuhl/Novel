package io.github.yeyuhl.novel.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mail配置属性
 *
 * @author yeyuhl
 * @date 2023/5/18
 */
@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(String nickname, String username) {

}
