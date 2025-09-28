package com.robottx.todo.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;

import com.robottx.todo.service.secret.SecretService;

import java.io.IOException;
import java.time.Clock;

import jakarta.ws.rs.InternalServerErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisStandaloneConfiguration redisConfiguration) {
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(ServiceConfig serviceConfig,
            SecretService secretService) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(serviceConfig.getValkeyHostname());
        redisConfiguration.setPort(serviceConfig.getValkeyPort());
        redisConfiguration.setPassword(secretService.getValkeyPassword());
        return redisConfiguration;
    }

    @Bean
    public RestClient restClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor requestInterceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        return RestClient.builder()
                .requestInterceptor(requestInterceptor)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "secret-provider", havingValue = "vault")
    public SecretsClient secretsClient(AbstractAuthenticationDetailsProvider authenticationDetailsProvider) {
        return SecretsClient.builder().build(authenticationDetailsProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "config-directory", havingValue = "config")
    public AbstractAuthenticationDetailsProvider instancePrincipalDetailsProvider() {
        return InstancePrincipalsAuthenticationDetailsProvider.builder().build();
    }

    @Bean
    @ConditionalOnProperty(name = "secret-provider", havingValue = "vault")
    @ConditionalOnProperty(name = "config-directory", havingValue = "local-config")
    public AbstractAuthenticationDetailsProvider getConfigFileAuthDetailsProvider(
            @Value("${config-directory}") String configDirectory) {
        try {
            ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse("%s/oci.config".formatted(configDirectory));
            return new ConfigFileAuthenticationDetailsProvider(configFile);
        } catch (IOException ex) {
            throw new InternalServerErrorException("Failed to create ConfigFileAuthenticationDetailsProvider", ex);
        }
    }

}
