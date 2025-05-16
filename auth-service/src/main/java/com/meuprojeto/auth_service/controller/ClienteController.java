package com.meuprojeto.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @GetMapping("/perfil")
    public ResponseEntity<String> clientePerfil() {
        return ResponseEntity.ok("Área do CLIENTE");
    }
}
