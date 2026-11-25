package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro de terreno (mantenimiento o prueba eléctrica).
 * Guarda una copia de los nombres de cliente, instalación y técnico, así el historial
 * no cambia si después se editan, igual que en la app.
 * Los registros no se borran: un administrador los archiva (borrado lógico).
 */
@Entity
@Table(name = "registros", indexes = @Index(columnList = "cliente, instalacion, codigoEquipo"))
public class Registro {

    /** UUID en texto, igual que en la app. Si no se envía, el backend lo genera. */
    @Id
    @Column(length = 36)
    private String id;

    @NotNull(message = "El tipo de registro es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoRegistro tipo;

    /** Fecha de creación en milisegundos (como System.currentTimeMillis() en Android). */
    @Column(nullable = false)
    private Long fechaMillis;

    @NotBlank(message = "El técnico es obligatorio")
    @Column(nullable = false, length = 50)
    private String tecnicoUsername;

    /** Lo completa el backend a partir del usuario. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private String tecnicoNombre;

    @NotBlank(message = "El cliente es obligatorio")
    @Column(nullable = false)
    private String cliente;

    @NotBlank(message = "La instalación es obligatoria")
    @Column(nullable = false)
    private String instalacion;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Column(nullable = false)
    private String tipoEquipo;

    @NotBlank(message = "El código del equipo es obligatorio")
    @Column(nullable = false)
    private String codigoEquipo;

    /** Solo viene informado si tipo = MANTENIMIENTO. */
    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "mantenimiento_id")
    private Mantenimiento mantenimiento;

    /** Solo viene informado si tipo = PRUEBAS_ELECTRICAS. */
    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "prueba_id")
    private PruebaElectrica prueba;

    // --- Archivado (solo administradores). Los maneja el backend, no se leen del JSON ---
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean archivado = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 50)
    private String archivadoPorUsername;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String archivadoPorNombre;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long archivadoFechaMillis;

    /** Historial de ediciones, de la más reciente a la más antigua. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "registro_id", nullable = false)
    @OrderBy("fechaMillis DESC")
    private List<EdicionRegistro> ediciones = new ArrayList<>();

    public Registro() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoRegistro getTipo() { return tipo; }
    public void setTipo(TipoRegistro tipo) { this.tipo = tipo; }

    public Long getFechaMillis() { return fechaMillis; }
    public void setFechaMillis(Long fechaMillis) { this.fechaMillis = fechaMillis; }

    public String getTecnicoUsername() { return tecnicoUsername; }
    public void setTecnicoUsername(String tecnicoUsername) { this.tecnicoUsername = tecnicoUsername; }

    public String getTecnicoNombre() { return tecnicoNombre; }
    public void setTecnicoNombre(String tecnicoNombre) { this.tecnicoNombre = tecnicoNombre; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getInstalacion() { return instalacion; }
    public void setInstalacion(String instalacion) { this.instalacion = instalacion; }

    public String getTipoEquipo() { return tipoEquipo; }
    public void setTipoEquipo(String tipoEquipo) { this.tipoEquipo = tipoEquipo; }

    public String getCodigoEquipo() { return codigoEquipo; }
    public void setCodigoEquipo(String codigoEquipo) { this.codigoEquipo = codigoEquipo; }

    public Mantenimiento getMantenimiento() { return mantenimiento; }
    public void setMantenimiento(Mantenimiento mantenimiento) { this.mantenimiento = mantenimiento; }

    public PruebaElectrica getPrueba() { return prueba; }
    public void setPrueba(PruebaElectrica prueba) { this.prueba = prueba; }

    public boolean isArchivado() { return archivado; }
    public void setArchivado(boolean archivado) { this.archivado = archivado; }

    public String getArchivadoPorUsername() { return archivadoPorUsername; }
    public void setArchivadoPorUsername(String v) { this.archivadoPorUsername = v; }

    public String getArchivadoPorNombre() { return archivadoPorNombre; }
    public void setArchivadoPorNombre(String v) { this.archivadoPorNombre = v; }

    public Long getArchivadoFechaMillis() { return archivadoFechaMillis; }
    public void setArchivadoFechaMillis(Long v) { this.archivadoFechaMillis = v; }

    public List<EdicionRegistro> getEdiciones() { return ediciones; }
    public void setEdiciones(List<EdicionRegistro> ediciones) { this.ediciones = ediciones; }
}
