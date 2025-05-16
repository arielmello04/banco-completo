package com.meuprojeto.auth_service.spec;

import com.meuprojeto.auth_service.entity.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransacaoSpecs {

    public static Specification<Transacao> porConta(ContaBancaria conta) {
        return (root, query, cb) -> cb.equal(root.get("conta"), conta);
    }

    public static Specification<Transacao> porTipo(String tipo) {
        if (tipo == null) return null;
        return (root, query, cb) -> cb.equal(root.get("tipo"), TipoTransacao.valueOf(tipo));
    }

    public static Specification<Transacao> porValorEntre(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("valor"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("valor"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("valor"), max);
            }
            return null;
        };
    }

    public static Specification<Transacao> porDataEntre(LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> {
            if (inicio != null && fim != null) {
                return cb.between(root.get("dataHora"), inicio.atStartOfDay(), fim.atTime(23, 59, 59));
            } else if (inicio != null) {
                return cb.greaterThanOrEqualTo(root.get("dataHora"), inicio.atStartOfDay());
            } else if (fim != null) {
                return cb.lessThanOrEqualTo(root.get("dataHora"), fim.atTime(23, 59, 59));
            }
            return null;
        };
    }
}
