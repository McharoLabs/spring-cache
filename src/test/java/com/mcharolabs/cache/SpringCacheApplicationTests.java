package com.mcharolabs.cache;

import com.mcharolabs.cache.config.CacheAutoConfiguration;
import com.mcharolabs.cache.config.CacheProperties;
import com.mcharolabs.cache.service.CacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {
		SpringCacheApplicationTests.TestRedisConfig.class,
		CacheAutoConfiguration.class
})
class SpringCacheApplicationTests {

	@Configuration
	static class TestRedisConfig {
		@Bean
		StringRedisTemplate stringRedisTemplate() {
			return new StringRedisTemplate(mock(RedisConnectionFactory.class));
		}
	}

	@Autowired
	private CacheService cacheService;

	@Autowired
	private CacheProperties cacheProperties;

	@Test
	void contextLoadsAndRegistersCacheBeans() {
		assertThat(cacheService).isNotNull();
		assertThat(cacheProperties).isNotNull();
		assertThat(cacheProperties.getDefaultTtlSeconds()).isEqualTo(3600);
	}
}