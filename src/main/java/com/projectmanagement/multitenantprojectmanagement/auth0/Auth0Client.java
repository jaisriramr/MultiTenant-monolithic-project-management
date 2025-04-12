package com.projectmanagement.multitenantprojectmanagement.auth0;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Auth0Client {

    private final Auth0Config auth0Config;
    @Autowired
    @Qualifier("auth0RestTemplate")
    private final RestTemplate restTemplate;

    private String generateAuth0Token() {
        String url = "https://" + auth0Config.getAuth0Domain() + "/oauth/token";
        
        Map<String, Object> requestBody = Map.of(
            "client_id", auth0Config.getClientId(),
            "client_secret", auth0Config.getClientSecret(),
            "audience", auth0Config.getAudience(),
            "grant_type", "client_credentials"
        );

        ResponseEntity<Map<String, Object>> response = makeApiRequest(HttpMethod.POST, url, requestBody, false);

        return response.getBody().get("access_token").toString();
    }

    public ResponseEntity<Map<String, Object>> makeApiRequest(HttpMethod method, String url, Map<String, Object> body, Boolean makeRequest) {
        HttpHeaders headers = new HttpHeaders();

        if(makeRequest) {
            headers.setBasicAuth(generateAuth0Token());
        } 

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = (body != null) ? new HttpEntity<>(body,headers) : new HttpEntity<>(headers);
        return restTemplate.exchange(url, method, entity, new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public ResponseEntity<Map<String, Object>> makeApiCallForPermissions(HttpMethod method, String url, Map<String, List<Map<String, String>>> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(generateAuth0Token());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = (body != null) ? new HttpEntity<>(body,headers) : new HttpEntity<>(headers);
        return restTemplate.exchange(url, method, entity, new ParameterizedTypeReference<Map<String, Object>>() {});
    }

}
