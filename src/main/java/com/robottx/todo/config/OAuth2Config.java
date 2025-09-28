package com.robottx.todo.config;

import lombok.Data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth-config")
@ConditionalOnProperty(name = "secret-provider", havingValue = "local-config")
@PropertySource(value = "file:${config-directory}/oauth.properties", ignoreResourceNotFound = true)
public class OAuth2Config {

    private String clientSecret;

}
