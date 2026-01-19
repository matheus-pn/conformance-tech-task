package com.raidiam.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.raidiam.api.oauth.model.IntrospectionResponse;

@Service
public class AuthService {

    @Value("${oauth.introspection.uri}")
    private String introspectionUri;

    private final RestClient restClient = RestClient.create();

    public void authorize(String authorization, String requiredScope) {
        String token = extractBearerToken(authorization);
        IntrospectionResponse introspection = introspect(token);

        if (introspection == null || !introspection.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!introspection.hasScope(requiredScope)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String[] parts = authorization.split(" ");
        if (parts.length != 2 || !parts[0].equals("Bearer")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return parts[1];
    }

    private IntrospectionResponse introspect(String token) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", token);

        return restClient.post()
                .uri(introspectionUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(IntrospectionResponse.class);
    }
}
