package com.example.backend.config;

import com.example.backend.model.Cliente;
import com.example.backend.model.Instalacion;
import com.example.backend.model.Rol;
import com.example.backend.model.Usuario;
import com.example.backend.repository.ClienteRepository;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Carga los mismos datos de prueba que la app la primera vez que arranca el backend.
 * Solo si las tablas están vacías, así no se pisan los datos reales.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public DataLoader(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    new Usuario("admin", "Administrador", Rol.ADMINISTRADOR, PasswordUtil.hash("admin")),
                    new Usuario("user", "Usuario de Prueba", Rol.TECNICO, PasswordUtil.hash("user"))
            ));
        }
        if (clienteRepository.count() == 0) {
            clienteRepository.saveAll(List.of(
                    new Cliente(UUID.randomUUID().toString(), "Jumbo", List.of(
                            new Instalacion("Jumbo Costanera Center", List.of("Transformador", "Tablero Eléctrico"), -33.4173, -70.6062),
                            new Instalacion("Jumbo La Florida", List.of("Transformador", "Banco de Condensadores"), -33.5225, -70.5982)
                    )),
                    new Cliente(UUID.randomUUID().toString(), "Falabella", List.of(
                            new Instalacion("Falabella Parque Arauco", List.of("Celda de Media Tensión", "Grupo Electrógeno"), -33.4018, -70.5786),
                            new Instalacion("Centro de Distribución Quilicura", List.of("Transformador", "Celda de Media Tensión"), -33.3610, -70.7320)
                    ))
            ));
        }
    }
}
