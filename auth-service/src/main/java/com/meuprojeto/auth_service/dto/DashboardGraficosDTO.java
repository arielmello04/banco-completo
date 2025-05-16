package com.meuprojeto.auth_service.dto;

import java.util.List;

public record DashboardGraficosDTO(
        List<SerieTemporalDTO> saldoTotalPorDia,
        List<SerieTemporalDTO> transacoesPorDia,
        List<ContagemPorTipoDTO> transacoesPorTipo
) {}
