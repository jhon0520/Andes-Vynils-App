# Documentación técnica — Vinyls Backend (BackVynils)

## 1. Resumen

**Vinyls Backend MISO** es una API REST para gestionar una colección de discos de vinilo: álbumes, canciones (tracks), artistas (músicos y bandas), coleccionistas, comentarios y premios.

| Aspecto | Detalle |
|---|---|
| Lenguaje | TypeScript 3.6 |
| Framework | NestJS 6 (`@nestjs/core`, `@nestjs/platform-express`) |
| ORM | TypeORM 0.2 (`@nestjs/typeorm`) |
| Base de datos | PostgreSQL |
| Validación | Joi 17 (validación manual dentro de los servicios) |
| Pruebas | Jest 24 + `@nestjs/testing` + colecciones Postman (`/collections`) |
| Puerto | `3000` |
| CORS | Habilitado para cualquier origen (`app.enableCors()`) |
| Autenticación | **Ninguna**: todos los endpoints son públicos |
| Prefijo global | Ninguno (las rutas cuelgan directamente de `/`) |

---

## 2. Estructura del proyecto

```
BackVynils/
├── src/
│   ├── main.ts                    # Arranque de la app (puerto 3000, CORS)
│   ├── app.module.ts              # Módulo raíz + configuración TypeORM/PostgreSQL
│   ├── interceptors/interceptor.ts  # BusinessErrorsInterceptor (errores de negocio → HTTP)
│   ├── shared/
│   │   ├── validation.ts          # Helper validate(schema, obj) sobre Joi
│   │   └── errors/business-errors.ts  # BusinessLogicException + enum BusinessError
│   ├── migration/1611260351811-DataSetup.ts  # Migración con datos semilla
│   │
│   ├── album/          # CRUD de álbumes
│   ├── track/          # Tracks de un álbum
│   ├── comment/        # Comentarios de un álbum
│   ├── musician/       # CRUD de músicos
│   ├── band/           # CRUD de bandas
│   ├── performer/      # Entidad base (herencia) de Musician y Band
│   ├── collector/      # CRUD de coleccionistas
│   ├── prize/          # CRUD de premios
│   │
│   ├── albumband/      # Asociación Álbum → Bandas
│   ├── albummusician/  # Asociación Álbum → Músicos
│   ├── bandalbum/      # Asociación Banda → Álbumes
│   ├── bandmusician/   # Asociación Banda → Músicos
│   ├── musicianalbum/  # Asociación Músico → Álbumes
│   ├── collectoralbum/ # Álbumes en la colección de un coleccionista (con precio/estado)
│   ├── collectorperformer/  # Artistas favoritos de un coleccionista
│   ├── performerprize/ # Premios otorgados a artistas
│   │
│   ├── genre/          # Enum GENRE
│   ├── recordlabel/    # Enum RECORD_LABEL
│   └── albumstatus/    # Enum ALBUM_STATUS
├── collections/        # Colecciones y entornos de Postman
├── sql/data.sql        # Script SQL de datos de prueba
├── Dockerfile          # Build multi-stage (node:12-alpine)
├── docker-compose.yml  # API + PostgreSQL
└── ormconfig.json      # Config para el CLI de TypeORM
```

Cada módulo de dominio sigue el patrón NestJS: `*.controller.ts` (rutas HTTP), `*.service.ts` (lógica de negocio y validación), `*.entity.ts` (modelo TypeORM), `*.dto.ts` (forma de entrada) y `*.spec.ts` (pruebas).

---

## 3. Configuración y ejecución

### 3.1 Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_USER` | `postgres` | Usuario |
| `DB_PASS` | `postgres` | Contraseña |
| `DB_NAME` | `vinyls` | Nombre de la base de datos |
| `USE_SSL` | *(vacío)* | Si es `"true"`, conecta con SSL (`rejectUnauthorized: false`) |

El puerto de PostgreSQL está fijo en `5432` y el de la API en `3000` (no son configurables por entorno).

### 3.2 Comportamiento de la base de datos

- `synchronize: true`: TypeORM crea y altera las tablas automáticamente a partir de las entidades al iniciar.
- `migrationsRun: true`: al iniciar se ejecuta la migración `DataSetup1611260351811`, que inserta los datos semilla (4 álbumes, 2 tracks, 2 artistas, 2 coleccionistas, 2 comentarios, 2 premios y sus asociaciones, todos con ids desde `100`).
- `dropSchema: false`: los datos se conservan entre reinicios.

### 3.3 Comandos

```bash
npm install
npm run start          # desarrollo
npm run start:dev      # modo watch
npm run build && npm run start:prod   # producción (dist/main)
npm run test           # pruebas unitarias (rootDir: src/album)
npm run test:e2e
npm run lint
```

Con Docker:

```bash
docker-compose up --build   # levanta "web" (puerto 3000) y "db" (postgres, puerto 5432)
```

---

## 4. Modelo de datos

### 4.1 Diagrama de entidades

```
                         ┌──────────────┐
                         │    Prize     │
                         └──────┬───────┘
                                │ 1..*
                         ┌──────▼───────┐
                         │PerformerPrize│ premiationDate
                         └──────┬───────┘
                                │ *..1
┌───────────┐  *..*   ┌─────────▼────────┐   *..*   ┌───────────┐
│ Collector │◄───────►│    Performer     │◄────────►│   Album   │
└─────┬─────┘favorite │ (herencia, col.  │ albums   └─┬───┬───┬─┘
      │      Perfor-  │  "type")         │            │   │   │
      │      mers     └──┬────────────┬──┘            │   │   │ 1..*
      │                  │            │               │   │ ┌─▼─────┐
      │            ┌─────▼──┐   ┌─────▼────┐  1..*    │   │ │ Track │
      │            │Musician│*─1│   Band   │          │   │ └───────┘
      │            └────────┘   └──────────┘          │   │ 1..*
      │ 1..*                                          │ ┌─▼───────┐
      ├──────────────────────────────────────────────────►Comment │
      │ 1..*                                          │ └─────────┘
      │              ┌────────────────┐   *..1        │
      └─────────────►│ CollectorAlbum │◄──────────────┘
                     │ price, status  │
                     └────────────────┘
```

### 4.2 Entidades

**Album** (`album`)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | number | PK autogenerada |
| `name` | string | |
| `cover` | string | URL de la portada |
| `releaseDate` | Date (timestamp) | |
| `description` | string | |
| `genre` | enum `GENRE` | |
| `recordLabel` | enum `RECORD_LABEL` | |
| `performers` | Performer[] | ManyToMany (tabla `performer_albums_album`) |
| `tracks` | Track[] | OneToMany |
| `comments` | Comment[] | OneToMany (cascade) |
| `collectorAlbums` | CollectorAlbum[] | OneToMany |

**Performer** (`performer`) — entidad base con herencia de tabla única (*single table inheritance*), columna discriminadora `type` (`Musician` / `Band`).

| Campo | Tipo | Notas |
|---|---|---|
| `id` | number | PK |
| `name` | string | |
| `image` | string | URL |
| `description` | string | |
| `collectors` | Collector[] | ManyToMany (tabla `performer_collectors_collector`) |
| `albums` | Album[] | ManyToMany |
| `performerPrizes` | PerformerPrize[] | OneToMany |

**Musician** (hereda de Performer): `birthDate: Date`, `band: Band` (ManyToOne).

**Band** (hereda de Performer): `creationDate: Date`, `musicians: Musician[]` (OneToMany).

**Track** (`track`): `id`, `name: string`, `duration: string` (p. ej. `"5:05"`), `album: Album` (ManyToOne).

**Comment** (`comment`): `id`, `description: string`, `rating: number`, `album: Album`, `collector: Collector`.

**Collector** (`collector`): `id`, `name`, `telephone`, `email`, `favoritePerformers: Performer[]`, `comments: Comment[]`, `collectorAlbums: CollectorAlbum[]`.

**CollectorAlbum** (`collector_album`): `id`, `price: number`, `status: ALBUM_STATUS`, `album: Album`, `collector: Collector`.

**Prize** (`prize`): `id`, `name`, `description`, `organization`, `performerPrizes: PerformerPrize[]`.

**PerformerPrize** (`performer_prize`): `id`, `premiationDate: Date`, `prize: Prize`, `performer: Performer`.

### 4.3 Enumeraciones

| Enum | Valores válidos (string exacto) |
|---|---|
| `GENRE` | `"Classical"`, `"Salsa"`, `"Rock"`, `"Folk"` |
| `RECORD_LABEL` | `"Sony Music"`, `"EMI"`, `"Discos Fuentes"`, `"Elektra"`, `"Fania Records"` |
| `ALBUM_STATUS` | `"Active"`, `"Inactive"` |

---

## 5. Convenciones generales de la API

### 5.1 Códigos de estado

| Situación | Código |
|---|---|
| GET / PUT exitoso | `200 OK` |
| POST exitoso | `200 OK` (se fuerza con `@HttpCode(200)`, **no** 201) |
| DELETE exitoso | `204 No Content` (sin cuerpo) |
| Recurso no encontrado | `404 Not Found` |
| Asociación inexistente entre recursos | `412 Precondition Failed` |
| Error de validación del cuerpo | `400 Bad Request` |
| Error no controlado (BD, referencias nulas, etc.) | `500 Internal Server Error` |

### 5.2 Formato de error

Los servicios lanzan `BusinessLogicException(message, type)` y el `BusinessErrorsInterceptor` lo traduce a `HttpException`. El cuerpo de la respuesta queda así:

```json
{
  "statusCode": 404,
  "message": "The album with the given id was not found"
}
```

En errores de validación, `message` contiene el texto de Joi, por ejemplo:

```json
{
  "statusCode": 400,
  "message": "ValidationError: \"genre\" must be one of [Classical, Salsa, Rock, Folk]"
}
```

### 5.3 Reglas de validación del cuerpo

- La validación usa esquemas `Joi.object(...)` **sin** `.unknown(true)`. Por eso **cualquier campo no declarado en el esquema es rechazado** con 400. Por ejemplo, enviar `"id"` en el cuerpo de un POST/PUT produce `"\"id\" is not allowed"`.
- Las fechas aceptan cualquier formato que entienda `Joi.date()` (ISO 8601 recomendado: `"1984-08-01"` o `"1984-08-01T00:00:00.000Z"`).
- En las respuestas, las fechas se devuelven como cadenas ISO 8601 (`"1984-08-01T05:00:00.000Z"`).
- Los PUT reemplazan el recurso completo: requieren todos los campos obligatorios, igual que el POST.
- Los parámetros de ruta (`:albumId`, etc.) son ids numéricos.

---

## 6. Catálogo de servicios (endpoints)

Resumen: **75 endpoints** agrupados en 16 controladores.

| # | Método | Ruta | Descripción |
|---|---|---|---|
| | | **Álbumes** | |
| 1 | GET | `/albums` | Listar álbumes |
| 2 | GET | `/albums/{albumId}` | Obtener un álbum |
| 3 | POST | `/albums` | Crear álbum |
| 4 | PUT | `/albums/{albumId}` | Actualizar álbum |
| 5 | DELETE | `/albums/{albumId}` | Eliminar álbum |
| | | **Tracks** | |
| 6 | GET | `/albums/{albumId}/tracks` | Listar tracks del álbum |
| 7 | GET | `/albums/{albumId}/tracks/{trackId}` | Obtener un track |
| 8 | POST | `/albums/{albumId}/tracks` | Agregar track al álbum |
| 9 | PUT | `/albums/{albumId}/tracks/{trackId}` | Actualizar track |
| 10 | DELETE | `/albums/{albumId}/tracks/{trackId}` | Quitar track del álbum |
| | | **Comentarios** | |
| 11 | GET | `/albums/{albumId}/comments` | Listar comentarios del álbum |
| 12 | GET | `/albums/{albumId}/comments/{commentId}` | Obtener un comentario |
| 13 | POST | `/albums/{albumId}/comments` | Crear comentario |
| 14 | PUT | `/albums/{albumId}/comments/{commentId}` | Actualizar comentario |
| 15 | DELETE | `/albums/{albumId}/comments/{commentId}` | Eliminar comentario |
| | | **Álbum ↔ Bandas** | |
| 16 | GET | `/albums/{albumId}/bands` | Bandas del álbum |
| 17 | GET | `/albums/{albumId}/bands/{bandId}` | Banda asociada al álbum |
| 18 | POST | `/albums/{albumId}/bands/{bandId}` | Asociar banda al álbum |
| 19 | PUT | `/albums/{albumId}/bands` | Reemplazar intérpretes del álbum por bandas |
| 20 | DELETE | `/albums/{albumId}/bands/{bandId}` | Desasociar banda |
| | | **Álbum ↔ Músicos** | |
| 21 | GET | `/albums/{albumId}/musicians` | Intérpretes del álbum |
| 22 | GET | `/albums/{albumId}/musicians/{musicianId}` | Músico asociado al álbum |
| 23 | POST | `/albums/{albumId}/musicians/{musicianId}` | Asociar músico al álbum |
| 24 | PUT | `/albums/{albumId}/musicians` | Reemplazar intérpretes del álbum por músicos |
| 25 | DELETE | `/albums/{albumId}/musicians/{musicianId}` | Desasociar músico |
| | | **Músicos** | |
| 26 | GET | `/musicians` | Listar músicos |
| 27 | GET | `/musicians/{musicianId}` | Obtener músico |
| 28 | POST | `/musicians` | Crear músico |
| 29 | PUT | `/musicians/{musicianId}` | Actualizar músico |
| 30 | DELETE | `/musicians/{musicianId}` | Eliminar músico |
| | | **Músico ↔ Álbumes** | |
| 31 | GET | `/musicians/{musicianId}/albums` | Álbumes del músico |
| 32 | GET | `/musicians/{musicianId}/albums/{albumId}` | Álbum asociado al músico |
| 33 | POST | `/musicians/{musicianId}/albums/{albumId}` | Asociar álbum al músico |
| 34 | PUT | `/musicians/{musicianId}/albums` | Reemplazar álbumes del músico |
| 35 | DELETE | `/musicians/{musicianId}/albums/{albumId}` | Desasociar álbum |
| | | **Bandas** | |
| 36 | GET | `/bands` | Listar bandas |
| 37 | GET | `/bands/{bandId}` | Obtener banda |
| 38 | POST | `/bands` | Crear banda |
| 39 | PUT | `/bands/{bandId}` | Actualizar banda |
| 40 | DELETE | `/bands/{bandId}` | Eliminar banda |
| | | **Banda ↔ Álbumes** | |
| 41 | GET | `/bands/{bandId}/albums` | Álbumes de la banda |
| 42 | GET | `/bands/{bandId}/albums/{albumId}` | Álbum asociado a la banda |
| 43 | POST | `/bands/{bandId}/albums/{albumId}` | Asociar álbum a la banda |
| 44 | PUT | `/bands/{bandId}/albums` | Reemplazar álbumes de la banda |
| 45 | DELETE | `/bands/{bandId}/albums/{albumId}` | Desasociar álbum |
| | | **Banda ↔ Músicos** | |
| 46 | GET | `/bands/{bandId}/musicians` | Músicos de la banda |
| 47 | GET | `/bands/{bandId}/musicians/{musicianId}` | Músico de la banda |
| 48 | POST | `/bands/{bandId}/musicians/{musicianId}` | Agregar músico a la banda |
| 49 | DELETE | `/bands/{bandId}/musicians/{musicianId}` | Quitar músico de la banda |
| | | **Coleccionistas** | |
| 50 | GET | `/collectors` | Listar coleccionistas |
| 51 | GET | `/collectors/{collectorId}` | Obtener coleccionista |
| 52 | POST | `/collectors` | Crear coleccionista |
| 53 | PUT | `/collectors/{collectorId}` | Actualizar coleccionista |
| 54 | DELETE | `/collectors/{collectorId}` | Eliminar coleccionista |
| | | **Coleccionista ↔ Álbumes** | |
| 55 | GET | `/collectors/{collectorId}/albums` | Álbumes del coleccionista |
| 56 | GET | `/collectors/{collectorId}/albums/{albumId}` | Un álbum del coleccionista |
| 57 | POST | `/collectors/{collectorId}/albums/{albumId}` | Agregar álbum a la colección |
| 58 | PUT | `/collectors/{collectorId}/albums/{albumId}` | Actualizar precio/estado |
| 59 | DELETE | `/collectors/{collectorId}/albums/{albumId}` | Quitar álbum de la colección |
| | | **Coleccionista ↔ Artistas favoritos** | |
| 60 | GET | `/collectors/{collectorId}/performers` | Artistas favoritos |
| 61 | POST | `/collectors/{collectorId}/musicians/{musicianId}` | Agregar músico favorito |
| 62 | POST | `/collectors/{collectorId}/bands/{bandId}` | Agregar banda favorita |
| 63 | DELETE | `/collectors/{collectorId}/musicians/{musicianId}` | Quitar músico favorito |
| 64 | DELETE | `/collectors/{collectorId}/bands/{bandId}` | Quitar banda favorita |
| | | **Premios** | |
| 65 | GET | `/prizes` | Listar premios |
| 66 | GET | `/prizes/{prizeId}` | Obtener premio |
| 67 | POST | `/prizes` | Crear premio |
| 68 | PUT | `/prizes/{prizeId}` | Actualizar premio |
| 69 | DELETE | `/prizes/{prizeId}` | Eliminar premio |
| | | **Premios ↔ Artistas** | |
| 70 | GET | `/prizes/{prizeId}/performers` | Premiaciones de un premio |
| 71 | POST | `/prizes/{prizeId}/musicians/{musicianId}` | Premiar a un músico |
| 72 | POST | `/prizes/{prizeId}/bands/{bandId}` | Premiar a una banda |
| 73 | DELETE | `/prizes/{prizeId}/musicians/{musicianId}` | Quitar premiación a músico |
| 74 | DELETE | `/prizes/{prizeId}/bands/{bandId}` | Quitar premiación a banda |
| 75 | GET | `/performerprizes` | Listar todas las premiaciones |

---

## 7. Detalle de cada servicio

Convención en esta sección: **Path** = parámetros de ruta, **Body** = cuerpo JSON de entrada, **Respuesta** = cuerpo devuelto en caso de éxito, **Errores** = respuestas de error controladas.

### 7.1 Álbumes — `AlbumController` (`/albums`)

#### GET `/albums`
- **Entrada:** ninguna.
- **Respuesta 200:** arreglo de álbumes con las relaciones `tracks`, `performers` y `comments`.

```json
[
  {
    "id": 100,
    "name": "Buscando América",
    "cover": "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
    "releaseDate": "1984-08-01T05:00:00.000Z",
    "description": "Buscando América es el primer álbum de la banda de Rubén Blades...",
    "genre": "Salsa",
    "recordLabel": "Elektra",
    "tracks": [ { "id": 100, "name": "Decisiones", "duration": "5:05" } ],
    "performers": [
      { "id": 100, "name": "Rubén Blades Bellido de Luna", "image": "https://...", "description": "...", "birthDate": "1948-07-16T05:00:00.000Z" }
    ],
    "comments": [ { "id": 100, "description": "The most relevant album of Ruben Blades", "rating": 5 } ]
  }
]
```

#### GET `/albums/{albumId}`
- **Path:** `albumId` (number).
- **Respuesta 200:** un álbum con `tracks`, `performers` y `comments` (misma forma que arriba).
- **Errores:** `404` — `"The album with the given id was not found"`.

#### POST `/albums`
- **Body:**

| Campo | Tipo | Requerido | Regla |
|---|---|---|---|
| `name` | string | Sí | |
| `cover` | string | Sí | |
| `releaseDate` | date | Sí | |
| `description` | string | Sí | |
| `genre` | string | Sí | Uno de `GENRE` |
| `recordLabel` | string | Sí | Uno de `RECORD_LABEL` |

```json
{
  "name": "Buscando América",
  "cover": "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
  "releaseDate": "1984-08-01T00:00:00-05:00",
  "description": "Primer álbum de la banda de Rubén Blades y Seis del Solar.",
  "genre": "Salsa",
  "recordLabel": "Elektra"
}
```

- **Respuesta 200:** el álbum creado, con su `id` (sin relaciones).

```json
{
  "id": 104,
  "name": "Buscando América",
  "cover": "https://...",
  "releaseDate": "1984-08-01T05:00:00.000Z",
  "description": "Primer álbum de la banda de Rubén Blades y Seis del Solar.",
  "genre": "Salsa",
  "recordLabel": "Elektra"
}
```

- **Errores:** `400` — validación Joi.

#### PUT `/albums/{albumId}`
- **Path:** `albumId`.
- **Body:** igual que el POST (todos los campos son obligatorios).
- **Respuesta 200:** el álbum actualizado (sin relaciones).
- **Errores:** `404` álbum no existe; `400` validación.

#### DELETE `/albums/{albumId}`
- **Path:** `albumId`.
- **Respuesta 204:** sin cuerpo.
- **Errores:** `404` álbum no existe.

---

### 7.2 Tracks — `TrackController` (`/albums/{albumId}/tracks`)

#### GET `/albums/{albumId}/tracks`
- **Path:** `albumId`.
- **Respuesta 200:** `[{ "id": 100, "name": "Decisiones", "duration": "5:05" }, ...]`
- **Errores:** `404` álbum.

#### GET `/albums/{albumId}/tracks/{trackId}`
- **Path:** `albumId`, `trackId`.
- **Respuesta 200:** `{ "id": 100, "name": "Decisiones", "duration": "5:05" }`
- **Errores:** `404` álbum (`"The album with the given id was not found"`); `404` track (`"The track with the given id was not found"`).

#### POST `/albums/{albumId}/tracks`
- **Path:** `albumId`.
- **Body:**

| Campo | Tipo | Requerido |
|---|---|---|
| `name` | string | Sí |
| `duration` | string | Sí (formato libre, p. ej. `"5:05"`) |

```json
{ "name": "Decisiones", "duration": "5:05" }
```

- **Respuesta 200:** el track creado, con el álbum embebido:

```json
{
  "id": 102,
  "name": "Decisiones",
  "duration": "5:05",
  "album": { "id": 100, "name": "Buscando América", "cover": "...", "releaseDate": "...", "description": "...", "genre": "Salsa", "recordLabel": "Elektra" }
}
```

- **Errores:** `404` álbum; `400` validación.

#### PUT `/albums/{albumId}/tracks/{trackId}`
- **Path:** `albumId`, `trackId`.
- **Body:** igual que el POST.
- **Respuesta 200:** `{ "id", "name", "duration" }` actualizado.
- **Errores:** `404` álbum / track; `400` validación.

#### DELETE `/albums/{albumId}/tracks/{trackId}`
- **Path:** `albumId`, `trackId`.
- **Respuesta 204:** sin cuerpo.
- **Errores:** `404` álbum / track.

---

### 7.3 Comentarios — `CommentController` (`/albums/{albumId}/comments`)

#### GET `/albums/{albumId}/comments`
- **Path:** `albumId`.
- **Respuesta 200:** `[{ "id": 100, "description": "...", "rating": 5 }, ...]`
- **Errores:** `404` álbum.

#### GET `/albums/{albumId}/comments/{commentId}`
- **Path:** `albumId`, `commentId`.
- **Respuesta 200:** `{ "id": 100, "description": "...", "rating": 5 }`
- **Errores:** `404` álbum; `404` comentario (`"The comment with the given id was not found"`); `404` si el comentario no pertenece al álbum (`"The comment is not associated to the album"`).

#### POST `/albums/{albumId}/comments`
- **Path:** `albumId`.
- **Body:**

| Campo | Tipo | Requerido | Regla |
|---|---|---|---|
| `description` | string | Sí | |
| `rating` | number | Sí | Entre 0 y 5 (`> -1` y `< 6`) |
| `collector` | object | Sí* | `{ "id": number }` del coleccionista autor |

\* `collector` no es requerido para Joi, pero el servicio lee `collector.id` antes de validar; si falta, responde `500`.

```json
{
  "description": "The most relevant album of Ruben Blades",
  "rating": 5,
  "collector": { "id": 100 }
}
```

- **Respuesta 200:** el comentario creado con `collector` y `album` embebidos.

```json
{
  "id": 102,
  "description": "The most relevant album of Ruben Blades",
  "rating": 5,
  "collector": { "id": 100, "name": "Manolo Bellon", "telephone": "3502457896", "email": "manollo@caracol.com.co" },
  "album": { "id": 100, "name": "Buscando América", "...": "..." }
}
```

- **Errores:** `404` álbum; `404` coleccionista; `400` validación.

#### PUT `/albums/{albumId}/comments/{commentId}`
- **Path:** `albumId`, `commentId`.
- **Body:** igual que el POST (`description`, `rating`, `collector.id`).
- **Respuesta 200:** el comentario actualizado (con `collector`).
- **Errores:** `404` álbum / coleccionista / comentario / comentario no asociado; `400` validación.

#### DELETE `/albums/{albumId}/comments/{commentId}`
- **Path:** `albumId`, `commentId`.
- **Respuesta 204:** sin cuerpo.
- **Errores:** `404` álbum / comentario.

---

### 7.4 Álbum ↔ Bandas — `AlbumBandController` (`/albums/{albumId}/bands`)

#### GET `/albums/{albumId}/bands`
- **Path:** `albumId`.
- **Respuesta 200:** arreglo de bandas asociadas al álbum (solo intérpretes de tipo `Band`).

```json
[ { "id": 101, "name": "Queen", "image": "https://...", "description": "...", "creationDate": "1970-01-01T05:00:00.000Z" } ]
```

- **Errores:** `404` álbum.

#### GET `/albums/{albumId}/bands/{bandId}`
- **Path:** `albumId`, `bandId`.
- **Respuesta 200:** la banda (`{ id, name, image, description, creationDate }`).
- **Errores:** `404` banda; `404` álbum; `412` — `"The band with the given id is not associated to the album"`.

#### POST `/albums/{albumId}/bands/{bandId}`
- **Path:** `albumId`, `bandId`. **Body:** ninguno.
- **Comportamiento:** agrega la banda a los intérpretes existentes del álbum.
- **Respuesta 200:** el álbum con `performers` actualizados.

```json
{ "id": 100, "name": "Buscando América", "...": "...", "performers": [ { "id": 100, "...": "..." }, { "id": 101, "...": "..." } ] }
```

- **Errores:** `404` banda / álbum.

#### PUT `/albums/{albumId}/bands`
- **Path:** `albumId`.
- **Body:** arreglo de bandas; solo se usa el `id` de cada una.

```json
[ { "id": 101 }, { "id": 102 } ]
```

- **Comportamiento:** **reemplaza todos** los intérpretes del álbum (incluidos músicos) por la lista enviada.
- **Respuesta 200:** el álbum con `performers` actualizados.
- **Errores:** `404` álbum; `404` si alguna banda no existe.

#### DELETE `/albums/{albumId}/bands/{bandId}`
- **Path:** `albumId`, `bandId`.
- **Respuesta 204:** sin cuerpo.
- **Errores:** `404` banda / álbum.

---

### 7.5 Álbum ↔ Músicos — `AlbumMusicianController` (`/albums/{albumId}/musicians`)

Igual que Álbum ↔ Bandas, pero con músicos:

| Endpoint | Entrada | Respuesta 200 | Errores |
|---|---|---|---|
| GET `/albums/{albumId}/musicians` | `albumId` | Arreglo de `performers` del álbum (**incluye también bandas**, no filtra por tipo) | 404 álbum |
| GET `/albums/{albumId}/musicians/{musicianId}` | `albumId`, `musicianId` | Músico `{ id, name, image, description, birthDate }` | 404 músico / álbum; 412 `"The musician with the given id is not associated to the album"` |
| POST `/albums/{albumId}/musicians/{musicianId}` | `albumId`, `musicianId`; sin body | Álbum con `performers` (se agrega el músico) | 404 músico / álbum |
| PUT `/albums/{albumId}/musicians` | `albumId`; body `[{ "id": 100 }, ...]` | Álbum con `performers` reemplazados por los músicos enviados | 404 álbum / músico |
| DELETE `/albums/{albumId}/musicians/{musicianId}` | `albumId`, `musicianId` | 204 sin cuerpo | 404 músico / álbum |

---

### 7.6 Músicos — `MusicianController` (`/musicians`)

#### GET `/musicians`
- **Respuesta 200:** arreglo de músicos con `albums` y `performerPrizes`.

```json
[
  {
    "id": 100,
    "name": "Rubén Blades Bellido de Luna",
    "image": "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
    "description": "Es un cantante, compositor, músico, actor, abogado, político y activista panameño...",
    "birthDate": "1948-07-16T05:00:00.000Z",
    "albums": [ { "id": 100, "name": "Buscando América", "...": "..." } ],
    "performerPrizes": [ { "id": 100, "premiationDate": "1978-12-10T05:00:00.000Z" } ]
  }
]
```

#### GET `/musicians/{musicianId}`
- **Path:** `musicianId`.
- **Respuesta 200:** un músico (misma forma).
- **Errores:** `404` — `"The musician with the given id was not found"`.

#### POST `/musicians`
- **Body:**

| Campo | Tipo | Requerido | Regla |
|---|---|---|---|
| `name` | string | Sí | |
| `image` | string | No* | URI válida |
| `description` | string | Sí | |
| `birthDate` | date | No* | |

\* Joi los marca como opcionales, pero las columnas en BD no admiten nulos: si se omiten, la inserción falla con `500`. En la práctica son obligatorios.

```json
{
  "name": "Rubén Blades Bellido de Luna",
  "image": "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
  "description": "Cantante, compositor, músico, actor y político panameño.",
  "birthDate": "1948-07-16T00:00:00-05:00"
}
```

- **Respuesta 200:** `{ "id", "name", "image", "description", "birthDate" }`.
- **Errores:** `400` validación.

#### PUT `/musicians/{musicianId}`
- **Body:** igual que el POST. **Respuesta 200:** músico actualizado. **Errores:** `404`, `400`.

#### DELETE `/musicians/{musicianId}`
- **Respuesta 204.** **Errores:** `404`.

---

### 7.7 Músico ↔ Álbumes — `MusicianAlbumController` (`/musicians/{musicianId}/albums`)

| Endpoint | Entrada | Respuesta 200 | Errores |
|---|---|---|---|
| GET `/musicians/{musicianId}/albums` | `musicianId` | Arreglo de álbumes `{ id, name, cover, releaseDate, description, genre, recordLabel }` | 404 músico |
| GET `/musicians/{musicianId}/albums/{albumId}` | `musicianId`, `albumId` | Un álbum | 404 músico / álbum; 412 `"The album with the given id is not associated to the musician"` |
| POST `/musicians/{musicianId}/albums/{albumId}` | `musicianId`, `albumId`; sin body | Álbum con `performers` actualizados | 404 músico / álbum |
| PUT `/musicians/{musicianId}/albums` | `musicianId`; body `[{ "id": 100 }, { "id": 101 }]` | Músico con `albums` reemplazados | 404 músico / álbum |
| DELETE `/musicians/{musicianId}/albums/{albumId}` | `musicianId`, `albumId` | 204 sin cuerpo | 404 músico / álbum |

---

### 7.8 Bandas — `BandController` (`/bands`)

#### GET `/bands`
- **Respuesta 200:** arreglo de bandas con `albums`, `musicians` y `performerPrizes`.

```json
[
  {
    "id": 101,
    "name": "Queen",
    "image": "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
    "description": "Queen es una banda británica de rock formada en 1970 en Londres...",
    "creationDate": "1970-01-01T05:00:00.000Z",
    "albums": [ { "id": 102, "name": "A Night at the Opera", "...": "..." } ],
    "musicians": [],
    "performerPrizes": [ { "id": 101, "premiationDate": "1980-12-10T05:00:00.000Z" } ]
  }
]
```

#### GET `/bands/{bandId}`
- **Respuesta 200:** una banda (misma forma). **Errores:** `404` — `"The band with the given id was not found"`.

#### POST `/bands`
- **Body:**

| Campo | Tipo | Requerido | Regla |
|---|---|---|---|
| `name` | string | Sí | |
| `image` | string | No* | URI válida |
| `description` | string | Sí | |
| `creationDate` | date | Sí | |

\* Igual que en músicos: `image` es opcional para Joi pero obligatoria en BD.

```json
{
  "name": "Queen",
  "image": "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
  "description": "Banda británica de rock formada en 1970 en Londres.",
  "creationDate": "1970-01-01T00:00:00-05:00"
}
```

- **Respuesta 200:** `{ "id", "name", "image", "description", "creationDate" }`. **Errores:** `400`.

#### PUT `/bands/{bandId}`
- **Body:** igual que el POST. **Respuesta 200:** banda actualizada. **Errores:** `404`, `400`.

#### DELETE `/bands/{bandId}`
- **Respuesta 204.** **Errores:** `404`.

---

### 7.9 Banda ↔ Álbumes — `BandAlbumController` (`/bands/{bandId}/albums`)

| Endpoint | Entrada | Respuesta 200 | Errores |
|---|---|---|---|
| GET `/bands/{bandId}/albums` | `bandId` | Arreglo de álbumes de la banda | 404 banda |
| GET `/bands/{bandId}/albums/{albumId}` | `bandId`, `albumId` | Un álbum | 404 banda / álbum; 412 `"The album with the given id is not associated to the band"` |
| POST `/bands/{bandId}/albums/{albumId}` | `bandId`, `albumId`; sin body | Álbum con `performers: [banda]` (**reemplaza** los intérpretes del álbum por esta banda) | 404 banda / álbum |
| PUT `/bands/{bandId}/albums` | `bandId`; body `[{ "id": 100 }, ...]` | Banda con `albums` reemplazados | 404 banda / álbum |
| DELETE `/bands/{bandId}/albums/{albumId}` | `bandId`, `albumId` | 204 sin cuerpo | 404 banda / álbum |

---

### 7.10 Banda ↔ Músicos — `BandMusicianController` (`/bands/{bandId}/musicians`)

| Endpoint | Entrada | Respuesta 200 | Errores |
|---|---|---|---|
| GET `/bands/{bandId}/musicians` | `bandId` | Arreglo de músicos `{ id, name, image, description, birthDate }` | 404 banda |
| GET `/bands/{bandId}/musicians/{musicianId}` | `bandId`, `musicianId` | Músico con `band` embebida | 404 banda / músico; 412 `"The musician with the given id is not associated to the band"` |
| POST `/bands/{bandId}/musicians/{musicianId}` | `bandId`, `musicianId`; sin body | Músico con `band` asignada | 404 banda / músico |
| DELETE `/bands/{bandId}/musicians/{musicianId}` | `bandId`, `musicianId` | 204 sin cuerpo | 404 banda / músico |

Ejemplo de respuesta del POST:

```json
{
  "id": 100,
  "name": "Rubén Blades Bellido de Luna",
  "image": "https://...",
  "description": "...",
  "birthDate": "1948-07-16T05:00:00.000Z",
  "band": { "id": 101, "name": "Queen", "image": "https://...", "description": "...", "creationDate": "1970-01-01T05:00:00.000Z" }
}
```

---

### 7.11 Coleccionistas — `CollectorController` (`/collectors`)

#### GET `/collectors`
- **Respuesta 200:** arreglo de coleccionistas con `comments`, `favoritePerformers` y `collectorAlbums`.

```json
[
  {
    "id": 100,
    "name": "Manolo Bellon",
    "telephone": "3502457896",
    "email": "manollo@caracol.com.co",
    "comments": [ { "id": 100, "description": "The most relevant album of Ruben Blades", "rating": 5 } ],
    "favoritePerformers": [ { "id": 100, "name": "Rubén Blades Bellido de Luna", "...": "..." } ],
    "collectorAlbums": [ { "id": 100, "price": 35, "status": "Active" } ]
  }
]
```

#### GET `/collectors/{collectorId}`
- **Respuesta 200:** un coleccionista (misma forma). **Errores:** `404` — `"The collector with the given id was not found"`.

#### POST `/collectors`
- **Body:**

| Campo | Tipo | Requerido | Regla |
|---|---|---|---|
| `name` | string | Sí | |
| `telephone` | string | Sí | |
| `email` | string | Sí | Email válido |

```json
{ "name": "Manolo Bellon", "telephone": "3502457896", "email": "manollo@caracol.com.co" }
```

- **Respuesta 200:** `{ "id", "name", "telephone", "email" }`. **Errores:** `400`.

#### PUT `/collectors/{collectorId}`
- **Body:** igual que el POST. **Respuesta 200:** coleccionista actualizado. **Errores:** `404`, `400`.

#### DELETE `/collectors/{collectorId}`
- **Respuesta 204.** **Errores:** `404`.

---

### 7.12 Coleccionista ↔ Álbumes — `CollectorAlbumController` (`/collectors/{collectorId}/albums`)

#### GET `/collectors/{collectorId}/albums`
- **Path:** `collectorId`.
- **Respuesta 200:** arreglo de registros `CollectorAlbum` del coleccionista, con `album` y `collector` embebidos.

```json
[
  {
    "id": 100,
    "price": 35,
    "status": "Active",
    "album": { "id": 100, "name": "Buscando América", "...": "..." },
    "collector": { "id": 100, "name": "Manolo Bellon", "telephone": "3502457896", "email": "manollo@caracol.com.co" }
  }
]
```

- **Errores:** `404` coleccionista.

#### GET `/collectors/{collectorId}/albums/{albumId}`
- **Path:** `collectorId`, `albumId`.
- **Respuesta 200:** **arreglo** (puede estar vacío) de `CollectorAlbum` que coinciden con ese coleccionista y álbum, misma forma que arriba.
- **Errores:** `404` coleccionista / álbum.

#### POST `/collectors/{collectorId}/albums/{albumId}`
- **Path:** `collectorId`, `albumId`.
- **Body:**

| Campo | Tipo | Requerido | Regla |
|---|---|---|---|
| `price` | number | Sí | |
| `status` | string | Sí | `"Active"` o `"Inactive"` |

```json
{ "price": 35, "status": "Active" }
```

- **Respuesta 200:** el registro creado:

```json
{
  "id": 102,
  "price": 35,
  "status": "Active",
  "album": { "id": 100, "...": "..." },
  "collector": { "id": 100, "...": "..." }
}
```

- **Errores:** `404` coleccionista / álbum; `400` validación.

#### PUT `/collectors/{collectorId}/albums/{albumId}`
- **Body:** igual que el POST (`price`, `status`).
- **Respuesta 200:** el registro `CollectorAlbum` actualizado con `album`.
- **Errores:** `404` coleccionista / álbum; `400` validación.

#### DELETE `/collectors/{collectorId}/albums/{albumId}`
- **Respuesta 204.**
- **Errores:** `404` coleccionista / álbum; `404` — `"The album is not associated to the collector"`.

---

### 7.13 Coleccionista ↔ Artistas favoritos — `CollectorPerformerController`

| Endpoint | Entrada | Respuesta 200 | Errores |
|---|---|---|---|
| GET `/collectors/{collectorId}/performers` | `collectorId` | Arreglo de intérpretes favoritos (músicos con `birthDate` y bandas con `creationDate`) | 404 coleccionista |
| POST `/collectors/{collectorId}/musicians/{musicianId}` | `collectorId`, `musicianId`; sin body | El intérprete con su arreglo `collectors` actualizado | 404 coleccionista; 404 `"The performer with the given id was not found"` |
| POST `/collectors/{collectorId}/bands/{bandId}` | `collectorId`, `bandId`; sin body | Ídem | Ídem |
| DELETE `/collectors/{collectorId}/musicians/{musicianId}` | `collectorId`, `musicianId` | 204 sin cuerpo | 404 coleccionista / intérprete |
| DELETE `/collectors/{collectorId}/bands/{bandId}` | `collectorId`, `bandId` | 204 sin cuerpo | 404 coleccionista / intérprete |

Ejemplo de respuesta del POST:

```json
{
  "id": 100,
  "name": "Rubén Blades Bellido de Luna",
  "image": "https://...",
  "description": "...",
  "birthDate": "1948-07-16T05:00:00.000Z",
  "collectors": [ { "id": 100, "name": "Manolo Bellon", "telephone": "3502457896", "email": "manollo@caracol.com.co" } ]
}
```

> Las rutas `/musicians/` y `/bands/` usan el mismo servicio y no verifican el tipo del intérprete: se acepta cualquier id de `performer`.

---

### 7.14 Premios — `PrizeController` (`/prizes`)

#### GET `/prizes`
- **Respuesta 200:** arreglo de premios con `performerPrizes`.

```json
[
  {
    "id": 100,
    "organization": "National Academy of Recording Arts & Sciences",
    "name": "Grammy Award",
    "description": "Grammy Award, any of a series of awards presented annually in the United States by the National Academy of Recording Arts & Sciences",
    "performerPrizes": [ { "id": 100, "premiationDate": "1978-12-10T05:00:00.000Z" } ]
  }
]
```

#### GET `/prizes/{prizeId}`
- **Respuesta 200:** un premio. **Errores:** `404` — `"The prize with the given id was not found"`.

#### POST `/prizes`
- **Body:**

| Campo | Tipo | Requerido |
|---|---|---|
| `name` | string | Sí |
| `description` | string | Sí |
| `organization` | string | Sí |

```json
{ "name": "Premio Lo Nuestro", "description": "Premios a los mejores músicos latinos del año.", "organization": "Univisión" }
```

- **Respuesta 200:** `{ "id", "name", "description", "organization" }`. **Errores:** `400`.

#### PUT `/prizes/{prizeId}`
- **Body:** igual que el POST. **Respuesta 200:** premio actualizado. **Errores:** `404`, `400`.

#### DELETE `/prizes/{prizeId}`
- **Respuesta 204.** **Errores:** `404`.

---

### 7.15 Premios ↔ Artistas — `PerformerPrizeController` / `PerformerPrizesController`

#### GET `/prizes/{prizeId}/performers`
- **Path:** `prizeId`.
- **Respuesta 200:** arreglo de premiaciones de ese premio, con `performer` y `prize` embebidos.

```json
[
  {
    "id": 100,
    "premiationDate": "1978-12-10T05:00:00.000Z",
    "performer": { "id": 100, "name": "Rubén Blades Bellido de Luna", "...": "..." },
    "prize": { "id": 100, "organization": "National Academy of Recording Arts & Sciences", "name": "Grammy Award", "description": "..." }
  }
]
```

- **Errores:** `404` premio.

#### POST `/prizes/{prizeId}/musicians/{musicianId}` y POST `/prizes/{prizeId}/bands/{bandId}`
- **Path:** `prizeId` y el id del intérprete.
- **Body:**

| Campo | Tipo | Requerido |
|---|---|---|
| `premiationDate` | date | No* |

\* Opcional para Joi pero obligatorio en BD (si falta → `500`).

```json
{ "premiationDate": "1978-12-10T00:00:00-05:00" }
```

- **Respuesta 200:**

```json
{
  "id": 102,
  "premiationDate": "1978-12-10T05:00:00.000Z",
  "prize": { "id": 100, "...": "..." },
  "performer": { "id": 100, "...": "..." }
}
```

- **Errores:** `404` premio; `404` `"The performer with the given id was not found"`; `400` validación.

#### DELETE `/prizes/{prizeId}/musicians/{musicianId}` y DELETE `/prizes/{prizeId}/bands/{bandId}`
- **Respuesta 204.** **Errores:** `404` premio / intérprete.

#### GET `/performerprizes`
- **Respuesta 200:** todas las premiaciones con su `prize`:

```json
[ { "id": 100, "premiationDate": "1978-12-10T05:00:00.000Z", "prize": { "id": 100, "organization": "National Academy of Recording Arts & Sciences", "name": "Grammy Award", "description": "..." } } ]
```

---

## 8. Hallazgos del análisis (bugs y riesgos)

Durante la revisión del código se encontraron comportamientos que no coinciden con lo que el endpoint aparenta hacer. Conviene tenerlos en cuenta al consumir la API o al priorizar correcciones.

| # | Ubicación | Problema | Efecto |
|---|---|---|---|
| 1 | `bandalbum.service.ts` `deleteAlbumToBand`, `bandmusician.service.ts` `deleteMusicianToBand`, `musicianalbum.service.ts` `deleteAlbumToMusician` | El `filter(e => { e.id !== id })` usa llaves sin `return`, así que siempre devuelve `undefined` | Se eliminan **todas** las asociaciones, no solo la indicada |
| 2 | `collectorperformer.service.ts` `deleteCollectorPerformer` | El resultado de `filter` no se asigna | El DELETE responde 204 pero **no elimina** el favorito |
| 3 | `albumband.service.ts` `deleteBandToAlbum`, `albummusician.service.ts` `deleteMusicianToAlbum` | Compara `e.id !== bandId`, pero el parámetro de ruta llega como string (no hay `ParseIntPipe`) | La comparación estricta nunca coincide: **no elimina** la asociación |
| 4 | `bandalbum.service.ts` `addBandAlbum` | Asigna `album.performers = [band]` | Borra los demás intérpretes del álbum |
| 5 | `albummusician.service.ts` `findMusiciansByAlbumId` | No filtra por tipo | Devuelve también bandas |
| 6 | `albumband`/`albummusician` PUT | Reemplaza todos los `performers` | Al asociar bandas se pierden los músicos y viceversa |
| 7 | `collectoralbum.service.ts` `updateAlbumCollector`/`deleteAlbumCollector`, `performerprize.service.ts` `deletePrizePerformer` | Buscan con `where: { collectorId, albumId }` / `{ prizeId, performerId }`, propiedades que no existen en la entidad; además el update y el delete de premios no validan que se haya encontrado | Pueden operar sobre un registro que no corresponde o fallar con `500` |
| 8 | `track.service.ts` `findOneTrack` / `update` / `delete` | No verifica que el track pertenezca al álbum | Se puede leer/editar un track de otro álbum; el delete solo lo desvincula (`albumId = NULL`), no lo borra |
| 9 | `comment.service.ts` `addCommentAlbum` / `updateComment` | Lee `commentDTO.collector.id` antes de validar | Sin `collector` en el body → `500` en vez de `400` |
| 10 | Esquemas Joi de músico, banda y premiación | `image`, `birthDate`, `premiationDate` opcionales en Joi pero `NOT NULL` en BD | Omitirlos produce `500` en vez de `400` |
| 11 | Todos los POST | `@HttpCode(200)` | No sigue la convención REST de `201 Created` |
| 12 | `app.module.ts` | `synchronize: true` en todos los entornos | Riesgo de cambios de esquema no controlados en producción |
| 13 | General | Sin autenticación ni autorización | Cualquiera puede crear, modificar o borrar datos |
| 14 | `package.json` (`jest.rootDir: src/album`) | `npm test` solo ejecuta las pruebas de `album` | Las demás pruebas unitarias no corren por defecto |

---

## 9. Pruebas

- **Unitarias:** cada módulo tiene `*.controller.spec.ts` y `*.service.spec.ts`; por la configuración actual de Jest solo se ejecutan las de `src/album`.
- **E2E:** `test/app.e2e-spec.ts` con `supertest`.
- **Postman:** la carpeta `collections/` contiene una colección por recurso (Album, Band, Musician, Collector, Comment, Prize, Tracks y asociaciones) y dos entornos (`Equipo propio` y `Heroku`) con las variables `ip` y `protocol`. `reports/index.html` contiene un reporte de ejecución.
