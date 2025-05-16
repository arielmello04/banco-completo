package com.meuprojeto.auth_service.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
        String nome,
        String email,
        String role,
        LocalDateTime dataCriacao
) {}
