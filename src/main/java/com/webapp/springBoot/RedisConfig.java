package com.webapp.springBoot;


import com.webapp.springBoot.DTO.Users.ListUsersDTO;
import com.webapp.springBoot.DTO.Users.UserResponceDTO;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        //  Подстраховка
        redisCacheConfigurationMap.put(
                "USER_RESPONSE", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(UserResponceDTO.class))));

        //  Подстраховка
        redisCacheConfigurationMap.put(
                "USER_RESPONSE_LIST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ListUsersDTO.class)))
        );
        redisCacheConfigurationMap.put(
                "REGISTER_OAUTH2", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(GoogleUserInfo.class)))
        );
        return RedisCacheManager.builder(redisConnectionFactory).withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }
}
