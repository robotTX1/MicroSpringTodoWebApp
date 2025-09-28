package com.robottx.todo.service.secret;

import com.robottx.todo.config.OAuth2Config;
import com.robottx.todo.config.ServiceConfig;
import com.robottx.todo.config.ValkeyConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "secret-provider", havingValue = "local-config")
public class LocalSecretService implements SecretService {

    private final ServiceConfig serviceConfig;
    private final ValkeyConfig valkeyConfig;
    private final OAuth2Config oAuth2Config;

    private final Map<String, String> secrets = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Valkey
        secrets.put(serviceConfig.getValkeyUsernameSecretName(), valkeyConfig.getUsername());
        secrets.put(serviceConfig.getValkeyPasswordSecretName(), valkeyConfig.getPassword());
        // OAuth2
        secrets.put(serviceConfig.getApplicationClientSecretName(), oAuth2Config.getClientSecret());
    }

    @Override
    public String getSecret(String secretName) {
        return secrets.get(secretName);
    }

    @Override
    public String getValkeyUsername() {
        return getSecret(serviceConfig.getValkeyUsernameSecretName());
    }

    @Override
    public String getValkeyPassword() {
        return getSecret(serviceConfig.getValkeyPasswordSecretName());
    }

    @Override
    public String getClientSecret() {
        return getSecret(serviceConfig.getApplicationClientSecretName());
    }


}
