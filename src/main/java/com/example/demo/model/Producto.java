package com.example.demo.model;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// Una consola de la tienda. Tiene la misma forma que tenía
// src/data/productos.js en React, así el front casi no cambia.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Producto {

    // El id es un texto (ej: "consolax") porque la web lo usa en la URL: /productos/consolax
    @Id
    private String id;
    private String nombre;
    private Integer precio;
    private String imagen;
    private String descripcion;

    // Lista de especificaciones. JPA la guarda en una tabla aparte (producto_specs).
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> specs;
}
