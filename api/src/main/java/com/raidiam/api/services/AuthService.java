package com.raidiam.api.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.raidiam.api.oauth.model.AuthResult;
import com.raidiam.api.oauth.model.IntrospectionResponse;

@Service
public class AuthService {

    private final String introspectionUri;
    private final RestClient restClient;

    public AuthService(
            RestClient.Builder restClientBuilder,
            @Value("${oauth.introspection.uri}") String introspectionUri) {
        this.restClient = restClientBuilder.build();
        this.introspectionUri = introspectionUri;
    }

    public AuthResult authorize(String authorization, String requiredScope) {
        Optional<String> token = extractBearerToken(authorization);
        if (token.isEmpty()) {
            return AuthResult.unauthorized();
        }

        Optional<IntrospectionResponse> introspection = introspect(token.get());

        if (introspection.isEmpty() || !introspection.get().isActive()) {
            return AuthResult.unauthorized();
        }

        if (!introspection.get().hasScope(requiredScope)) {
            return AuthResult.forbidden();
        }

        return AuthResult.SUCCESS;
    }

    private Optional<String> extractBearerToken(String authorization) {
        if (authorization == null) {
            return Optional.empty();
        }
        String[] parts = authorization.split(" ");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer")) {
            return Optional.empty();
        }
        return Optional.of(parts[1]);
    }

    private Optional<IntrospectionResponse> introspect(String token) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", token);

        try {
            IntrospectionResponse response = restClient.post()
                    .uri(introspectionUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(IntrospectionResponse.class);

            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }
}
