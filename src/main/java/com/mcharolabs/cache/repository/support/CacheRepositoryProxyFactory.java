package com.mcharolabs.cache.repository.support;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.repository.CacheRepository;
import com.mcharolabs.cache.service.CacheService;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Creates JDK dynamic proxies for {@link CacheRepository} interfaces.
 *
 * @author godfrey.steven
 * @since 2026
 */
public final class CacheRepositoryProxyFactory {

    private CacheRepositoryProxyFactory() {}

    @SuppressWarnings("unchecked")
    public static <ID, T extends CacheDataDto> CacheRepository<ID, T> createProxy(
            Class<? extends CacheRepository<ID, T>> repositoryInterface,
            CacheService cacheService,
            Class<T> valueType) {

        InvocationHandler handler = new CacheRepositoryInvocationHandler<>(cacheService, valueType);
        return (CacheRepository<ID, T>) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class<?>[]{repositoryInterface},
                handler);
    }

    private record CacheRepositoryInvocationHandler<ID, T extends CacheDataDto>(
            CacheService cacheService,
            Class<T> valueType
    ) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                        method.getDeclaringClass(),
                        MethodHandles.lookup());
                return lookup.findSpecial(
                                method.getDeclaringClass(),
                                method.getName(),
                                MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                method.getDeclaringClass())
                        .bindTo(proxy)
                        .invokeWithArguments(args);
            }

            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            @SuppressWarnings("unchecked")
            CacheRepository<ID, T> repo = (CacheRepository<ID, T>) proxy;

            return switch (method.getName()) {
                case "save" -> {
                    @SuppressWarnings("unchecked")
                    ID id = (ID) args[0];
                    @SuppressWarnings("unchecked")
                    T value = (T) args[1];
                    yield cacheService.set(repo.getKey(id), value);
                }
                case "findById" -> {
                    @SuppressWarnings("unchecked")
                    ID id = (ID) args[0];
                    yield cacheService.get(repo.getKey(id), valueType);
                }
                case "deleteById" -> {
                    @SuppressWarnings("unchecked")
                    ID id = (ID) args[0];
                    yield cacheService.delete(repo.getKey(id));
                }
                case "existsById" -> {
                    @SuppressWarnings("unchecked")
                    ID id = (ID) args[0];
                    yield cacheService.get(repo.getKey(id), valueType).isPresent();
                }
                default -> throw new UnsupportedOperationException("Unsupported method: " + method.getName());
            };
        }
    }
}