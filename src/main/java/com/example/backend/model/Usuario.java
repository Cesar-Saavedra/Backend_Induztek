package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Equivalente a la tabla Room "usuarios" de la app.
 * La contraseña se guarda como hash SHA-256 y nunca se devuelve en las respuestas.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(length = 50)
    private String username;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 64)
    private String passwordHash;

    public Usuario() {}

    public Usuario(String username, String nombre, Rol rol, String passwordHash) {
        this.username = username;
        this.nombre = nombre;
        this.rol = rol;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
