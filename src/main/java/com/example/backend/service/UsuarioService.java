package com.example.backend.service;

import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.model.Rol;
import com.example.backend.model.Usuario;
import com.example.backend.model.UsuarioRequest;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final int LARGO_MINIMO_PASSWORD = 4;

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Usuario findById(String username) {
        return repository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public Usuario login(String username, String password) {
        return repository.findById(username.trim())
                .filter(u -> u.getPasswordHash().equals(PasswordUtil.hash(password)))
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña incorrectos"));
    }

    @Transactional
    public Usuario create(UsuarioRequest request) {
        String username = request.username() == null ? "" : request.username().trim();
        if (username.isEmpty()) {
            throw new BadRequestException("El nombre de usuario es obligatorio");
        }
        if (repository.existsById(username)) {
            throw new ConflictException("Ese nombre de usuario ya existe: " + username);
        }
        validarPassword(request.password(), true);
        Usuario usuario = new Usuario(username, request.nombre().trim(), request.rol(), PasswordUtil.hash(request.password()));
        return repository.save(usuario);
    }

    /** Edita un usuario. El username no cambia; si password viene vacío se mantiene la actual. */
    @Transactional
    public Usuario update(String username, UsuarioRequest request) {
        Usuario usuario = findById(username);
        if (usuario.getRol() == Rol.ADMINISTRADOR && request.rol() != Rol.ADMINISTRADOR) {
            verificarQueNoEsElUltimoAdmin();
        }
        validarPassword(request.password(), false);
        usuario.setNombre(request.nombre().trim());
        usuario.setRol(request.rol());
        if (request.password() != null && !request.password().isEmpty()) {
            usuario.setPasswordHash(PasswordUtil.hash(request.password()));
        }
        return repository.save(usuario);
    }

    @Transactional
    public void delete(String username) {
        Usuario usuario = findById(username);
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            verificarQueNoEsElUltimoAdmin();
        }
        repository.delete(usuario);
    }

    /** Lanza 403 si el usuario no existe o no es administrador (para archivar y restaurar). */
    @Transactional(readOnly = true)
    public Usuario verificarAdministrador(String username) {
        Usuario usuario = repository.findById(username)
                .orElseThrow(() -> new ForbiddenException("Usuario no encontrado: " + username));
        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new ForbiddenException("Solo un administrador puede realizar esta acción");
        }
        return usuario;
    }

    // La app nunca debe quedarse sin administrador
    private void verificarQueNoEsElUltimoAdmin() {
        if (repository.countByRol(Rol.ADMINISTRADOR) <= 1) {
            throw new ConflictException("Debe existir al menos un administrador");
        }
    }

    private void validarPassword(String password, boolean obligatoria) {
        boolean vacia = password == null || password.isEmpty();
        if (obligatoria && vacia) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        if (!vacia && password.length() < LARGO_MINIMO_PASSWORD) {
            throw new BadRequestException("La contraseña debe tener al menos " + LARGO_MINIMO_PASSWORD + " caracteres");
        }
    }
}
