package com.cms.policy.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Set the key serializer to String
        template.setKeySerializer(new StringRedisSerializer());
        
        // Set the hash key serializer to String
        template.setHashKeySerializer(new StringRedisSerializer());

        // Set the value serializer to Generic Jackson JSON serializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        // Set the hash value serializer to Generic Jackson JSON serializer
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Ensure the template is fully initialized
        template.afterPropertiesSet();

        return template;
    }
}
