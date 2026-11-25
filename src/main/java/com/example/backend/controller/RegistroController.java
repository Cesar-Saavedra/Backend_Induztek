package com.example.backend.controller;

import com.example.backend.model.Registro;
import com.example.backend.model.TipoRegistro;
import com.example.backend.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** Registros de terreno. No hay DELETE: un administrador los archiva y los puede restaurar. */
@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    private final RegistroService service;

    public RegistroController(RegistroService service) {
        this.service = service;
    }

    // GET /api/registros?archivados=false&tipo=MANTENIMIENTO&cliente=...&instalacion=...&codigoEquipo=...
    @GetMapping
    public List<Registro> getAll(
            @RequestParam(defaultValue = "false") boolean archivados,
            @RequestParam(required = false) TipoRegistro tipo,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String instalacion,
            @RequestParam(required = false) String codigoEquipo) {
        return service.findAll(archivados, tipo, cliente, instalacion, codigoEquipo);
    }

    // GET /api/registros/{id}
    @GetMapping("/{id}")
    public Registro getById(@PathVariable String id) {
        return service.findById(id);
    }

    // POST /api/registros
    @PostMapping
    public ResponseEntity<Registro> create(@Valid @RequestBody Registro registro) {
        Registro created = service.create(registro);
        return ResponseEntity.created(URI.create("/api/registros/" + created.getId())).body(created);
    }

    // PUT /api/registros/{id}?editor=username
    @PutMapping("/{id}")
    public Registro update(@PathVariable String id, @RequestParam String editor, @Valid @RequestBody Registro registro) {
        return service.update(id, registro, editor);
    }

    // POST /api/registros/{id}/archivar?admin=username
    @PostMapping("/{id}/archivar")
    public Registro archivar(@PathVariable String id, @RequestParam String admin) {
        return service.archivar(id, admin);
    }

    // POST /api/registros/{id}/restaurar?admin=username
    @PostMapping("/{id}/restaurar")
    public Registro restaurar(@PathVariable String id, @RequestParam String admin) {
        return service.restaurar(id, admin);
    }
}
