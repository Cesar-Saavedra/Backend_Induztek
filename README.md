# Backend Induztek – Registro de Terreno

API REST para la app Android **Induztek – Registro de Terreno**. Guarda en **PostgreSQL** (Neon) los usuarios, clientes con sus instalaciones y equipos, y los registros de mantenimiento y pruebas eléctricas, con su historial de ediciones.

Hecho con **Spring Boot 4** (Java 21), Spring Data JPA y validación con Bean Validation.

**Estudiantes:** César Saavedra y Emilio Rojo

## Estructura
`model` → `repository` → `service` → `controller`, más:
- `exception`: el manejador global de errores (responde siempre un JSON con `status`, `message` y `errors`);
- `config/DataLoader`: los datos de prueba;
- `util`: el hash de contraseñas y el detector de cambios del historial.

| Tabla | Contenido |
|---|---|
| `usuarios` | usuario (PK), nombre, rol (`ADMINISTRADOR` / `TECNICO`), hash SHA-256 de la contraseña |
| `clientes` | id (UUID), nombre único |
| `instalaciones` | nombre, latitud, longitud; pertenece a un cliente |
| `tipos_equipo` | tipos de equipo de cada instalación |
| `registros` | tipo, fecha, técnico, cliente, instalación, equipo, código, datos de archivado |
| `mantenimientos` / `pruebas_electricas` | detalle del registro según su tipo |
| `ediciones_registro` / `edicion_cambios` | historial: quién editó, cuándo y qué campos cambió |

Las tablas las crea Hibernate al arrancar (`ddl-auto=update`).

## Endpoints

### Usuarios
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Listar usuarios |
| GET | `/api/usuarios/{username}` | Ver un usuario |
| POST | `/api/usuarios` | Crear: `{ "username", "nombre", "rol", "password" }` |
| PUT | `/api/usuarios/{username}` | Editar: `{ "nombre", "rol", "password" }`; si `password` va vacío, se mantiene la actual |
| DELETE | `/api/usuarios/{username}` | Eliminar (409 si es el último administrador) |
| POST | `/api/usuarios/login` | `{ "username": "admin", "password": "admin" }`; devuelve el usuario o 401 |

### Clientes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar clientes con sus instalaciones, coordenadas y tipos de equipo |
| GET | `/api/clientes/{id}` | Ver un cliente |
| POST | `/api/clientes` | Crear (el `id` es opcional; si no va, se genera un UUID) |
| PUT | `/api/clientes/{id}` | Editar (reemplaza todas las instalaciones) |
| DELETE | `/api/clientes/{id}` | Eliminar (los registros guardan una copia de los nombres y no se afectan) |

```json
{
  "nombre": "Jumbo",
  "instalaciones": [
    { "nombre": "Jumbo La Florida", "tiposEquipo": ["Transformador"], "latitud": -33.5225, "longitud": -70.5982 }
  ]
}
```

### Registros
No hay DELETE: un administrador **archiva** los registros y los puede **restaurar**.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/registros` | Registros activos, del más reciente al más antiguo |
| GET | `/api/registros?archivados=true` | Registros archivados |
| GET | `/api/registros?cliente=..&instalacion=..&codigoEquipo=..` | Historial de un equipo (el primero es el registro anterior). También acepta `tipo` |
| GET | `/api/registros/{id}` | Ver un registro |
| POST | `/api/registros` | Crear |
| PUT | `/api/registros/{id}?editor={username}` | Editar; agrega una entrada al historial con los campos que cambiaron |
| POST | `/api/registros/{id}/archivar?admin={username}` | Archivar (403 si no es administrador) |
| POST | `/api/registros/{id}/restaurar?admin={username}` | Restaurar |

Ejemplo de mantenimiento:
```json
{
  "tipo": "MANTENIMIENTO",
  "tecnicoUsername": "user",
  "cliente": "Jumbo",
  "instalacion": "Jumbo La Florida",
  "tipoEquipo": "Transformador",
  "codigoEquipo": "T-01",
  "mantenimiento": { "estadoCables": "MALO", "descripcionCables": "Cable pelado", "observaciones": "..." }
}
```

En las pruebas eléctricas, el registro trae el campo `"prueba"` en vez de `"mantenimiento"`:
```json
"prueba": { "resistenciaAislamientoMOhm": "550", "veredicto": "APROBADO", "notasTecnicas": "" }
```

`tecnicoNombre`, `archivado`, `ediciones` y los demás datos de archivado los completa el backend.

### Estado
`GET /` o `GET /api/salud` → `{ "servicio": "Induztek API", "estado": "ok" }`

## Configuración (variables de entorno)
| Variable | Para qué | Valor local por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/induztek` |
| `DB_USER` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de la base de datos | `postgres` |
| `PORT` | Puerto del servidor (Render lo asigna solo) | `8080` |

Con **Neon**, `DB_URL` se arma con el host y la base de datos del panel **Connect**:
```
jdbc:postgresql://ep-xxxxx.region.aws.neon.tech/neondb?sslmode=require
```
El usuario y la contraseña van en `DB_USER` y `DB_PASSWORD`. **Nunca los escribas en el código ni los subas a GitHub.**

## Ejecutar en local
Requisitos: Java 21 y una base de datos PostgreSQL (local o Neon).

```bash
# Linux / Mac
export DB_URL="jdbc:postgresql://ep-xxxxx.region.aws.neon.tech/neondb?sslmode=require"
export DB_USER="..."
export DB_PASSWORD="..."
./mvnw spring-boot:run
```
```powershell
# Windows (PowerShell)
$env:DB_URL="jdbc:postgresql://ep-xxxxx.region.aws.neon.tech/neondb?sslmode=require"
$env:DB_USER="..."
$env:DB_PASSWORD="..."
.\mvnw.cmd spring-boot:run
```

Desde IntelliJ, las variables se configuran en **Run → Edit Configurations → Environment variables**.

Desde el emulador Android, la URL base es `http://10.0.2.2:8080/`.

La primera vez se crean los usuarios `admin/admin` (administrador) y `user/user` (técnico), y los clientes Jumbo y Falabella.

## Tests
```bash
./mvnw test
```
Usan una base H2 en memoria, en modo PostgreSQL, así que no necesitan una base de datos real.

## Publicar en Render
El proyecto incluye un `Dockerfile`, porque Render ejecuta Java mediante Docker:
1. En Render, crear un **Web Service** desde este repositorio, con *Language*: **Docker**.
2. En **Environment**, agregar `DB_URL`, `DB_USER` y `DB_PASSWORD` con los datos de Neon.
3. Opcional: en **Health Check Path**, poner `/api/salud`.

## Pendiente
- **Autenticación con token (JWT).** Hoy el usuario que edita o archiva se envía como parámetro (`editor`, `admin`).
- **Subir las fotos de los mantenimientos al servidor.** Hoy `fotoUri` es solo un texto.
