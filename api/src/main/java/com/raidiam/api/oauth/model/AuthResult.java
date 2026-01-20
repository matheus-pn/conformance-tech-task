package com.raidiam.api.oauth.model;

import org.springframework.http.HttpStatus;

public record AuthResult(HttpStatus errorStatus) {
    public static final AuthResult SUCCESS = new AuthResult(null);

    public boolean isSuccess() {
        return errorStatus == null;
    }

    public static AuthResult unauthorized() {
        return new AuthResult(HttpStatus.UNAUTHORIZED);
    }

    public static AuthResult forbidden() {
        return new AuthResult(HttpStatus.FORBIDDEN);
    }
}
