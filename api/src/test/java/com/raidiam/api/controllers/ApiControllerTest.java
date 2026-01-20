package com.raidiam.api.controllers;

import com.raidiam.api.oauth.model.AuthResult;
import com.raidiam.api.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void now_withValidAuthorization_shouldReturnTime() throws Exception {
        when(authService.authorize("Bearer token", "time")).thenReturn(AuthResult.SUCCESS);

        mockMvc.perform(get("/api/now").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value(notNullValue()));
    }

    @Test
    void now_withUnauthorized_shouldReturn401() throws Exception {
        when(authService.authorize(null, "time")).thenReturn(AuthResult.unauthorized());

        mockMvc.perform(get("/api/now"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void now_withForbidden_shouldReturn403() throws Exception {
        when(authService.authorize("Bearer token", "time")).thenReturn(AuthResult.forbidden());

        mockMvc.perform(get("/api/now").header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void random_withValidAuthorization_shouldReturnNumber() throws Exception {
        when(authService.authorize("Bearer token", "random")).thenReturn(AuthResult.SUCCESS);

        mockMvc.perform(get("/api/random").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.random").value(notNullValue()));
    }

    @Test
    void random_withUnauthorized_shouldReturn401() throws Exception {
        when(authService.authorize(null, "random")).thenReturn(AuthResult.unauthorized());

        mockMvc.perform(get("/api/random"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void random_withForbidden_shouldReturn403() throws Exception {
        when(authService.authorize("Bearer token", "random")).thenReturn(AuthResult.forbidden());

        mockMvc.perform(get("/api/random").header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }
}
