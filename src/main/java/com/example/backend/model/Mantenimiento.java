package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/** Detalle de un registro de tipo MANTENIMIENTO. Mismos campos que MantenimientoState de la app. */
@Entity
@Table(name = "mantenimientos")
public class Mantenimiento {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoFisicoExterior = EstadoComponente.BUENO;
    private String descripcionFisicoExterior = "";

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoAisladores = EstadoComponente.BUENO;
    private String descripcionAisladores = "";

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoRegletas = EstadoComponente.BUENO;
    private String descripcionRegletas = "";

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoContactores = EstadoComponente.BUENO;
    private String descripcionContactores = "";

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoCables = EstadoComponente.BUENO;
    private String descripcionCables = "";

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoLucesPiloto = EstadoComponente.BUENO;
    private String descripcionLucesPiloto = "";

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoComponente estadoCanaletas = EstadoComponente.BUENO;
    private String descripcionCanaletas = "";

    private boolean sistemaTierraOk = true;
    private boolean sinFugasAceite = true;
    private boolean limpiezaRealizada = false;
    private boolean reajusteTorqueRealizado = false;
    private boolean lubricacionRealizada = false;

    @Column(length = 2000)
    private String observaciones = "";

    /** Referencia a la foto (por ahora un texto; subir la imagen al servidor es un paso aparte). */
    private String fotoUri;

    public Mantenimiento() {}

    /** Copia los datos de otro mantenimiento manteniendo el id (se usa al editar). */
    public void copiarDatosDe(Mantenimiento o) {
        estadoFisicoExterior = o.estadoFisicoExterior;
        descripcionFisicoExterior = o.descripcionFisicoExterior;
        estadoAisladores = o.estadoAisladores;
        descripcionAisladores = o.descripcionAisladores;
        estadoRegletas = o.estadoRegletas;
        descripcionRegletas = o.descripcionRegletas;
        estadoContactores = o.estadoContactores;
        descripcionContactores = o.descripcionContactores;
        estadoCables = o.estadoCables;
        descripcionCables = o.descripcionCables;
        estadoLucesPiloto = o.estadoLucesPiloto;
        descripcionLucesPiloto = o.descripcionLucesPiloto;
        estadoCanaletas = o.estadoCanaletas;
        descripcionCanaletas = o.descripcionCanaletas;
        sistemaTierraOk = o.sistemaTierraOk;
        sinFugasAceite = o.sinFugasAceite;
        limpiezaRealizada = o.limpiezaRealizada;
        reajusteTorqueRealizado = o.reajusteTorqueRealizado;
        lubricacionRealizada = o.lubricacionRealizada;
        observaciones = o.observaciones;
        fotoUri = o.fotoUri;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoComponente getEstadoFisicoExterior() { return estadoFisicoExterior; }
    public void setEstadoFisicoExterior(EstadoComponente v) { this.estadoFisicoExterior = v; }
    public String getDescripcionFisicoExterior() { return descripcionFisicoExterior; }
    public void setDescripcionFisicoExterior(String v) { this.descripcionFisicoExterior = v; }

    public EstadoComponente getEstadoAisladores() { return estadoAisladores; }
    public void setEstadoAisladores(EstadoComponente v) { this.estadoAisladores = v; }
    public String getDescripcionAisladores() { return descripcionAisladores; }
    public void setDescripcionAisladores(String v) { this.descripcionAisladores = v; }

    public EstadoComponente getEstadoRegletas() { return estadoRegletas; }
    public void setEstadoRegletas(EstadoComponente v) { this.estadoRegletas = v; }
    public String getDescripcionRegletas() { return descripcionRegletas; }
    public void setDescripcionRegletas(String v) { this.descripcionRegletas = v; }

    public EstadoComponente getEstadoContactores() { return estadoContactores; }
    public void setEstadoContactores(EstadoComponente v) { this.estadoContactores = v; }
    public String getDescripcionContactores() { return descripcionContactores; }
    public void setDescripcionContactores(String v) { this.descripcionContactores = v; }

    public EstadoComponente getEstadoCables() { return estadoCables; }
    public void setEstadoCables(EstadoComponente v) { this.estadoCables = v; }
    public String getDescripcionCables() { return descripcionCables; }
    public void setDescripcionCables(String v) { this.descripcionCables = v; }

    public EstadoComponente getEstadoLucesPiloto() { return estadoLucesPiloto; }
    public void setEstadoLucesPiloto(EstadoComponente v) { this.estadoLucesPiloto = v; }
    public String getDescripcionLucesPiloto() { return descripcionLucesPiloto; }
    public void setDescripcionLucesPiloto(String v) { this.descripcionLucesPiloto = v; }

    public EstadoComponente getEstadoCanaletas() { return estadoCanaletas; }
    public void setEstadoCanaletas(EstadoComponente v) { this.estadoCanaletas = v; }
    public String getDescripcionCanaletas() { return descripcionCanaletas; }
    public void setDescripcionCanaletas(String v) { this.descripcionCanaletas = v; }

    public boolean isSistemaTierraOk() { return sistemaTierraOk; }
    public void setSistemaTierraOk(boolean v) { this.sistemaTierraOk = v; }
    public boolean isSinFugasAceite() { return sinFugasAceite; }
    public void setSinFugasAceite(boolean v) { this.sinFugasAceite = v; }
    public boolean isLimpiezaRealizada() { return limpiezaRealizada; }
    public void setLimpiezaRealizada(boolean v) { this.limpiezaRealizada = v; }
    public boolean isReajusteTorqueRealizado() { return reajusteTorqueRealizado; }
    public void setReajusteTorqueRealizado(boolean v) { this.reajusteTorqueRealizado = v; }
    public boolean isLubricacionRealizada() { return lubricacionRealizada; }
    public void setLubricacionRealizada(boolean v) { this.lubricacionRealizada = v; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String v) { this.observaciones = v; }

    public String getFotoUri() { return fotoUri; }
    public void setFotoUri(String v) { this.fotoUri = v; }
}
