package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        Long contaOrigemId,
        Long contaDestinoId,
        BigDecimal valor
) {}