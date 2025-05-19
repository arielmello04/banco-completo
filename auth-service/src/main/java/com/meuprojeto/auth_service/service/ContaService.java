package com.meuprojeto.auth_service.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import com.meuprojeto.auth_service.dto.*;
import com.meuprojeto.auth_service.entity.*;
import com.meuprojeto.auth_service.repository.ContaRepository;
import com.meuprojeto.auth_service.repository.TransacaoRepository;
import com.meuprojeto.auth_service.repository.UserRepository;
import com.meuprojeto.auth_service.spec.ContaSpecs;
import com.meuprojeto.auth_service.spec.TransacaoSpecs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final UserRepository userRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaBancaria criarConta(String email, double saldoInicial) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String agencia = "0001";
        String numeroConta = String.valueOf(System.currentTimeMillis()).substring(6);

        ContaBancaria conta = ContaBancaria.builder()
                .usuario(usuario)
                .saldo(BigDecimal.valueOf(saldoInicial))
                .agencia(agencia)
                .numeroConta(numeroConta)
                .build();

        return contaRepository.save(conta);
    }

    public List<ContaBancaria> listarContasDoUsuario(String email) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return contaRepository.findByUsuario(usuario);
    }

    public ContaBancaria buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
    }

    public void deletarConta(Long id) {
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        ContaBancaria conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));

        if (!conta.getUsuario().getEmail().equals(emailUsuarioLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para deletar esta conta");
        }

        contaRepository.delete(conta);
    }

    @Transactional
    public ContaBancaria depositar(Long contaId, BigDecimal valor) {
        ContaBancaria conta = buscarContaPorId(contaId);
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);

        transacaoRepository.save(Transacao.builder()
                .conta(conta)
                .valor(valor)
                .tipo(TipoTransacao.DEPOSITO)
                .dataHora(LocalDateTime.now())
                .descricao("Depósito em conta")
                .build()
        );

        return conta;
    }

    @Transactional
    public ContaBancaria sacar(Long contaId, BigDecimal valor) {
        ContaBancaria conta = buscarContaPorId(contaId);
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);

        transacaoRepository.save(Transacao.builder()
                .conta(conta)
                .valor(valor)
                .tipo(TipoTransacao.SAQUE)
                .dataHora(LocalDateTime.now())
                .descricao("Saque em conta")
                .build()
        );

        return conta;
    }

    @Transactional
    public void transferir(String emailUsuario, TransferenciaRequestDTO dto) {
        ContaBancaria origem = contaRepository.findById(dto.contaOrigemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada"));

        if (!origem.getUsuario().getEmail().equals(emailUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode transferir de suas próprias contas");
        }

        if (dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor deve ser positivo");
        }

        if (origem.getSaldo().compareTo(dto.valor()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        }

        ContaBancaria destino = contaRepository.findById(dto.contaDestinoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada"));

        origem.setSaldo(origem.getSaldo().subtract(dto.valor()));
        destino.setSaldo(destino.getSaldo().add(dto.valor()));

        contaRepository.save(origem);
        contaRepository.save(destino);

        // Registra transações
        transacaoRepository.save(Transacao.builder()
                .conta(origem)
                .valor(dto.valor())
                .tipo(TipoTransacao.TRANSFERENCIA_ENVIO)
                .dataHora(LocalDateTime.now())
                .descricao("Transferência enviada para conta " + destino.getNumeroConta())
                .build());

        transacaoRepository.save(Transacao.builder()
                .conta(destino)
                .valor(dto.valor())
                .tipo(TipoTransacao.TRANSFERENCIA_RECEBIDA)
                .dataHora(LocalDateTime.now())
                .descricao("Transferência recebida da conta " + origem.getNumeroConta())
                .build());
    }

    public Page<TransacaoDTO> filtrarTransacoes(Long contaId, String emailUsuario,
                                                int page, int size,
                                                String tipo, BigDecimal valorMin, BigDecimal valorMax,
                                                LocalDate dataInicio, LocalDate dataFim) {

        ContaBancaria conta = buscarContaPorId(contaId);
        if (!conta.getUsuario().getEmail().equals(emailUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado à conta");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataHora").descending());

        Specification<Transacao> spec = Specification.where(TransacaoSpecs.porConta(conta))
                .and(TransacaoSpecs.porTipo(tipo))
                .and(TransacaoSpecs.porValorEntre(valorMin, valorMax))
                .and(TransacaoSpecs.porDataEntre(dataInicio, dataFim));

        return transacaoRepository.findAll(spec, pageable)
                .map(t -> new TransacaoDTO(
                        t.getId(),
                        t.getTipo().name(),
                        t.getValor(),
                        t.getDataHora(),
                        t.getDescricao()
                ));
    }


    public List<ContaGerenteDTO> listarTodasContas() {
        return contaRepository.findAll().stream().map(conta ->
                new ContaGerenteDTO(
                        conta.getId(),
                        conta.getAgencia(),
                        conta.getNumeroConta(),
                        conta.getSaldo(),
                        conta.getUsuario().getNome(),
                        conta.getUsuario().getEmail()
                )
        ).toList();
    }

    public List<TransacaoDTO> listarTransacoes(Long contaId, String emailUsuario) {
        ContaBancaria conta = buscarContaPorId(contaId);

        if (!conta.getUsuario().getEmail().equals(emailUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado à conta");
        }

        return transacaoRepository.findByContaOrderByDataHoraDesc(conta)
                .stream()
                .map(t -> new TransacaoDTO(
                        t.getId(),
                        t.getTipo().name(),
                        t.getValor(),
                        t.getDataHora(),
                        t.getDescricao()
                )).toList();
    }

    public List<TransacaoDTO> listarTransacoesFiltradas(
            Long contaId,
            String emailUsuario,
            String tipo,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        ContaBancaria conta = buscarContaPorId(contaId);

        if (!conta.getUsuario().getEmail().equals(emailUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado à conta");
        }

        return transacaoRepository.findByContaOrderByDataHoraDesc(conta).stream()
                .filter(t -> tipo == null || t.getTipo().name().equalsIgnoreCase(tipo))
                .filter(t -> dataInicio == null || !t.getDataHora().toLocalDate().isBefore(dataInicio))
                .filter(t -> dataFim == null || !t.getDataHora().toLocalDate().isAfter(dataFim))
                .map(t -> new TransacaoDTO(
                        t.getId(),
                        t.getTipo().name(),
                        t.getValor(),
                        t.getDataHora(),
                        t.getDescricao()
                ))
                .toList();
    }

    public Page<TransacaoDTO> listarTransacoesFiltrado(
            Long contaId,
            String emailUsuario,
            String tipo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            int page,
            int size
    ) {
        ContaBancaria conta = buscarContaPorId(contaId);

        if (!conta.getUsuario().getEmail().equals(emailUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado à conta");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataHora").descending());

        // Filtro dinâmico usando Specification ou lógica simples
        List<Transacao> todas = transacaoRepository.findByContaOrderByDataHoraDesc(conta);
        Stream<Transacao> stream = todas.stream();

        if (tipo != null) {
            stream = stream.filter(t -> t.getTipo().name().equalsIgnoreCase(tipo));
        }
        if (dataInicio != null) {
            stream = stream.filter(t -> !t.getDataHora().isBefore(dataInicio));
        }
        if (dataFim != null) {
            stream = stream.filter(t -> !t.getDataHora().isAfter(dataFim));
        }

        List<TransacaoDTO> filtradas = stream
                .map(t -> new TransacaoDTO(t.getId(), t.getTipo().name(), t.getValor(), t.getDataHora(), t.getDescricao()))
                .toList();

        int start = Math.min(page * size, filtradas.size());
        int end = Math.min(start + size, filtradas.size());

        return new PageImpl<>(filtradas.subList(start, end), pageable, filtradas.size());
    }

    public byte[] gerarExtratoPdf(Long contaId, String emailUsuario, LocalDate inicio, LocalDate fim) {
        ContaBancaria conta = buscarContaPorId(contaId);
        if (!conta.getUsuario().getEmail().equals(emailUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado à conta");
        }

        List<TransacaoDTO> todas = listarTransacoes(contaId, emailUsuario);

        List<TransacaoDTO> filtradas = todas.stream()
                .filter(t -> {
                    LocalDate data = t.dataHora().toLocalDate();
                    boolean aposInicio = (inicio == null || !data.isBefore(inicio));
                    boolean antesFim = (fim == null || !data.isAfter(fim));
                    return aposInicio && antesFim;
                })
                .toList();

        return gerarPdf(conta, filtradas);
    }

    private byte[] gerarPdf(ContaBancaria conta, List<TransacaoDTO> transacoes) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                            new Phrase("Página " + writer.getPageNumber()),
                            (document.right() - document.left()) / 2 + document.leftMargin(),
                            document.bottom() - 10, 0);
                }
            });

            document.open();

            // Logotipo (opcional, com try/catch em caso de falha)
            try {
                Image logo = Image.getInstance(getClass().getResource("/static/images/logo.png"));
                logo.scaleToFit(100, 50);
                logo.setAlignment(Image.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception ignored) {}

            Font title = new Font(Font.HELVETICA, 20, Font.BOLD);
            document.add(new Paragraph("Extrato Bancário", title));
            document.add(new Paragraph("Cliente: " + conta.getUsuario().getNome()));
            document.add(new Paragraph("Agência: " + conta.getAgencia()));
            document.add(new Paragraph("Conta: " + conta.getNumeroConta()));
            document.add(new Paragraph("Saldo Atual: R$ " + conta.getSaldo()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 2, 2, 4});

            Stream.of("Data", "Tipo", "Valor", "Descrição").forEach(c -> {
                PdfPCell cell = new PdfPCell(new Phrase(c, new Font(Font.HELVETICA, 12, Font.BOLD)));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            });

            for (TransacaoDTO t : transacoes) {
                table.addCell(t.dataHora().toString());
                table.addCell(t.tipo());
                table.addCell("R$ " + t.valor());
                table.addCell(t.descricao());
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    public byte[] exportarContasCsv() {
        List<ContaGerenteDTO> contas = listarTodasContas();

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Agência,Número da Conta,Saldo,Nome do Usuário,Email do Usuário\n");

        for (ContaGerenteDTO conta : contas) {
            sb.append(conta.id()).append(",")
                    .append(conta.agencia()).append(",")
                    .append(conta.numeroConta()).append(",")
                    .append(conta.saldo()).append(",")
                    .append("\"").append(conta.nomeUsuario()).append("\",")
                    .append(conta.emailUsuario())
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] gerarContasPdf() {
        List<ContaGerenteDTO> contas = listarTodasContas();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                            new Phrase("Página " + writer.getPageNumber()),
                            (document.right() - document.left()) / 2 + document.leftMargin(),
                            document.bottom() - 10, 0);
                }
            });

            document.open();

            Font title = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Relatório de Contas Bancárias", title));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2, 2, 2, 3, 4});

            Stream.of("ID", "Agência", "Número", "Saldo", "Usuário", "Email").forEach(col -> {
                PdfPCell cell = new PdfPCell(new Phrase(col, new Font(Font.HELVETICA, 12, Font.BOLD)));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            });

            for (ContaGerenteDTO c : contas) {
                table.addCell(String.valueOf(c.id()));
                table.addCell(c.agencia());
                table.addCell(c.numeroConta());
                table.addCell("R$ " + c.saldo());
                table.addCell(c.nomeUsuario());
                table.addCell(c.emailUsuario());
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF das contas", e);
        }
    }

    public byte[] gerarContasPdfFiltrado(String agencia, String nome, BigDecimal saldoMin, BigDecimal saldoMax) {
        List<ContaGerenteDTO> contas = listarTodasContas().stream()
                .filter(c -> agencia == null || c.agencia().equalsIgnoreCase(agencia))
                .filter(c -> nome == null || c.nomeUsuario().toLowerCase().contains(nome.toLowerCase()))
                .filter(c -> saldoMin == null || c.saldo().compareTo(saldoMin) >= 0)
                .filter(c -> saldoMax == null || c.saldo().compareTo(saldoMax) <= 0)
                .toList();

        return gerarPdfContas(contas);
    }

    private byte[] gerarPdfContas(List<ContaGerenteDTO> contas) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                            new Phrase("Página " + writer.getPageNumber()),
                            (document.right() - document.left()) / 2 + document.leftMargin(),
                            document.bottom() - 10, 0);
                }
            });

            document.open();

            Font title = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Relatório de Contas Bancárias", title));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2, 2, 2, 3, 4});

            Stream.of("ID", "Agência", "Número", "Saldo", "Usuário", "Email").forEach(col -> {
                PdfPCell cell = new PdfPCell(new Phrase(col, new Font(Font.HELVETICA, 12, Font.BOLD)));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            });

            for (ContaGerenteDTO c : contas) {
                table.addCell(String.valueOf(c.id()));
                table.addCell(c.agencia());
                table.addCell(c.numeroConta());
                table.addCell("R$ " + c.saldo());
                table.addCell(c.nomeUsuario());
                table.addCell(c.emailUsuario());
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF filtrado", e);
        }
    }

    public DashboardResumoDTO gerarResumoDashboard(LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes = transacaoRepository.findAll();

        Stream<Transacao> filtradas = transacoes.stream();

        if (dataInicio != null) {
            filtradas = filtradas.filter(t -> !t.getDataHora().toLocalDate().isBefore(dataInicio));
        }
        if (dataFim != null) {
            filtradas = filtradas.filter(t -> !t.getDataHora().toLocalDate().isAfter(dataFim));
        }

        List<Transacao> lista = filtradas.toList();

        long totalDepositos = lista.stream().filter(t -> t.getTipo() == TipoTransacao.DEPOSITO).count();
        long totalSaques = lista.stream().filter(t -> t.getTipo() == TipoTransacao.SAQUE).count();
        long totalTransferencias = lista.stream().filter(t ->
                t.getTipo() == TipoTransacao.TRANSFERENCIA_ENVIO || t.getTipo() == TipoTransacao.TRANSFERENCIA_RECEBIDA).count();

        long totalClientes = userRepository.countByRole(Role.CLIENTE);
        long totalGerentes = userRepository.countByRole(Role.GERENTE);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        return new DashboardResumoDTO(
                contaRepository.count(),
                contaRepository.findAll().stream()
                        .map(ContaBancaria::getSaldo)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                lista.size(),
                totalDepositos,
                totalSaques,
                totalTransferencias,
                totalClientes,
                totalGerentes,
                totalAdmins
        );
    }

    public DashboardGraficosDTO gerarGraficosDashboard(LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes = transacaoRepository.findAll();

        Stream<Transacao> stream = transacoes.stream();

        if (dataInicio != null) {
            stream = stream.filter(t -> !t.getDataHora().toLocalDate().isBefore(dataInicio));
        }
        if (dataFim != null) {
            stream = stream.filter(t -> !t.getDataHora().toLocalDate().isAfter(dataFim));
        }

        List<Transacao> filtradas = stream.toList();

        // Série temporal de transações por dia
        var porDia = filtradas.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDataHora().toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> new SerieTemporalDTO(e.getKey(), BigDecimal.valueOf(e.getValue())))
                .sorted(Comparator.comparing(SerieTemporalDTO::data))
                .toList();

        // Série temporal do saldo total diário (somatório dos valores de transação por dia)
        var saldoDiario = filtradas.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDataHora().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Transacao::getValor, BigDecimal::add)
                ))
                .entrySet().stream()
                .map(e -> new SerieTemporalDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(SerieTemporalDTO::data))
                .toList();

        // Agrupamento por tipo
        var porTipo = filtradas.stream()
                .collect(Collectors.groupingBy(t -> t.getTipo().name(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new ContagemPorTipoDTO(e.getKey(), e.getValue()))
                .toList();

        return new DashboardGraficosDTO(saldoDiario, porDia, porTipo);
    }

    public Page<ContaGerenteDTO> filtrarContas(String agencia, String nome, BigDecimal saldoMin, BigDecimal saldoMax, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<ContaBancaria> spec = Specification
                .where(ContaSpecs.porAgencia(agencia))
                .and(ContaSpecs.porNomeUsuario(nome))
                .and(ContaSpecs.saldoEntre(saldoMin, saldoMax));

        return contaRepository.findAll(spec, pageable)
                .map(conta -> new ContaGerenteDTO(
                        conta.getId(),
                        conta.getAgencia(),
                        conta.getNumeroConta(),
                        conta.getSaldo(),
                        conta.getUsuario().getNome(),
                        conta.getUsuario().getEmail()
                ));
    }

}
