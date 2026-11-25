package com.example.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Prueba los endpoints contra una base H2 en memoria (con los datos de prueba del DataLoader). */
@SpringBootTest
class InduztekApiTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private String crearMantenimiento(String codigo) throws Exception {
        MvcResult res = mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content("""
                {"tipo": "MANTENIMIENTO", "tecnicoUsername": "user",
                 "cliente": "Jumbo", "instalacion": "Jumbo La Florida",
                 "tipoEquipo": "Transformador", "codigoEquipo": "%s",
                 "mantenimiento": {"estadoCables": "MALO", "descripcionCables": "Cable pelado", "observaciones": "OK"}}
                """.formatted(codigo)))
                .andExpect(status().isCreated())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        return json.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void saludResponde() throws Exception {
        mvc.perform(get("/api/salud")).andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("ok"));
    }

    @Test
    void loginCorrectoNoDevuelveElHash() throws Exception {
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"admin\", \"password\": \"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"admin\", \"password\": \"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crudDeUsuarios() throws Exception {
        String nuevo = "{\"username\": \"jperez\", \"nombre\": \"Juan Pérez\", \"rol\": \"TECNICO\", \"password\": \"1234\"}";
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(nuevo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.nombre").value("Juan Pérez"));
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(nuevo))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"corto\", \"nombre\": \"X\", \"rol\": \"TECNICO\", \"password\": \"12\"}"))
                .andExpect(status().isBadRequest());

        // Editar sin contraseña mantiene la actual
        mvc.perform(put("/api/usuarios/jperez").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Juan P.\", \"rol\": \"TECNICO\", \"password\": \"\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Juan P."));
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"jperez\", \"password\": \"1234\"}"))
                .andExpect(status().isOk());

        mvc.perform(delete("/api/usuarios/jperez")).andExpect(status().isNoContent());
        mvc.perform(get("/api/usuarios/jperez")).andExpect(status().isNotFound());
    }

    @Test
    void noSePuedeEliminarAlUltimoAdministrador() throws Exception {
        mvc.perform(delete("/api/usuarios/admin")).andExpect(status().isConflict());
        mvc.perform(put("/api/usuarios/admin").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Administrador\", \"rol\": \"TECNICO\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void clientesConInstalacionesYEquipos() throws Exception {
        mvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Jumbo')].instalaciones[0].tiposEquipo[0]").value(hasItem("Transformador")))
                .andExpect(jsonPath("$[?(@.nombre == 'Jumbo')].instalaciones[0].latitud").value(hasItem(-33.4173)));

        MvcResult res = mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre": "Lider", "instalaciones": [
                  {"nombre": "Lider Maipú", "tiposEquipo": ["Transformador", "Tablero"], "latitud": -33.51, "longitud": -70.75}]}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.instalaciones[0].tiposEquipo[1]").value("Tablero"))
                .andReturn();
        String id = res.getResponse().getContentAsString().replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        // Editar reemplaza las instalaciones
        mvc.perform(put("/api/clientes/" + id).contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre": "Lider", "instalaciones": [
                  {"nombre": "Lider Florida", "tiposEquipo": ["Grupo Electrógeno"]},
                  {"nombre": "Lider Ñuñoa", "tiposEquipo": ["Celda"]}]}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instalaciones", hasSize(2)))
                .andExpect(jsonPath("$.instalaciones[1].nombre").value("Lider Ñuñoa"));

        // Validaciones
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Sin instalaciones\", \"instalaciones\": []}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre": "Solo latitud", "instalaciones": [{"nombre": "A", "tiposEquipo": ["X"], "latitud": -33.4}]}
                """))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre": "jumbo", "instalaciones": [{"nombre": "A", "tiposEquipo": ["X"]}]}
                """))
                .andExpect(status().isConflict());

        mvc.perform(delete("/api/clientes/" + id)).andExpect(status().isNoContent());
    }

    @Test
    void crearRegistroCompletaElNombreDelTecnico() throws Exception {
        String id = crearMantenimiento("T-100");
        mvc.perform(get("/api/registros/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tecnicoNombre").value("Usuario de Prueba"))
                .andExpect(jsonPath("$.mantenimiento.estadoCables").value("MALO"))
                .andExpect(jsonPath("$.prueba").doesNotExist())
                .andExpect(jsonPath("$.archivado").value(false));

        // Un mantenimiento no puede traer datos de prueba eléctrica
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content("""
                {"tipo": "MANTENIMIENTO", "tecnicoUsername": "user", "cliente": "Jumbo",
                 "instalacion": "X", "tipoEquipo": "X", "codigoEquipo": "X",
                 "prueba": {"veredicto": "APROBADO"}}
                """))
                .andExpect(status().isBadRequest());

        // Historial de un equipo: filtro por cliente, instalación y código
        mvc.perform(get("/api/registros").param("cliente", "jumbo").param("instalacion", "Jumbo La Florida").param("codigoEquipo", "t-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void editarRegistroDejaHistorial() throws Exception {
        String id = crearMantenimiento("T-200");
        String editado = """
                {"tipo": "MANTENIMIENTO", "tecnicoUsername": "user",
                 "cliente": "Jumbo", "instalacion": "Jumbo La Florida",
                 "tipoEquipo": "Transformador", "codigoEquipo": "T-200",
                 "mantenimiento": {"estadoCables": "BUENO", "descripcionCables": "Cable pelado", "observaciones": "Corregido"}}
                """;
        mvc.perform(put("/api/registros/" + id).param("editor", "admin").contentType(MediaType.APPLICATION_JSON).content(editado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mantenimiento.observaciones").value("Corregido"))
                .andExpect(jsonPath("$.tecnicoNombre").value("Usuario de Prueba"))
                .andExpect(jsonPath("$.ediciones", hasSize(1)))
                .andExpect(jsonPath("$.ediciones[0].editorNombre").value("Administrador"))
                .andExpect(jsonPath("$.ediciones[0].cambios", contains("Cables", "Observaciones")));

        // Guardar sin cambios no agrega otra edición
        mvc.perform(put("/api/registros/" + id).param("editor", "admin").contentType(MediaType.APPLICATION_JSON).content(editado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ediciones", hasSize(1)));

        // Falta el parámetro editor
        mvc.perform(put("/api/registros/" + id).contentType(MediaType.APPLICATION_JSON).content(editado))
                .andExpect(status().isBadRequest());
    }

    @Test
    void soloElAdministradorArchivaYRestaura() throws Exception {
        String id = crearMantenimiento("T-300");

        mvc.perform(post("/api/registros/" + id + "/archivar").param("admin", "user"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/registros/" + id + "/archivar").param("admin", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archivado").value(true))
                .andExpect(jsonPath("$.archivadoPorNombre").value("Administrador"));

        mvc.perform(get("/api/registros").param("codigoEquipo", "T-300")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/api/registros").param("archivados", "true").param("codigoEquipo", "T-300")).andExpect(jsonPath("$", hasSize(1)));

        // Un registro archivado no se puede editar
        mvc.perform(put("/api/registros/" + id).param("editor", "admin").contentType(MediaType.APPLICATION_JSON).content("""
                {"tipo": "MANTENIMIENTO", "tecnicoUsername": "user", "cliente": "Jumbo",
                 "instalacion": "Jumbo La Florida", "tipoEquipo": "Transformador", "codigoEquipo": "T-300",
                 "mantenimiento": {}}
                """))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/registros/" + id + "/restaurar").param("admin", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archivado").value(false));
    }
}
