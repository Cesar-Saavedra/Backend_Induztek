package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

/** Una edición de un registro: quién, cuándo y qué campos cambió (historial). */
@Entity
@Table(name = "ediciones_registro")
public class EdicionRegistro {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fechaMillis;

    @Column(nullable = false, length = 50)
    private String editorUsername;

    @Column(nullable = false)
    private String editorNombre;

    /** Nombres de los campos modificados, ej. ["Cables", "Observaciones"]. */
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "edicion_cambios", joinColumns = @JoinColumn(name = "edicion_id"))
    @OrderColumn(name = "orden")
    @Column(name = "campo", nullable = false)
    private List<String> cambios = new ArrayList<>();

    public EdicionRegistro() {}

    public EdicionRegistro(Long fechaMillis, String editorUsername, String editorNombre, List<String> cambios) {
        this.fechaMillis = fechaMillis;
        this.editorUsername = editorUsername;
        this.editorNombre = editorNombre;
        this.cambios = new ArrayList<>(cambios);
    }

    public Long getId() { return id; }
    public Long getFechaMillis() { return fechaMillis; }
    public String getEditorUsername() { return editorUsername; }
    public String getEditorNombre() { return editorNombre; }
    public List<String> getCambios() { return cambios; }
}
