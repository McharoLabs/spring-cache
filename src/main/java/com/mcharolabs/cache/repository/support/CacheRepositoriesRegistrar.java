package com.mcharolabs.cache.repository.support;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.repository.CacheRepository;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Scans auto-configured packages for {@link CacheRepository} interfaces and registers proxy beans.
 *
 * @author godfrey.steven
 * @since 2026
 */
public class CacheRepositoriesRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private BeanFactory beanFactory;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        Set<String> packages = new LinkedHashSet<>();

        var attrs = importingClassMetadata.getAnnotationAttributes(EnableCacheRepositories.class.getName());
        if (attrs != null) {
            String[] configured = (String[]) attrs.get("basePackages");
            if (configured != null) {
                packages.addAll(Arrays.asList(configured));
            }
        }

        if (packages.isEmpty() && beanFactory != null && AutoConfigurationPackages.has(beanFactory)) {
            packages.addAll(AutoConfigurationPackages.get(beanFactory));
        }

        if (packages.isEmpty()) {
            packages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        AnnotationMetadata metadata = beanDefinition.getMetadata();
                        return metadata.isIndependent() && metadata.isInterface();
                    }
                };
        scanner.addIncludeFilter(new AssignableTypeFilter(CacheRepository.class));

        for (String basePackage : packages) {
            if (!StringUtils.hasText(basePackage)) {
                continue;
            }
            for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                String beanClassName = candidate.getBeanClassName();
                if (beanClassName == null) {
                    continue;
                }
                try {
                    Class<?> repoClass = ClassUtils.forName(beanClassName, getClass().getClassLoader());
                    if (repoClass == CacheRepository.class || !CacheRepository.class.isAssignableFrom(repoClass)) {
                        continue;
                    }
                    Class<? extends CacheDataDto> valueType = resolveValueType(repoClass);

                    BeanDefinitionBuilder builder =
                            BeanDefinitionBuilder.genericBeanDefinition(CacheRepositoryFactoryBean.class);
                    builder.addConstructorArgValue(repoClass);
                    builder.addConstructorArgValue(valueType);

                    String beanName = StringUtils.uncapitalize(repoClass.getSimpleName());
                    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
                } catch (ClassNotFoundException ex) {
                    throw new IllegalStateException("Failed to load cache repository interface: " + beanClassName, ex);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends CacheDataDto> resolveValueType(Class<?> repoClass) {
        for (Type genericInterface : repoClass.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType pt
                    && pt.getRawType() instanceof Class<?> raw
                    && CacheRepository.class.isAssignableFrom(raw)) {
                Type[] args = pt.getActualTypeArguments();
                if (args.length >= 2
                        && args[1] instanceof Class<?> valueClass
                        && CacheDataDto.class.isAssignableFrom(valueClass)) {
                    return (Class<? extends CacheDataDto>) valueClass;
                }
            }
        }
        throw new IllegalStateException("Cannot resolve CacheDataDto type argument for " + repoClass.getName());
    }
}