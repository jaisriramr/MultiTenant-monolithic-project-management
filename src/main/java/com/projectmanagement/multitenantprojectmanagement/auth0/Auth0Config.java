package com.projectmanagement.multitenantprojectmanagement.auth0;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
@ConfigurationProperties(prefix = "auth0")
@Getter
public class Auth0Config {

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Value("${auth0.client_id}")
    private String clientId;

    @Value("${auth0.client_secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.identifier}")
    private String identifier;

    @Value("${auth0.api_id}")
    private String api_id;

    @Value("${auth0.connection_id}")
    private String connection_id;

    @Bean(name = "auth0RestTemplate")
    public RestTemplate auth0RestTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

}
