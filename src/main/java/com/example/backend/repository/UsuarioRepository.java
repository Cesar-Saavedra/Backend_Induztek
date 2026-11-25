package com.example.backend.repository;

import com.example.backend.model.Rol;
import com.example.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findAllByOrderByNombreAsc();
    long countByRol(Rol rol);
}
