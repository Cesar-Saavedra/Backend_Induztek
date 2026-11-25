package com.example.backend.controller;

import com.example.backend.model.Cliente;
import com.example.backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    // GET /api/clientes  (con sus instalaciones, coordenadas y tipos de equipo)
    @GetMapping
    public List<Cliente> getAll() {
        return service.findAll();
    }

    // GET /api/clientes/{id}
    @GetMapping("/{id}")
    public Cliente getById(@PathVariable String id) {
        return service.findById(id);
    }

    // POST /api/clientes
    @PostMapping
    public ResponseEntity<Cliente> create(@Valid @RequestBody Cliente cliente) {
        Cliente created = service.create(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + created.getId())).body(created);
    }

    // PUT /api/clientes/{id}
    @PutMapping("/{id}")
    public Cliente update(@PathVariable String id, @Valid @RequestBody Cliente cliente) {
        return service.update(id, cliente);
    }

    // DELETE /api/clientes/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
