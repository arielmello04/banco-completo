package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SerieTemporalDTO(LocalDate data, BigDecimal valor) {}

