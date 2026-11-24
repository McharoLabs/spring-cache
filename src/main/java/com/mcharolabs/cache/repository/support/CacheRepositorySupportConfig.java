package com.mcharolabs.cache.repository.support;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.repository.CacheRepository;
import com.mcharolabs.cache.service.CacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the factory bean used to instantiate {@link CacheRepository} proxy beans.
 *
 * @author godfrey.steven
 * @since 2026
 */
@Configuration
public class CacheRepositorySupportConfig {

    @Bean("cacheRepositoryProxyFactoryBean")
    public CacheRepositoryProxyInvoker cacheRepositoryProxyFactoryBean(CacheService cacheService) {
        return new CacheRepositoryProxyInvoker(cacheService);
    }

    public record CacheRepositoryProxyInvoker(CacheService cacheService) {
        public <ID, T extends CacheDataDto> CacheRepository<ID, T> createRepositoryProxy(
                Class<? extends CacheRepository<ID, T>> repositoryInterface,
                Class<T> valueType) {
            return CacheRepositoryProxyFactory.createProxy(repositoryInterface, cacheService, valueType);
        }
    }
}