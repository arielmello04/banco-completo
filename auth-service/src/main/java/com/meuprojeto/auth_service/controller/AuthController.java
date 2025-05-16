package com.meuprojeto.auth_service.controller;

import com.meuprojeto.auth_service.dto.*;
import com.meuprojeto.auth_service.entity.User;
import com.meuprojeto.auth_service.repository.UserRepository;
import com.meuprojeto.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        UserResponseDTO response = new UserResponseDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getDataCriacao()
        );

        return ResponseEntity.ok(response);
    }
}
