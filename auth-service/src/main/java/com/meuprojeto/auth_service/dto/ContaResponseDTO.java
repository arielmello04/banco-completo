package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;

public record ContaResponseDTO(
        Long id,
        String agencia,
        String numeroConta,
        BigDecimal saldo
) {}