package com.robottx.todo.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "service-config")
@PropertySource(value = "file:${config-directory}/service.properties")
public class ServiceConfig {

    // Valkey
    private String valkeyHostname;
    private Integer valkeyPort;
    private String valkeyUsernameSecretName;
    private String valkeyPasswordSecretName;

    // OAuth
    private String applicationRealm;
    private String applicationClientId;
    private String applicationClientSecretName;
    private String authorizationServerUri;
    private String authorizationServerTokenUri;
    private String authorizationServerJwksUri;

    // Todo App
    private String todoApiUri;

}
