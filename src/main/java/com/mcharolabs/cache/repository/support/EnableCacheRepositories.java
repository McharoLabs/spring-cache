package com.mcharolabs.cache.repository.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enables automatic Spring bean registration for {@code CacheRepository} interfaces.
 *
 * @author godfrey.steven
 * @since 2026
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CacheRepositoriesRegistrar.class)
public @interface EnableCacheRepositories {
    String[] basePackages() default {};
}