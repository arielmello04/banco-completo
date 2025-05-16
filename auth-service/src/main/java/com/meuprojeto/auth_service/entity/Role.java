package com.meuprojeto.auth_service.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    CLIENTE, GERENTE, ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
