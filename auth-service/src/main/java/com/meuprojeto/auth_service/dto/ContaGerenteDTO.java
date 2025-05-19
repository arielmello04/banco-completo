package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;

public record ContaGerenteDTO(
        Long id,
        String agencia,
        String numeroConta,
        BigDecimal saldo,
        String nomeUsuario,
        String emailUsuario
) {}
