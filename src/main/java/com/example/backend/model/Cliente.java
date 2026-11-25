package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Cliente con sus instalaciones. Mismo formato JSON que el modelo Cliente de la app:
 * { "id": "uuid", "nombre": "Jumbo", "instalaciones": [ ... ] }
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    /** UUID en texto, igual que en la app. Si no se envía, el backend lo genera. */
    @Id
    @Column(length = 36)
    private String id;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Relación 1 a N: la columna instalaciones.cliente_id apunta a este cliente.
     * orphanRemoval borra las instalaciones que se quitan de la lista al editar.
     */
    @Valid
    @NotEmpty(message = "Agregue al menos una instalación")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "cliente_id", nullable = false)
    @OrderColumn(name = "orden")
    private List<Instalacion> instalaciones = new ArrayList<>();

    public Cliente() {}

    public Cliente(String id, String nombre, List<Instalacion> instalaciones) {
        this.id = id;
        this.nombre = nombre;
        this.instalaciones = new ArrayList<>(instalaciones);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Instalacion> getInstalaciones() { return instalaciones; }
    public void setInstalaciones(List<Instalacion> instalaciones) { this.instalaciones = instalaciones; }
}
