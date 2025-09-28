package com.robottx.todo.service.secret;

import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleByNameRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleByNameResponse;

import com.robottx.todo.config.ServiceConfig;
import com.robottx.todo.config.VaultConfig;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "secret-provider", havingValue = "vault")
public class VaultSecretService implements SecretService {

    private final SecretsClient secretsClient;
    private final VaultConfig vaultConfig;
    private final ServiceConfig serviceConfig;

    private final Map<String, String> secrets = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try (secretsClient) {
            // Valkey
            fetchSecret(secretsClient, serviceConfig.getValkeyUsernameSecretName());
            fetchSecret(secretsClient, serviceConfig.getValkeyPasswordSecretName());
            // Oauth2
            fetchSecret(secretsClient, serviceConfig.getApplicationClientSecretName());
        }
        log.debug("VaultSecretService initialized");
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

    private void fetchSecret(SecretsClient client, String secretName) {
        secrets.put(secretName, getSecretFromVault(client, secretName));
        log.trace("Fetched secret {}", secretName);
    }

    private String getSecretFromVault(SecretsClient client, String secretName) {
        GetSecretBundleByNameRequest secretBundleRequest = GetSecretBundleByNameRequest.builder()
                .vaultId(vaultConfig.getVaultOcid())
                .secretName(secretName)
                .build();
        GetSecretBundleByNameResponse secretBundle = client.getSecretBundleByName(secretBundleRequest);
        Base64SecretBundleContentDetails secretBundleContent =
                (Base64SecretBundleContentDetails) secretBundle.getSecretBundle().getSecretBundleContent();
        return new String(Base64.getDecoder().decode(secretBundleContent.getContent()));
    }

}
