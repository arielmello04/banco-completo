package com.meuprojeto.auth_service.controller;

import com.meuprojeto.auth_service.dto.DashboardGraficosDTO;
import com.meuprojeto.auth_service.dto.DashboardResumoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.meuprojeto.auth_service.service.ContaService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ContaService contaService;

    @GetMapping("/relatorios")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Área do ADMIN");
    }
}
