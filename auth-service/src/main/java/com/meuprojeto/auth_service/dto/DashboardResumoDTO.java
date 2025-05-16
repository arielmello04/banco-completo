package com.meuprojeto.auth_service.dto;

import java.math.BigDecimal;

public record DashboardResumoDTO(
        long totalContas,
        BigDecimal saldoTotal,
        long totalTransacoes,
        long totalDepositos,
        long totalSaques,
        long totalTransferencias,
        long totalClientes,
        long totalGerentes,
        long totalAdmins
) {}