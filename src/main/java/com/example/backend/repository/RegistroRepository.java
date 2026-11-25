package com.example.backend.repository;

import com.example.backend.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, String> {
    /** Activos (archivado = false) o archivados (true), del más reciente al más antiguo. */
    List<Registro> findByArchivadoOrderByFechaMillisDesc(boolean archivado);
}
