# Backend de Productos (ConFun)

Este backend le entrega las consolas a la app React (`confun-react`).

## Archivos nuevos (en `src/main/java/com/example/demo/`)

| Archivo | Para qué sirve |
|---|---|
| `model/Producto.java` | La entidad: cómo es un producto (id, nombre, precio, imagen, descripcion, specs). |
| `repository/ProductoRepository.java` | Habla con la base de datos (JPA hace todo el SQL solo). |
| `service/ProductoService.java` | La lógica: listar, buscar, guardar y borrar. |
| `controller/ProductoController.java` | Las URLs de la API (`/api/productos`). |
| `config/CorsConfig.java` | Permite que `localhost:3000` (React) le pida datos a `localhost:8080`. |
| `config/DataLoader.java` | Carga las 9 consolas cada vez que arranca el servidor. |

La base de datos es **Oracle Autonomous Database** (`BDConfun`). Los datos quedan guardados aunque apagues el backend. Las 9 consolas se cargan solo la primera vez, cuando la tabla está vacía.

### Configurar la conexión (una sola vez)

1. La carpeta `wallet` (lo que venía en el zip del Wallet) va dentro de esta carpeta `backend`.
2. Abre `db.properties` (al lado de `pom.xml`) y cambia `ESCRIBE_AQUI_TU_CLAVE` por la clave del usuario ADMIN de la base.
3. `wallet/` y `db.properties` están en `.gitignore`: **no los subas a GitHub**, son como la llave de la base.

## Cómo levantar todo

1. Backend: en una terminal de VS Code, dentro de esta carpeta (`backend`):
   ```powershell
   .\mvnw spring-boot:run
   ```
   Espera a ver `Started DemoApplication`.
2. Frontend: en otra terminal, dentro de `confun-react`:
   ```powershell
   npm run dev
   ```
3. Abre http://localhost:3000/productos

Para apagar un servidor: haz clic en su terminal y presiona `Ctrl + C`.

### Si dice que el puerto 8080 (o 3000) está ocupado

Significa que quedó encendido un servidor de antes. Para ver quién lo usa y cerrarlo:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen | Select-Object OwningProcess
Stop-Process -Id NUMERO
```

(cambia `NUMERO` por el que salió en `OwningProcess`, y usa 3000 en vez de 8080 para el front).

## URLs de la API

| Método | URL | Qué hace |
|---|---|---|
| GET | http://localhost:8080/api/productos | Lista las 9 consolas |
| GET | http://localhost:8080/api/productos/consolax | Una consola (404 si no existe) |
| POST | /api/productos | Crea una (envía el JSON del producto) |
| PUT | /api/productos/{id} | Modifica una |
| DELETE | /api/productos/{id} | Borra una |

Para probarlas sin código: http://localhost:8080/swagger-ui/index.html
Para ver la base de datos: en la consola de Oracle Cloud, abre BDConfun → Database Actions → SQL y ejecuta `SELECT * FROM producto;`.

## Qué cambió en React

- `src/api/productosApi.js` (nuevo): las funciones `getProductos()` y `getProducto(id)` que usan `fetch`.
- `src/pages/Productos.jsx` y `src/pages/DetalleProducto.jsx`: piden los datos al backend con `useEffect` y muestran "Cargando..." o un mensaje si el backend está apagado.
- `src/data/productos.js`: ya no tiene la lista, solo `formatoPrecio`.
- El campo `desc` ahora se llama `descripcion` (igual que en Java).
