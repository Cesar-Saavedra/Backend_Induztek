package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Para comprobar que el backend está arriba (Render lo puede usar como health check). */
@RestController
public class SaludController {

    // GET /  y  GET /api/salud
    @GetMapping({"/", "/api/salud"})
    public Map<String, String> salud() {
        return Map.of("servicio", "Induztek API", "estado", "ok");
    }
}
