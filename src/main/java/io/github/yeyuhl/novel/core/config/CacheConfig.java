package io.github.yeyuhl.novel.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.yeyuhl.novel.core.constant.CacheConsts;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 缓存配置类
 *
 * @author yeyuhl
 * @date 2023/4/17
 */
@Configuration
public class CacheConfig {

    /**
     * Caffeine 缓存管理器
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches = new ArrayList<>(CacheConsts.CacheEnum.values().length);

        for (var c : CacheConsts.CacheEnum.values()) {
            // 如果是本地缓存
            if (c.isLocal()) {
                // 新建一个Caffeine对象，recordStats()方法表示启用统计信息记录，maximumSize()方法设置了缓存的最大值
                Caffeine<Object, Object> caffeine = Caffeine.newBuilder().recordStats().maximumSize(c.getMaxSize());
                // 如果ttl>0，那么设置缓存的过期时间
                if (c.getTtl() > 0) {
                    caffeine.expireAfterWrite(Duration.ofSeconds(c.getTtl()));
                }
                caches.add(new CaffeineCache(c.getName(), caffeine.build()));
            }
        }
        // 将缓存放到缓存管理器里面
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * Redis 缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // nonLockingRedisCacheWriter()是一个静态方法，创建一个非锁定的RedisCacheWriter对象，并使用传入的connectionFactory连接Redis
        // 非锁定的RedisCacheWriter意味着性能更好，但是也容易出现问题（脏读）
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        // 使用默认config，禁用缓存空值，并且为缓存名称添加了前缀
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues().prefixCacheNameWith(CacheConsts.REDIS_CACHE_PREFIX);

        Map<String, RedisCacheConfiguration> cacheMap = new LinkedHashMap<>(CacheConsts.CacheEnum.values().length);

        for (var c : CacheConsts.CacheEnum.values()) {
            // 如果是远程缓存，给每个缓存设置对应config
            if (c.isRemote()) {
                if (c.getTtl() > 0) {
                    cacheMap.put(c.getName(),
                            RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                                    .prefixCacheNameWith(CacheConsts.REDIS_CACHE_PREFIX)
                                    .entryTtl(Duration.ofSeconds(c.getTtl())));
                } else {
                    cacheMap.put(c.getName(),
                            RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                                    .prefixCacheNameWith(CacheConsts.REDIS_CACHE_PREFIX));
                }
            }
        }

        RedisCacheManager redisCacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig, cacheMap);
        // 设置为事务感知，即相关操作都以事务形式操作
        redisCacheManager.setTransactionAware(true);
        redisCacheManager.initializeCaches();
        return redisCacheManager;
    }

}
