package com.meuprojeto.auth_service.controller;

import com.meuprojeto.auth_service.dto.ContaGerenteDTO;
import com.meuprojeto.auth_service.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/gerente/contas")
@RequiredArgsConstructor
public class GerenteContaController {

    private final ContaService contaService;

    @GetMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<ContaGerenteDTO>> listarTodasContas() {
        List<ContaGerenteDTO> contas = contaService.listarTodasContas();
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/exportar/csv")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<byte[]> exportarContasCsv() {
        byte[] csv = contaService.exportarContasCsv();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=contas.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(csv);
    }

    @GetMapping("/exportar/pdf")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<byte[]> exportarContasPdf() {
        byte[] pdf = contaService.gerarContasPdf();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=contas.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @GetMapping("/exportar/pdf/filtrado")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<byte[]> exportarContasPdfComFiltro(
            @RequestParam(required = false) String agencia,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) BigDecimal saldoMin,
            @RequestParam(required = false) BigDecimal saldoMax
    ) {
        byte[] pdf = contaService.gerarContasPdfFiltrado(agencia, nome, saldoMin, saldoMax);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=relatorio-contas.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @GetMapping("/filtro")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Page<ContaGerenteDTO>> filtrarContas(
            @RequestParam(required = false) String agencia,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) BigDecimal saldoMin,
            @RequestParam(required = false) BigDecimal saldoMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(contaService.filtrarContas(agencia, nome, saldoMin, saldoMax, page, size));
    }


}
