// src/test/java/com/meuprojeto/auth_service/AuthControllerTest.java

package com.meuprojeto.auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuprojeto.auth_service.dto.AuthRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginComDadosInvalidos_deveRetornar401() throws Exception {
        AuthRequest request = new AuthRequest("email_inexistente@teste.com", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUsuarioNovo_deveRetornarToken() throws Exception {
        String email = "teste" + System.currentTimeMillis() + "@email.com";

        var request = new com.meuprojeto.auth_service.dto.RegisterRequest(
                "Novo Usuário", email, "123456", null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("CLIENTE"));
    }
}
