package com.example.demo.controller;
import com.example.demo.model.Producto;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos ConFun")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Ver todos los productos")
    public List<Producto> getAllProductos() {
        return productoService.getAllProductos();
    }

    // Si el producto no existe devolvemos 404, así React sabe que no se encontró
    @GetMapping("/{id}")
    @Operation(summary = "Ver un producto por su id")
    public ResponseEntity<Producto> getProductoById(@PathVariable String id) {
        Producto producto = productoService.getProductoById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @Operation(summary = "Agregar un producto nuevo")
    public Producto createProducto(@RequestBody Producto producto) {
        return productoService.saveProducto(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un producto")
    public ResponseEntity<Producto> updateProducto(@PathVariable String id, @RequestBody Producto producto) {
        Producto existente = productoService.getProductoById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        existente.setNombre(producto.getNombre());
        existente.setPrecio(producto.getPrecio());
        existente.setImagen(producto.getImagen());
        existente.setDescripcion(producto.getDescripcion());
        existente.setSpecs(producto.getSpecs());
        return ResponseEntity.ok(productoService.saveProducto(existente));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public void deleteProducto(@PathVariable String id) {
        productoService.deleteProducto(id);
    }
}
