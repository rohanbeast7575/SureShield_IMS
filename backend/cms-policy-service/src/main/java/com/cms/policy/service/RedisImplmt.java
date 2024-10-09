package com.cms.policy.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisImplmt {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper(); 
	
	public void setPolicies(String key_policies, Object obj, Long ttl) {
		//ObjectMapper objectMapper = new ObjectMapper();
		try {
			 String jsonString = objectMapper.writeValueAsString(obj); 
			redisTemplate.opsForValue().set(key_policies, jsonString, ttl, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("Exception", e);
		}
	}

	public <T> List<T> getPolicies(String keyPolicies, Class<T> entityClass) {
        try {
            // Get JSON string directly from Redis
           
        	Object redisData = redisTemplate.opsForValue().get(keyPolicies);
           System.out.println(redisData);
        	if (redisData != null && redisData instanceof String) {
                String jsonString = (String) redisData; // Cast to String
                // Deserialize the JSON string to a List of the specified type
                return objectMapper.readValue(jsonString,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass));
            } else {
                return null; // Return an empty list if the key is not found or data is not in the correct format
            }
        } catch (Exception e) {
            log.error("Exception while getting list from Redis for key: " + keyPolicies, e);
            return Collections.emptyList(); // Return an empty list in case of an exception
        }
		//return null;
    }

}
