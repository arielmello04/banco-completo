package com.meuprojeto.auth_service.dto;

import lombok.*;
import com.meuprojeto.auth_service.entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nome;
    private String email;
    private String senha;
    private Role role; // CLIENTE, GERENTE, ADMIN
}
