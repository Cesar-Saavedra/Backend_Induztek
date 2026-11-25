package com.example.demo.repository;
import com.example.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

// <Producto, String>: la entidad y el tipo de su id
public interface ProductoRepository extends JpaRepository<Producto, String> {
}
