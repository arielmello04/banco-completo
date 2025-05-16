package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoDTO(
        Long id,
        String tipo,
        BigDecimal valor,
        LocalDateTime dataHora,
        String descricao
) {}