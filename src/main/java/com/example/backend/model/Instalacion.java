package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Instalación de un cliente, con sus tipos de equipo y su ubicación GPS (opcional).
 * JSON: { "nombre": "...", "tiposEquipo": ["Transformador"], "latitud": -33.41, "longitud": -70.60 }
 */
@Entity
@Table(name = "instalaciones")
public class Instalacion {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la instalación es obligatorio")
    @Column(nullable = false)
    private String nombre;

    /** Tabla tipos_equipo; @OrderColumn conserva el orden en que se agregaron. */
    @NotEmpty(message = "Cada instalación debe tener al menos un tipo de equipo")
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "tipos_equipo", joinColumns = @JoinColumn(name = "instalacion_id"))
    @OrderColumn(name = "orden")
    @Column(name = "nombre", nullable = false)
    private List<@NotBlank(message = "El tipo de equipo no puede estar vacío") String> tiposEquipo = new ArrayList<>();

    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitud;

    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitud;

    public Instalacion() {}

    public Instalacion(String nombre, List<String> tiposEquipo, Double latitud, Double longitud) {
        this.nombre = nombre;
        this.tiposEquipo = new ArrayList<>(tiposEquipo);
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getTiposEquipo() { return tiposEquipo; }
    public void setTiposEquipo(List<String> tiposEquipo) { this.tiposEquipo = tiposEquipo; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}
