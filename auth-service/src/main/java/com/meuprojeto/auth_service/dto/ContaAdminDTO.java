package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;

public record ContaAdminDTO(
        Long id,
        String agencia,
        String numeroConta,
        BigDecimal saldo,
        String nomeUsuario,
        String emailUsuario
) {}
