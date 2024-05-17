package io.github.yeyuhl.novel.core.config;

import io.github.yeyuhl.novel.core.constant.SystemConfigConsts;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenApi配置类
 * 实际用的时候不知道为什么不能用，但是现在也只有Springdoc现在适配了Spring Boot3.0
 *
 * @author yeyuhl
 * @date 2023/5/20
 */
@Configuration
@Profile("dev")
@OpenAPIDefinition(info = @Info(title = "novel 项目接口文档", version = "v3.2.0", license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")))
@SecurityScheme(type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, name = SystemConfigConsts.HTTP_AUTH_HEADER_NAME, description = "登录 token")
public class OpenApiConfig {

}
