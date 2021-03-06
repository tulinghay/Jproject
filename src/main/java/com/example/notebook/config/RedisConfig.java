package com.example.notebook.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mapping.model.IdPropertyIdentifierAccessor;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;

/**
 * @Author William
 * @Date 2022/6/27 11:25
 * @Version 1.0
 */
@Configuration
public class RedisConfig {
    //
    //@Bean
    //public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    //    RedisTemplate<String, Object> template = new RedisTemplate<>();
    //    RedisSerializer<String> redisSerializer = new StringRedisSerializer();
    //    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    //    ObjectMapper om = new ObjectMapper();
    //    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    //    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    //    jackson2JsonRedisSerializer.setObjectMapper(om);
    //    template.setConnectionFactory(factory);
    //    //key???????????????
    //    template.setKeySerializer(redisSerializer);
    //    //value?????????
    //    template.setValueSerializer(jackson2JsonRedisSerializer);
    //    //value hashmap?????????
    //    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    //    return template;
    //}
    //
    //@Bean
    //public CacheManager cacheManager(RedisConnectionFactory factory) {
    //    RedisSerializer<String> redisSerializer = new StringRedisSerializer();
    //    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    //    //???????????????????????????????????????
    //    ObjectMapper om = new ObjectMapper();
    //    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    //    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    //    jackson2JsonRedisSerializer.setObjectMapper(om);
    //    // ??????????????????????????????????????????,????????????600???
    //    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
    //            .entryTtl(Duration.ofSeconds(600)) //????????????10?????? ---- ???????????????
    //            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))//??????key??????????????????
    //            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)) //??????value????????????
    //            .disableCachingNullValues();
    //    RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
    //            .cacheDefaults(config)
    //            .build();
    //    return cacheManager;
    //}

    //bean????????????ioc
    @Bean
    @ConditionalOnMissingBean(
            name = {"redisTemplate"}
    )//???????????????????????????RedisTemplate????????????????????????
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate1(RedisConnectionFactory redisConnectionFactory) {
        //?????????RedisTemplate??????????????????????????????redis???????????????????????????!
        //????????????<Object, Object>???????????????????????????<String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    //ConditionalOnMissingBean ??????string???redis????????????????????????????????????????????????????????????bean !,??????????????????????????????bean???????????????????????????
    @ConditionalOnMissingBean
    // ConditionalOnSingleCandidate ????????????????????????bean??????????????????bean
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    // ????????????jdk???????????????????????????json?????????
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        //?????????<String, Object>??????
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        // ??????Jackson2JsonRedisSerialize?????????????????????
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(new LaissezFaireSubTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
