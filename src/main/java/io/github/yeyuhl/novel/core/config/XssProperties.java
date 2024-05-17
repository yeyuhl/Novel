package io.github.yeyuhl.novel.core.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Xss过滤配置属性
 *
 * @author yeyuhl
 * @date 2023/5/18
 */
@ConfigurationProperties(prefix = "novel.xss")
public record XssProperties(Boolean enabled, List<String> excludes) {

}
