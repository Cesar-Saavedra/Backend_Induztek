package com.example.backend.util;

import com.example.backend.model.Mantenimiento;
import com.example.backend.model.PruebaElectrica;
import com.example.backend.model.Registro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Compara un registro antes y después de editarlo y devuelve los nombres de los campos
 * que cambiaron. Mismos nombres que DetectorCambios.kt de la app.
 */
public final class DetectorCambios {

    private DetectorCambios() {}

    public static List<String> comparar(Registro antes, Registro despues) {
        List<String> cambios = new ArrayList<>();
        comparar(cambios, "Cliente", antes.getCliente(), despues.getCliente());
        comparar(cambios, "Instalación", antes.getInstalacion(), despues.getInstalacion());
        comparar(cambios, "Tipo de equipo", antes.getTipoEquipo(), despues.getTipoEquipo());
        comparar(cambios, "Código del equipo", antes.getCodigoEquipo(), despues.getCodigoEquipo());

        if (antes.getMantenimiento() != null && despues.getMantenimiento() != null) {
            mantenimiento(cambios, antes.getMantenimiento(), despues.getMantenimiento());
        }
        if (antes.getPrueba() != null && despues.getPrueba() != null) {
            prueba(cambios, antes.getPrueba(), despues.getPrueba());
        }
        return cambios;
    }

    private static void mantenimiento(List<String> c, Mantenimiento a, Mantenimiento d) {
        comparar(c, "Estado físico exterior",
                List.of(a.getEstadoFisicoExterior(), a.getDescripcionFisicoExterior()),
                List.of(d.getEstadoFisicoExterior(), d.getDescripcionFisicoExterior()));
        comparar(c, "Aisladores / Bushing",
                List.of(a.getEstadoAisladores(), a.getDescripcionAisladores()),
                List.of(d.getEstadoAisladores(), d.getDescripcionAisladores()));
        comparar(c, "Regletas",
                List.of(a.getEstadoRegletas(), a.getDescripcionRegletas()),
                List.of(d.getEstadoRegletas(), d.getDescripcionRegletas()));
        comparar(c, "Contactores",
                List.of(a.getEstadoContactores(), a.getDescripcionContactores()),
                List.of(d.getEstadoContactores(), d.getDescripcionContactores()));
        comparar(c, "Cables",
                List.of(a.getEstadoCables(), a.getDescripcionCables()),
                List.of(d.getEstadoCables(), d.getDescripcionCables()));
        comparar(c, "Luces piloto",
                List.of(a.getEstadoLucesPiloto(), a.getDescripcionLucesPiloto()),
                List.of(d.getEstadoLucesPiloto(), d.getDescripcionLucesPiloto()));
        comparar(c, "Canaletas",
                List.of(a.getEstadoCanaletas(), a.getDescripcionCanaletas()),
                List.of(d.getEstadoCanaletas(), d.getDescripcionCanaletas()));
        comparar(c, "Observaciones", a.getObservaciones(), d.getObservaciones());
        comparar(c, "Fotografía", a.getFotoUri(), d.getFotoUri());
    }

    private static void prueba(List<String> c, PruebaElectrica a, PruebaElectrica d) {
        comparar(c, "Tensión de prueba", a.getTensionPruebaAislamiento(), d.getTensionPruebaAislamiento());
        comparar(c, "Resistencia de aislamiento", a.getResistenciaAislamientoMOhm(), d.getResistenciaAislamientoMOhm());
        comparar(c, "Resistencia de contacto", a.getResistenciaContactoMicroOhm(), d.getResistenciaContactoMicroOhm());
        comparar(c, "Voltaje", a.getVoltajeLineaV(), d.getVoltajeLineaV());
        comparar(c, "Corriente", a.getCorrienteCargaA(), d.getCorrienteCargaA());
        comparar(c, "Veredicto", a.getVeredicto(), d.getVeredicto());
        comparar(c, "Notas técnicas", a.getNotasTecnicas(), d.getNotasTecnicas());
    }

    private static void comparar(List<String> cambios, String campo, Object antes, Object despues) {
        if (!Objects.equals(antes, despues)) cambios.add(campo);
    }
}
