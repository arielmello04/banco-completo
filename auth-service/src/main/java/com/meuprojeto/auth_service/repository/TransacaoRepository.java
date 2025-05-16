package com.meuprojeto.auth_service.repository;

import com.meuprojeto.auth_service.entity.Transacao;
import com.meuprojeto.auth_service.entity.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long>, JpaSpecificationExecutor<Transacao> {
    List<Transacao> findByContaOrderByDataHoraDesc(ContaBancaria conta);
}
