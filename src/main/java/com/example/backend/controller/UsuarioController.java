package com.example.backend.controller;

import com.example.backend.model.LoginRequest;
import com.example.backend.model.Usuario;
import com.example.backend.model.UsuarioRequest;
import com.example.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // GET /api/usuarios
    @GetMapping
    public List<Usuario> getAll() {
        return service.findAll();
    }

    // GET /api/usuarios/{username}
    @GetMapping("/{username}")
    public Usuario getById(@PathVariable String username) {
        return service.findById(username);
    }

    // POST /api/usuarios
    @PostMapping
    public ResponseEntity<Usuario> create(@Valid @RequestBody UsuarioRequest request) {
        Usuario created = service.create(request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.getUsername())).body(created);
    }

    // PUT /api/usuarios/{username}
    @PutMapping("/{username}")
    public Usuario update(@PathVariable String username, @Valid @RequestBody UsuarioRequest request) {
        return service.update(username, request);
    }

    // DELETE /api/usuarios/{username}
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String username) {
        service.delete(username);
    }

    // POST /api/usuarios/login  { "username": "...", "password": "..." }
    @PostMapping("/login")
    public Usuario login(@Valid @RequestBody LoginRequest request) {
        return service.login(request.username(), request.password());
    }
}
