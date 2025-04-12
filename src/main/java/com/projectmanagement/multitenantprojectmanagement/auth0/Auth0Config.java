package com.projectmanagement.multitenantprojectmanagement.auth0;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "auth0")
@Data
public class Auth0Config {

    private String domain;

    private String clientId;

    private String clientSecret;

    private String audience;

    private String identifier;

    private String apiId;

    private String connectionId;

    @Bean(name = "auth0RestTemplate")
    public RestTemplate auth0RestTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

}
