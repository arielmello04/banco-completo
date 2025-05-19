// src/test/java/com/meuprojeto/auth_service/ContaControllerTest.java

package com.meuprojeto.auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuprojeto.auth_service.dto.AuthRequest;
import com.meuprojeto.auth_service.dto.RegisterRequest;
import com.meuprojeto.auth_service.dto.TransacaoRequestDTO;
import com.meuprojeto.auth_service.entity.Role;
import com.meuprojeto.auth_service.repository.ContaRepository;
import com.meuprojeto.auth_service.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.*;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UserRepository userRepository;

    private String token;

    @BeforeEach
    void autenticarOuRegistrarUsuario() throws Exception {
        String email = "cliente" + System.currentTimeMillis() + "@teste.com";
        String senha = "123456";

        // Registra novo cliente
        var req = new RegisterRequest("Cliente Teste", email, senha, Role.CLIENTE);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    token = objectMapper.readTree(json).get("token").asText();
                });
    }

    @Test
    void criarConta_deveRetornarContaCriada() throws Exception {
        mockMvc.perform(post("/api/cliente/contas")
                        .param("saldoInicial", "500")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value("500.0"));
    }

    @Test
    void listarContas_doClienteLogado() throws Exception {
        // Cria uma conta para garantir que há retorno
        mockMvc.perform(post("/api/cliente/contas")
                        .param("saldoInicial", "100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cliente/contas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deposito_emContaValida() throws Exception {
        // Cria conta
        MvcResult result = mockMvc.perform(post("/api/cliente/contas")
                        .param("saldoInicial", "0")
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        Long contaId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // Realiza depósito
        TransacaoRequestDTO deposito = new TransacaoRequestDTO(new BigDecimal("200"));

        mockMvc.perform(post("/api/cliente/contas/" + contaId + "/deposito")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposito)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value("200.0"));
    }
}
