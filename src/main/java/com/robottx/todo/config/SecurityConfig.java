package com.robottx.todo.config;

import com.oracle.bmc.util.internal.StringUtils;

import com.robottx.todo.service.secret.SecretService;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/landing").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(List<ClientRegistration> clientRegistrations) {
        return new InMemoryClientRegistrationRepository(clientRegistrations);
    }

    @Bean
    public ClientRegistration clientRegistration(ServiceConfig serviceConfig, SecretService secretService) {
        String hostname = serviceConfig.getHostname();
        hostname = StringUtils.isBlank(hostname) ? "{baseUrl}" : hostname;
        return ClientRegistration.withRegistrationId("keycloak")
                .clientId(serviceConfig.getApplicationClientId())
                .clientSecret(secretService.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("%s/login/oauth2/code/{registrationId}".formatted(hostname))
                .scope("openid", "profile", "email")
                .authorizationUri(serviceConfig.getAuthorizationServerUri())
                .tokenUri(serviceConfig.getAuthorizationServerTokenUri())
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri(serviceConfig.getAuthorizationServerJwksUri())
                .clientName("keycloak")
                .build();
    }

}
