package com.example.backend.service;

import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Cliente;
import com.example.backend.model.Instalacion;
import com.example.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Cliente findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
    }

    @Transactional
    public Cliente create(Cliente cliente) {
        // Acepta el id que genera la app; si no viene, se crea uno nuevo
        if (cliente.getId() == null || cliente.getId().isBlank()) {
            cliente.setId(UUID.randomUUID().toString());
        } else if (repository.existsById(cliente.getId())) {
            throw new ConflictException("Ya existe un cliente con el id: " + cliente.getId());
        }
        cliente.setNombre(cliente.getNombre().trim());
        if (repository.existsByNombreIgnoreCase(cliente.getNombre())) {
            throw new ConflictException("Ya existe un cliente con ese nombre: " + cliente.getNombre());
        }
        validarInstalaciones(cliente.getInstalaciones());
        cliente.getInstalaciones().forEach(i -> i.setId(null));
        return repository.save(cliente);
    }

    /** Reemplaza el nombre y todas las instalaciones (con sus equipos) por las enviadas. */
    @Transactional
    public Cliente update(String id, Cliente data) {
        Cliente cliente = findById(id);
        String nombre = data.getNombre().trim();
        if (repository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictException("Ya existe un cliente con ese nombre: " + nombre);
        }
        validarInstalaciones(data.getInstalaciones());
        cliente.setNombre(nombre);
        // orphanRemoval borra las instalaciones antiguas; las nuevas se insertan
        cliente.getInstalaciones().clear();
        data.getInstalaciones().forEach(i -> {
            i.setId(null);
            cliente.getInstalaciones().add(i);
        });
        return repository.save(cliente);
    }

    /** Borra el cliente con sus instalaciones. Los registros guardan una copia de los nombres y no se afectan. */
    @Transactional
    public void delete(String id) {
        repository.delete(findById(id));
    }

    private void validarInstalaciones(List<Instalacion> instalaciones) {
        Set<String> nombres = new HashSet<>();
        for (Instalacion instalacion : instalaciones) {
            instalacion.setNombre(instalacion.getNombre().trim());
            if (!nombres.add(instalacion.getNombre().toLowerCase())) {
                throw new BadRequestException("La instalación está repetida: " + instalacion.getNombre());
            }
            // Las coordenadas son opcionales, pero si se envían deben venir las dos
            if ((instalacion.getLatitud() == null) != (instalacion.getLongitud() == null)) {
                throw new BadRequestException("Revise la latitud y longitud de " + instalacion.getNombre());
            }
        }
    }
}
