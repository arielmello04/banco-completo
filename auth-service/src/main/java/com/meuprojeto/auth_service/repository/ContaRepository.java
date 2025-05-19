package com.meuprojeto.auth_service.repository;

import com.meuprojeto.auth_service.entity.ContaBancaria;
import com.meuprojeto.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ContaRepository extends JpaRepository<ContaBancaria, Long>, JpaSpecificationExecutor<ContaBancaria> {
    List<ContaBancaria> findByUsuario(User usuario);
}
