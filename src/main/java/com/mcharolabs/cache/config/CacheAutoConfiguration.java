package com.mcharolabs.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcharolabs.cache.repository.support.CacheRepositoriesRegistrar;
import com.mcharolabs.cache.service.CacheService;
import com.mcharolabs.cache.service.impl.CacheServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Autoconfiguration for Redis caching and automatic {@code CacheRepository} proxy registration.
 *
 * @author godfrey.steven
 * @since 2026
 */
@AutoConfiguration(after = DataRedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
@Import(CacheRepositoriesRegistrar.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper cacheObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(CacheService.class)
    public CacheService cacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            CacheProperties properties) {
        return new CacheServiceImpl(redisTemplate, objectMapper, properties);
    }
}