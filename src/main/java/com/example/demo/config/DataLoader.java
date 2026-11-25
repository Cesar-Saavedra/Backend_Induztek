package com.example.demo.config;
import com.example.demo.model.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

// Se ejecuta al arrancar Spring Boot y carga las 9 consolas,
// pero SOLO si la tabla está vacía (la primera vez).
// Ahora la base es Oracle y los datos quedan guardados: si ya hay productos,
// no hace nada, así no se pisan los que agregaste o editaste.
@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public DataLoader(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            return;
        }
        productoRepository.saveAll(List.of(
            new Producto("consolax", "Consola X", 299990, "/img/consolax.png",
                "Excelente rendimiento para streaming y títulos estándar.",
                List.of("Almacenamiento: 512 GB SSD", "Resolución: Hasta 1440p", "Puertos: 2x USB 3.0, 1x HDMI 2.0", "Conexión: Wi-Fi 5")),
            new Producto("consolay", "Consola Y", 399990, "/img/consolay.png",
                "Potencia gráfica superior para juegos a 60 FPS estables.",
                List.of("Almacenamiento: 1 TB SSD NVMe", "Resolución: 4K nativo", "Puertos: 3x USB-A, 1x USB-C, HDMI 2.1", "Conexión: Wi-Fi 6")),
            new Producto("consolaz", "Consola Z", 499990, "/img/consolaz.png",
                "Nuestra consola más potente con compatibilidad Ray Tracing completa.",
                List.of("Almacenamiento: 2 TB SSD ultra rápido", "Rendimiento: Hasta 120 FPS en 4K", "Audio espacial 3D", "Mandos incluidos: 2 inalámbricos")),
            new Producto("consolaa", "Consola A", 299990, "/img/slider1.png",
                "Edición estándar estilizada con amplia compatibilidad de títulos.",
                List.of("Almacenamiento: 512 GB", "Video: Full HD 1080p", "Refrigeración silenciosa", "Control ergonómico incluido")),
            new Producto("consolab", "Consola B", 399990, "/img/slider2.png",
                "Optimización y diseño futurista para sesiones prolongadas de juego.",
                List.of("Almacenamiento: 1 TB SSD", "Arquitectura gráfica RDNA", "Bajo consumo energético", "Bluetooth 5.2")),
            new Producto("consolac", "Consola C", 499990, "/img/slider3.png",
                "Edición pro con arquitectura multihilo para realidad virtual.",
                List.of("Almacenamiento: 1.5 TB SSD", "Soporte dedicado VR", "Tasa de refresco: 120 Hz", "Salida de audio óptica")),
            new Producto("consola1", "Consola 1", 299990, "/img/consola1.png",
                "Modelo portátil y de sobremesa para todo tipo de jugadores.",
                List.of("Almacenamiento: 64 GB expandible por MicroSD", "Pantalla táctil HD", "Batería: hasta 6 horas", "Base dock con salida HDMI")),
            new Producto("consola2", "Consola 2", 399990, "/img/consola2.png",
                "Plataforma versátil para catálogo digital y juego local cooperativo.",
                List.of("Almacenamiento: 825 GB SSD", "Audio Tempest 3D", "Lector de discos Ultra HD", "Soporte HDR10")),
            new Producto("consola3", "Consola 3", 499990, "/img/consola3.png",
                "La cumbre del procesamiento gráfico con tiempos de carga instantáneos.",
                List.of("Almacenamiento: 1 TB PCIe 4.0", "Resolución: 4K nativo y 8K ready", "Ray Tracing por hardware", "Chasis con flujo térmico vertical"))
        ));
    }
}
