package com.robottx.todo.config;

import lombok.Data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "valkey-config")
@ConditionalOnProperty(name = "secret-provider", havingValue = "local-config")
@PropertySource(value = "file:${config-directory}/valkey.properties", ignoreResourceNotFound = true)
public class ValkeyConfig {

    private String username;
    private String password;

}
