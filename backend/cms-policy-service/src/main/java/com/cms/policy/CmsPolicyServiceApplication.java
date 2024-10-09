package com.cms.policy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class CmsPolicyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmsPolicyServiceApplication.class, args);
	}

}
