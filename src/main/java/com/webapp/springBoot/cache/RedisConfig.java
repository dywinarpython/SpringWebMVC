package com.webapp.springBoot.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.DTO.Community.CommunityResponseDTO;
import com.webapp.springBoot.DTO.Community.ListCommunityDTO;
import com.webapp.springBoot.DTO.Feed.ListFeedDTO;
import com.webapp.springBoot.DTO.Friend.ListResponseFriendDTO;
import com.webapp.springBoot.DTO.Users.ListUsersDTO;
import com.webapp.springBoot.DTO.Users.UserResponceDTO;
import com.webapp.springBoot.DTO.Post.ResponseListPostDTO;
import com.webapp.springBoot.DTO.Post.ResponsePostDTO;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jackson2.SecurityJackson2Modules;

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
                "USER_RESPONSE", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(UserResponceDTO.class))));

        //  Подстраховка
        redisCacheConfigurationMap.put(
                "USER_RESPONSE_LIST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ListUsersDTO.class)))
        );
        //  Подстраховка
        redisCacheConfigurationMap.put(
                "COMMUNITY_RESPONSE", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(CommunityResponseDTO.class))));

        //  Подстраховка
        redisCacheConfigurationMap.put(
                "COMMUNITY_RESPONSE_LIST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ListCommunityDTO.class)))
        );


        redisCacheConfigurationMap.put(
                "POST_LIST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ResponseListPostDTO.class)))
        );
        redisCacheConfigurationMap.put(
                "POST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ResponsePostDTO.class)))
        );

        redisCacheConfigurationMap.put(
                "FRIENDS_LIST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ListResponseFriendDTO.class)))
        );
        redisCacheConfigurationMap.put(
                "CHECK_FRIEND", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class)))
        );

        redisCacheConfigurationMap.put(
                "FOLLOWERS_LIST", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ListResponseFriendDTO.class)))
        );
        redisCacheConfigurationMap.put(
                "CHECK_FOLLOWERS", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class)))
        );


        redisCacheConfigurationMap.put(
                "REGISTER_OAUTH2", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(GoogleUserInfo.class)))
        );
        redisCacheConfigurationMap.put(
                "VERIFY_PHONE", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
        );


        ObjectMapper objectMapper = new ObjectMapper();
        // Регистрация модулей Spring Security для UserDetails и токенов
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        Jackson2JsonRedisSerializer<UserDetails> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, UserDetails.class);

        redisCacheConfigurationMap.put(
                "SECURITY", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
        );


        redisCacheConfigurationMap.put(
                "LIST_FEED_USER", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ListFeedDTO.class)))
        );



        redisCacheConfigurationMap.put(
                "REACTION", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Integer.class)))
        );

        return RedisCacheManager.builder(redisConnectionFactory).withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }


}
