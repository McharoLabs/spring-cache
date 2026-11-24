package com.mcharolabs.cache.repository.support;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.repository.CacheRepository;
import com.mcharolabs.cache.service.CacheService;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring {@link FactoryBean} that creates a dynamic proxy for a {@link CacheRepository} interface.
 *
 * @author godfrey.steven
 * @since 2026
 */
public class CacheRepositoryFactoryBean<ID, T extends CacheDataDto>
        implements FactoryBean<CacheRepository<ID, T>>, ApplicationContextAware {

    private final Class<? extends CacheRepository<ID, T>> repositoryInterface;
    private final Class<T> valueType;
    private ApplicationContext applicationContext;

    public CacheRepositoryFactoryBean(
            Class<? extends CacheRepository<ID, T>> repositoryInterface,
            Class<T> valueType) {
        this.repositoryInterface = Objects.requireNonNull(repositoryInterface, "repositoryInterface must not be null");
        this.valueType = Objects.requireNonNull(valueType, "valueType must not be null");
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public CacheRepository<ID, T> getObject() {
        CacheService cacheService = applicationContext.getBean(CacheService.class);
        return CacheRepositoryProxyFactory.createProxy(repositoryInterface, cacheService, valueType);
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryInterface;
    }

}