package com.example.backend.service;

import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.EdicionRegistro;
import com.example.backend.model.Registro;
import com.example.backend.model.TipoRegistro;
import com.example.backend.model.Usuario;
import com.example.backend.repository.RegistroRepository;
import com.example.backend.util.DetectorCambios;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistroService {

    private final RegistroRepository repository;
    private final UsuarioService usuarioService;

    public RegistroService(RegistroRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /**
     * Registros activos o archivados, del más reciente al más antiguo.
     * Los filtros son opcionales; con cliente + instalación + código se obtiene el
     * historial de un equipo (el primero es su registro anterior).
     */
    @Transactional(readOnly = true)
    public List<Registro> findAll(boolean archivados, TipoRegistro tipo, String cliente, String instalacion, String codigoEquipo) {
        return repository.findByArchivadoOrderByFechaMillisDesc(archivados).stream()
                .filter(r -> tipo == null || r.getTipo() == tipo)
                .filter(r -> coincide(cliente, r.getCliente()))
                .filter(r -> coincide(instalacion, r.getInstalacion()))
                .filter(r -> coincide(codigoEquipo, r.getCodigoEquipo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Registro findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado: " + id));
    }

    @Transactional
    public Registro create(Registro registro) {
        if (registro.getId() == null || registro.getId().isBlank()) {
            registro.setId(UUID.randomUUID().toString());
        } else if (repository.existsById(registro.getId())) {
            throw new ConflictException("Ya existe un registro con el id: " + registro.getId());
        }
        validarDetalle(registro);
        Usuario tecnico = buscarUsuario(registro.getTecnicoUsername());
        registro.setTecnicoNombre(tecnico.getNombre());
        if (registro.getFechaMillis() == null) {
            registro.setFechaMillis(System.currentTimeMillis());
        }
        limpiarIds(registro);
        return repository.save(registro);
    }

    /**
     * Edita un registro y deja constancia en el historial de quién lo cambió y qué campos.
     * El técnico original, la fecha y el tipo no cambian. Si no hubo cambios no se registra nada.
     */
    @Transactional
    public Registro update(String id, Registro data, String editorUsername) {
        Registro registro = findById(id);
        if (registro.isArchivado()) {
            throw new ConflictException("Un registro archivado no se puede editar");
        }
        if (data.getTipo() != registro.getTipo()) {
            throw new BadRequestException("El tipo de registro no se puede cambiar");
        }
        validarDetalle(data);
        Usuario editor = buscarUsuario(editorUsername);

        List<String> cambios = DetectorCambios.comparar(registro, data);
        if (cambios.isEmpty()) {
            return registro;
        }

        registro.setCliente(data.getCliente());
        registro.setInstalacion(data.getInstalacion());
        registro.setTipoEquipo(data.getTipoEquipo());
        registro.setCodigoEquipo(data.getCodigoEquipo());
        if (registro.getMantenimiento() != null) {
            registro.getMantenimiento().copiarDatosDe(data.getMantenimiento());
        }
        if (registro.getPrueba() != null) {
            registro.getPrueba().copiarDatosDe(data.getPrueba());
        }
        registro.getEdiciones().add(0, new EdicionRegistro(
                System.currentTimeMillis(), editor.getUsername(), editor.getNombre(), cambios));
        return repository.save(registro);
    }

    /** "Eliminar" un registro: queda archivado y solo los administradores lo ven. */
    @Transactional
    public Registro archivar(String id, String adminUsername) {
        Usuario admin = usuarioService.verificarAdministrador(adminUsername);
        Registro registro = findById(id);
        registro.setArchivado(true);
        registro.setArchivadoPorUsername(admin.getUsername());
        registro.setArchivadoPorNombre(admin.getNombre());
        registro.setArchivadoFechaMillis(System.currentTimeMillis());
        return repository.save(registro);
    }

    @Transactional
    public Registro restaurar(String id, String adminUsername) {
        usuarioService.verificarAdministrador(adminUsername);
        Registro registro = findById(id);
        registro.setArchivado(false);
        registro.setArchivadoPorUsername(null);
        registro.setArchivadoPorNombre(null);
        registro.setArchivadoFechaMillis(null);
        return repository.save(registro);
    }

    // Un mantenimiento trae solo "mantenimiento" y una prueba solo "prueba"
    private void validarDetalle(Registro registro) {
        boolean esMantenimiento = registro.getTipo() == TipoRegistro.MANTENIMIENTO;
        if (esMantenimiento && (registro.getMantenimiento() == null || registro.getPrueba() != null)) {
            throw new BadRequestException("Un registro de mantenimiento debe traer solo el campo 'mantenimiento'");
        }
        if (!esMantenimiento && (registro.getPrueba() == null || registro.getMantenimiento() != null)) {
            throw new BadRequestException("Un registro de pruebas eléctricas debe traer solo el campo 'prueba'");
        }
    }

    private Usuario buscarUsuario(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Falta el usuario que realiza la acción");
        }
        return usuarioService.findById(username);
    }

    private void limpiarIds(Registro registro) {
        if (registro.getMantenimiento() != null) registro.getMantenimiento().setId(null);
        if (registro.getPrueba() != null) registro.getPrueba().setId(null);
    }

    private static boolean coincide(String filtro, String valor) {
        return filtro == null || filtro.isBlank() || filtro.trim().equalsIgnoreCase(valor.trim());
    }
}
