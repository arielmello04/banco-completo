package com.meuprojeto.auth_service.spec;

import com.meuprojeto.auth_service.entity.ContaBancaria;
import com.meuprojeto.auth_service.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ContaSpecs {

    public static Specification<ContaBancaria> saldoEntre(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("saldo"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("saldo"), min);
            if (max != null) return cb.lessThanOrEqualTo(root.get("saldo"), max);
            return cb.conjunction();
        };
    }

    public static Specification<ContaBancaria> porAgencia(String agencia) {
        return (root, query, cb) -> agencia != null
                ? cb.equal(root.get("agencia"), agencia)
                : cb.conjunction();
    }

    public static Specification<ContaBancaria> porNomeUsuario(String nome) {
        return (root, query, cb) -> nome != null
                ? cb.like(cb.lower(root.get("usuario").get("nome")), "%" + nome.toLowerCase() + "%")
                : cb.conjunction();
    }
}
