package com.projectmanagement.multitenantprojectmanagement.auth0;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Auth0Client {

    private final Auth0Config auth0Config;
    private final RestTemplate restTemplate;

    private String generateAuth0Token() {
        String url = "https://" + auth0Config.getDomain() + "/oauth/token";

        Map<String, Object> requestBody = Map.of(
                "client_id", auth0Config.getClientId(),
                "client_secret", auth0Config.getClientSecret(),
                "audience", auth0Config.getAudience(),
                "grant_type", "client_credentials");

        try {
            ResponseEntity<Map<String, Object>> response = makeApiRequest(HttpMethod.POST, url, requestBody, false);

            Map<String, Object> body = response.getBody();
            if (body != null) {
                Object tokenObj = body.get("access_token");
                if (tokenObj instanceof String token) {
                    return token;
                }
            }
            return null;
            
        }catch(Exception e) {
            throw new RuntimeException("Failed to get Auth0 token", e);
        }
    }

    public ResponseEntity<Map<String, Object>> makeApiRequest(HttpMethod method, String url, Map<String, Object> body,
            Boolean makeRequest) {
        HttpHeaders headers = new HttpHeaders();

        if (makeRequest) {
            headers.setBearerAuth(generateAuth0Token());
        }

        if(method == HttpMethod.PATCH) {
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<?> entity = (body != null) ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);
        return restTemplate.exchange(url, method, entity, new ParameterizedTypeReference<Map<String, Object>>() {
        });
    }

}
