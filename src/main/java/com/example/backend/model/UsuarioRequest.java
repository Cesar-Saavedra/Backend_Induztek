package com.example.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para crear o editar un usuario:
 * { "username": "jperez", "nombre": "Juan Pérez", "rol": "TECNICO", "password": "1234" }
 * Al editar, el username viene en la URL y si "password" llega vacío se mantiene la actual.
 */
public record UsuarioRequest(
        @Size(max = 50, message = "El nombre de usuario no puede superar 50 caracteres")
        @Pattern(regexp = "\\S*", message = "El nombre de usuario no puede tener espacios")
        String username,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        String password
) {}
