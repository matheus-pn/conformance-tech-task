package com.raidiam.api.controllers;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ApiController {

    private Random random = new Random(System.currentTimeMillis());

    private void authorize(String authorization) {
        if (authorization == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }
        String[] bearerAuthValues = authorization.split(" ");
        Boolean validBearer = bearerAuthValues.length == 2 && bearerAuthValues[0].equals("Bearer");
        if (!validBearer) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");

        String token = bearerAuthValues[1];

        var formData = new LinkedMultiValueMap<String, String>();
        formData.add("token", token);

        RestClient restClient = RestClient.create();
        var response = restClient.post()
            .uri("http://localhost:8081/token/introspect")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(Map.class);

        Boolean active = (Boolean) response.get("active");
        if (active == null || !active) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }

    @GetMapping(value = "/api/now", produces = "application/json")
    public Map<String, String> now(@RequestHeader("Authorization") String authHeader) {
        authorize(authHeader);

        return Map.of(
            "time", String.valueOf(Instant.now())
        );
    }

    @GetMapping(value = "/api/random", produces = "application/json")
    public Map<String, String> random(@RequestHeader("Authorization") String authHeader) {
        authorize(authHeader);

        return Map.of(
            "random", String.valueOf(random.nextLong())
        );
    }
}
