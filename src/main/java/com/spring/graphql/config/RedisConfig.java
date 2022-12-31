package com.spring.graphql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Configuration
public class RedisConfig {

    @Value("${redis.server}")
    private String server;

    @Value("${redis.port}")
    private Integer port;

    @Value("${redis.password}")
    @ToString.Exclude
    private String password;
}
