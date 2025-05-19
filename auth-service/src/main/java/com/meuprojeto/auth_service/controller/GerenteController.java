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
@RequestMapping("/api/gerente")
@RequiredArgsConstructor
public class GerenteController {

    private final ContaService contaService;

    @GetMapping("/relatorios")
    public ResponseEntity<String> gerenteRelatorios() {
        return ResponseEntity.ok("Área do GERENTE");
    }

}
