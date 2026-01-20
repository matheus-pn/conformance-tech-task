package com.raidiam.api.controllers;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.raidiam.api.oauth.model.AuthResult;
import com.raidiam.api.services.AuthService;

@RestController
public class ApiController {

    private final Random random = new Random(System.currentTimeMillis());
    private final AuthService authService;

    public ApiController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(value = "/api/now", produces = "application/json")
    public Map<String, String> now(@RequestHeader("Authorization") String authHeader) {
        AuthResult auth = authService.authorize(authHeader, "time");
        if (!auth.isSuccess()) {
            throw new ResponseStatusException(auth.errorStatus());
        }

        return Map.of(
            "time", String.valueOf(Instant.now())
        );
    }

    @GetMapping(value = "/api/random", produces = "application/json")
    public Map<String, String> random(@RequestHeader("Authorization") String authHeader) {
        AuthResult auth = authService.authorize(authHeader, "random");
        if (!auth.isSuccess()) {
            throw new ResponseStatusException(auth.errorStatus());
        }

        return Map.of(
            "random", String.valueOf(random.nextLong())
        );
    }
}
