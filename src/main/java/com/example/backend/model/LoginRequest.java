package com.example.backend.model;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo de la petición de login: { "username": "...", "password": "..." } */
public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password
) {}
