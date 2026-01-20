package com.raidiam.api.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.raidiam.api.oauth.model.AuthResult;
import com.raidiam.api.oauth.model.IntrospectionResponse;

@Service
public class AuthService {

    @Value("${oauth.introspection.uri}")
    private String introspectionUri;

    private final RestClient restClient = RestClient.create();

    public AuthResult authorize(String authorization, String requiredScope) {
        Optional<String> token = extractBearerToken(authorization);
        if (token.isEmpty()) {
            return AuthResult.unauthorized();
        }

        IntrospectionResponse introspection = introspect(token.get());

        if (introspection == null || !introspection.isActive()) {
            return AuthResult.unauthorized();
        }

        if (!introspection.hasScope(requiredScope)) {
            return AuthResult.forbidden();
        }

        return AuthResult.SUCCESS;
    }

    private Optional<String> extractBearerToken(String authorization) {
        if (authorization == null) {
            return Optional.empty();
        }
        String[] parts = authorization.split(" ");
        if (parts.length != 2 || !parts[0].equals("Bearer")) {
            return Optional.empty();
        }
        return Optional.of(parts[1]);
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
