package com.store.application.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerFactoryConfig {
    @Bean
    public LoggerFactoryConfig loggerFactory() {
        return new LoggerFactoryConfig();
    }

    public Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
