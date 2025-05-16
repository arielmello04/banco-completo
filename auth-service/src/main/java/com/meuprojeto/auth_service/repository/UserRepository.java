package com.meuprojeto.auth_service.repository;

import com.meuprojeto.auth_service.entity.Role;
import com.meuprojeto.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long countByRole(Role role);
}
