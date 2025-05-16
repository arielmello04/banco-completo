package com.meuprojeto.auth_service.controller;

import com.meuprojeto.auth_service.dto.*;
import com.meuprojeto.auth_service.entity.ContaBancaria;
import com.meuprojeto.auth_service.repository.ContaRepository;
import com.meuprojeto.auth_service.service.ContaService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cliente/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;
    private final ContaRepository contaRepository;

    @PostMapping
    public ResponseEntity<ContaResponseDTO> criarConta(@RequestParam double saldoInicial) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ContaBancaria novaConta = contaService.criarConta(email, saldoInicial);
        return ResponseEntity.ok(toDTO(novaConta));
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listarMinhasContas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ContaBancaria> contas = contaService.listarContasDoUsuario(email);
        List<ContaResponseDTO> dtos = contas.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ContaResponseDTO> buscarPorId(@PathVariable Long id) {
        ContaBancaria conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        return ResponseEntity.ok(toDTO(conta));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> deletarConta(@PathVariable Long id) {
        contaService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{id}/deposito")
    public ResponseEntity<ContaResponseDTO> depositar(@PathVariable Long id,
                                                      @RequestBody TransacaoRequestDTO request) {
        ContaBancaria conta = contaService.depositar(id, request.valor());
        return ResponseEntity.ok(toDTO(conta));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{id}/saque")
    public ResponseEntity<ContaResponseDTO> sacar(@PathVariable Long id,
                                                  @RequestBody TransacaoRequestDTO request) {
        ContaBancaria conta = contaService.sacar(id, request.valor());
        return ResponseEntity.ok(toDTO(conta));
    }

    @PostMapping("/transferir")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> transferir(@RequestBody TransferenciaRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        contaService.transferir(email, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/extrato")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<TransacaoDTO>> extrato(
            @PathVariable Long id,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<TransacaoDTO> extrato = contaService.listarTransacoesFiltrado(id, email, tipo, dataInicio, dataFim, page, size);
        return ResponseEntity.ok(extrato);
    }


    private ContaResponseDTO toDTO(ContaBancaria conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getAgencia(),
                conta.getNumeroConta(),
                conta.getSaldo()
        );
    }

    @GetMapping("/{id}/exportar-csv")
    @PreAuthorize("hasRole('CLIENTE')")
    public void exportarCsvComFiltro(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String tipo,
            HttpServletResponse response
    ) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<TransacaoDTO> transacoes = contaService.listarTransacoesFiltradas(id, email, tipo, inicio, fim);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"extrato.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,Data,Tipo,Valor,Descrição");
            for (TransacaoDTO t : transacoes) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        t.id(),
                        t.dataHora(),
                        t.tipo(),
                        t.valor(),
                        t.descricao().replace(",", " ")
                );
            }
        }
    }


    @GetMapping("/{id}/extrato/pdf")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<byte[]> gerarExtratoPdf(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] pdf = contaService.gerarExtratoPdf(id, email, dataInicio, dataFim);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=extrato_conta_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }


}
