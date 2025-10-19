package com.zljin.flashbuy.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 * 适用于存放数量不大的热点数据
 * 数据不需要在多个服务实例之间共享
 * 数据允许短暂的不一致
 *
 * 性能为纳秒级别，用的是本地缓存
 *
 * */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)  // 访问后10分钟过期
                .maximumSize(100) // 最大100个条目
                .recordStats()// 开启统计
                .removalListener((key, value, cause) ->
                        log.debug("local缓存移除: key={}, cause={}", key, cause)
                ));
        return cacheManager;
    }
}
