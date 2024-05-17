package io.github.yeyuhl.novel;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@SpringBootApplication
@MapperScan("io.github.yeyuhl.novel.dao.mapper")
@EnableCaching
@EnableScheduling
@Slf4j
public class NovelRewriteApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelRewriteApplication.class, args);
    }

    /**
     * 这是一个CommandLineRunner bean
     * 实现CommandLineRunner接口允许在程序启动时运行代码
     * 它将在程序启动时执行，打印出应用程序上下文中所有CacheManager bean的名称和类
     * 如果idea提醒命令行过长需要缩短，可能就是这个bean造成的，根据idea提示优化即可
     */
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            Map<String, CacheManager> beans = context.getBeansOfType(CacheManager.class);
            log.info("加载了如下缓存管理器：");
            beans.forEach((k, v) -> {
                log.info("{}:{}", k, v.getClass().getName());
                log.info("缓存：{}", v.getCacheNames());
            });

        };
    }

    /**
     * 这是一个SecurityFilterChain bean，它用于保护程序中的所有endpoint
     * endpoint是两个系统之间的通信点，比如在Web应用程序的上下文中，endpoint是可用于访问特定资源的URL
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 禁用了CSRF保护
        http.csrf().disable()
                // 指定SecurityFilterChain应该应用于所有endpoint
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                // 所有用户必须拥有ENDPOINT_ADMIN角色才能访问任何endpoint
                .authorizeHttpRequests(requests -> requests.anyRequest().hasRole("ENDPOINT_ADMIN"));
        // 将SecurityFilterChain配置为使用HTTP Basic认证
        http.httpBasic();
        return http.build();
    }
}
