package com.raidiam.api.services;

import com.raidiam.api.oauth.model.AuthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AuthServiceTest {

    private MockRestServiceServer mockServer;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        authService = new AuthService(builder, "http://auth-server/introspect");
    }

    @Test
    void authorize_withInactiveToken_shouldReturnUnauthorized() {
        mockServer.expect(requestTo("http://auth-server/introspect"))
                .andRespond(withSuccess("{\"active\": false}", MediaType.APPLICATION_JSON));

        AuthResult result = authService.authorize("Bearer token", "time");

        assertThat(result.errorStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void authorize_withMissingScope_shouldReturnForbidden() {
        mockServer.expect(requestTo("http://auth-server/introspect"))
                .andRespond(withSuccess("{\"active\": true, \"scope\": \"other\"}", MediaType.APPLICATION_JSON));

        AuthResult result = authService.authorize("Bearer token", "time");

        assertThat(result.errorStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void authorize_withValidTokenAndScope_shouldReturnSuccess() {
        mockServer.expect(requestTo("http://auth-server/introspect"))
                .andRespond(withSuccess("{\"active\": true, \"scope\": \"time\"}", MediaType.APPLICATION_JSON));

        AuthResult result = authService.authorize("Bearer token", "time");

        assertThat(result.isSuccess()).isTrue();
    }
}
