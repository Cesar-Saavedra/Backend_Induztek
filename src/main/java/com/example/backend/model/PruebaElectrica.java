package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/** Detalle de un registro de tipo PRUEBAS_ELECTRICAS. Mismos campos que PruebasElectricasState de la app. */
@Entity
@Table(name = "pruebas_electricas")
public class PruebaElectrica {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Las mediciones se guardan como texto, igual que en la app
    private String tensionPruebaAislamiento = "1000 V";
    private String resistenciaAislamientoMOhm = "";
    private String resistenciaContactoMicroOhm = "";
    private String voltajeLineaV = "";
    private String corrienteCargaA = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VeredictoPrueba veredicto = VeredictoPrueba.APROBADO;

    @Column(length = 2000)
    private String notasTecnicas = "";

    public PruebaElectrica() {}

    /** Copia los datos de otra prueba manteniendo el id (se usa al editar). */
    public void copiarDatosDe(PruebaElectrica o) {
        tensionPruebaAislamiento = o.tensionPruebaAislamiento;
        resistenciaAislamientoMOhm = o.resistenciaAislamientoMOhm;
        resistenciaContactoMicroOhm = o.resistenciaContactoMicroOhm;
        voltajeLineaV = o.voltajeLineaV;
        corrienteCargaA = o.corrienteCargaA;
        veredicto = o.veredicto;
        notasTecnicas = o.notasTecnicas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTensionPruebaAislamiento() { return tensionPruebaAislamiento; }
    public void setTensionPruebaAislamiento(String v) { this.tensionPruebaAislamiento = v; }

    public String getResistenciaAislamientoMOhm() { return resistenciaAislamientoMOhm; }
    public void setResistenciaAislamientoMOhm(String v) { this.resistenciaAislamientoMOhm = v; }

    public String getResistenciaContactoMicroOhm() { return resistenciaContactoMicroOhm; }
    public void setResistenciaContactoMicroOhm(String v) { this.resistenciaContactoMicroOhm = v; }

    public String getVoltajeLineaV() { return voltajeLineaV; }
    public void setVoltajeLineaV(String v) { this.voltajeLineaV = v; }

    public String getCorrienteCargaA() { return corrienteCargaA; }
    public void setCorrienteCargaA(String v) { this.corrienteCargaA = v; }

    public VeredictoPrueba getVeredicto() { return veredicto; }
    public void setVeredicto(VeredictoPrueba v) { this.veredicto = v; }

    public String getNotasTecnicas() { return notasTecnicas; }
    public void setNotasTecnicas(String v) { this.notasTecnicas = v; }
}
