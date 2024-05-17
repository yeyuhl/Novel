package io.github.yeyuhl.novel.core.config;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Elasticsearch相关配置
 *
 * @author yeyuhl
 * @date 2023/5/11
 */
@Configuration
@Slf4j
public class EsConfig {

    /**
     * 解决 ElasticsearchClientConfigurations 修改默认 ObjectMapper 配置的问题
     */
    @Bean
    JacksonJsonpMapper jacksonJsonpMapper() {
        return new JacksonJsonpMapper();
    }

    /**
     * 对elasticsearchRestClient进行设置，感觉这个设置信任证书安全上有些疏漏
     */
    @ConditionalOnProperty(value = "spring.elasticsearch.ssl.verification-mode", havingValue = "none")
    @Bean
    RestClient elasticsearchRestClient(RestClientBuilder restClientBuilder, ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
        // setHttpClientConfigCallback通过其构建器构建RestClient时用来自定义http客户端配置
        restClientBuilder.setHttpClientConfigCallback((HttpAsyncClientBuilder clientBuilder) -> {
            // X509TrustManager是一个接口，扩展了TrustManager接口，用于管理X509证书，验证远程安全套接字的证书链
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sc = null;
            try {
                // 使用SSL协议
                sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                log.error("Elasticsearch RestClient 配置失败！", e);
            }
            assert sc != null;
            // 设置clientBuilder的SSL上下文，还设置了一个始终返回true的SSL主机名验证器
            clientBuilder.setSSLContext(sc);
            clientBuilder.setSSLHostnameVerifier((hostname, session) -> true);

            // 遍历有序的builderCustomizers流，并对每个自定义器调用其自定义方法，传入clientBuilder作为参数。最后返回修改后的clientBuilder
            builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(clientBuilder));
            return clientBuilder;
        });
        return restClientBuilder.build();
    }

}
