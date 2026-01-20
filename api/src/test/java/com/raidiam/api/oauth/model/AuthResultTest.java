package com.raidiam.api.oauth.model;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResultTest {

    @Test
    void success_shouldBeSuccessful() {
        assertThat(AuthResult.SUCCESS.isSuccess()).isTrue();
        assertThat(AuthResult.SUCCESS.errorStatus()).isNull();
    }

    @Test
    void unauthorized_shouldHaveUnauthorizedStatus() {
        AuthResult result = AuthResult.unauthorized();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.errorStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void forbidden_shouldHaveForbiddenStatus() {
        AuthResult result = AuthResult.forbidden();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.errorStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
