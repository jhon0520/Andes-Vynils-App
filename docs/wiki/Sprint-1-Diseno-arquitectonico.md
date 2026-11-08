# Sprint 1 · Diseño arquitectónico

**Wine Music (Vinilos)** · Equipo 10 · MISW4104 Ingeniería de Software para Aplicaciones Móviles

Esta página describe la arquitectura de la app Android tal como está en el código de la rama `develop` al cierre del sprint 1. Incluye los diagramas UML de **componentes**, **paquetes** y **clases** que pide la rúbrica (en inglés, como el código), más diagramas de secuencia y de navegación que muestran cómo se recorre la arquitectura en las HU implementadas.

> Documento de trabajo del equipo con el detalle de cada HU: [`docs/App-technical-documentation.md`](../App-technical-documentation.md) en el repositorio.

## Contenido

1. [Visión general](#1-visión-general)
2. [Decisiones de arquitectura](#2-decisiones-de-arquitectura)
3. [Diagrama de componentes (UML)](#3-diagrama-de-componentes-uml)
4. [Diagrama de paquetes (UML)](#4-diagrama-de-paquetes-uml)
5. [Diagramas de clases](#5-diagramas-de-clases)
6. [Diagramas de secuencia](#6-diagramas-de-secuencia)
7. [Navegación](#7-navegación)
8. [Patrones MVVM, Repository y Service Adapter](#8-patrones-mvvm-repository-y-service-adapter)
9. [Trazabilidad HU → código → API](#9-trazabilidad-hu--código--api)
10. [Estado, errores y concurrencia](#10-estado-errores-y-concurrencia)
11. [Tecnologías](#11-tecnologías)
12. [Riesgos y evolución](#12-riesgos-y-evolución)

---

## 1. Visión general

Vinilos es una app nativa Android escrita en **Kotlin** con **Jetpack Compose**. Consume la API REST **BackVynils** (NestJS + PostgreSQL) para mostrar álbumes, artistas y coleccionistas.

La arquitectura sigue **MVVM** sobre **Clean Architecture** en tres capas, más un módulo de inyección de dependencias:

| Capa | Paquete | Qué contiene | De qué depende |
|---|---|---|---|
| Presentación | `ui` | Pantallas Compose (`Screen` + `Content`), ViewModels, navegación, tema y componentes reutilizables | `domain` |
| Dominio | `domain` | Modelos, interfaces de repositorio y casos de uso. Kotlin puro: sin Android, Retrofit ni Compose | Nada |
| Datos | `data` | Interfaces Retrofit (Service Adapter), DTO, mappers e implementaciones de repositorio | `domain` |
| Inyección | `di` | Módulos de Hilt que crean Retrofit y conectan cada interfaz con su implementación | `data`, `domain` |

**Regla de dependencias:** las flechas siempre apuntan hacia `domain`. Ninguna pantalla importa `data` y ningún archivo de `domain` importa librerías de Android o de red. Así se puede cambiar la fuente de datos (por ejemplo, agregar caché en el sprint 2) sin tocar las pantallas, y probar ViewModels y casos de uso con repositorios falsos.

## 2. Decisiones de arquitectura

| # | Decisión | Alternativa descartada | Motivo |
|---|---|---|---|
| D1 | Jetpack Compose + Material 3 | Vistas XML + Fragments | Interfaz declarativa, menos código y pruebas de UI sin emulador de vistas (`ComposeTestRule`). |
| D2 | MVVM con `StateFlow<UiState<T>>` | LiveData | `StateFlow` es parte de corrutinas, funciona en pruebas unitarias sin Android y se observa con `collectAsStateWithLifecycle()`, que respeta el ciclo de vida. |
| D3 | Capa de casos de uso en `domain/usecase` | ViewModel llamando directo al repositorio | Las reglas (ordenar por nombre, combinar fuentes) quedan en un solo lugar y se prueban aisladas. |
| D4 | Repository con interfaz en `domain` e implementación en `data` | Repositorio concreto | Permite inyectar repositorios falsos en las pruebas y agregar caché local sin cambiar a quien lo usa. |
| D5 | Retrofit + kotlinx.serialization como Service Adapter | Volley | Interfaces declarativas con funciones `suspend`, sin callbacks; un único cliente HTTP para toda la app. |
| D6 | Hilt para inyección de dependencias | Singletons manuales / Service Locator | Un único `Retrofit` y una única instancia de cada repositorio (`@Singleton`), y ViewModels creados con sus dependencias (`@HiltViewModel`). |
| D7 | Navigation Compose con rutas tipadas (`@Serializable`) | Rutas como texto | El compilador valida los argumentos (`AlbumDetailRoute(albumId)`). |
| D8 | Búsqueda y filtros en memoria, en el ViewModel | Llamar a la API por cada búsqueda | El backend no ofrece búsqueda; filtrar la lista ya cargada evita llamadas de red mientras se escribe. |
| D9 | Coil para imágenes | Glide / Picasso | Pensado para Compose y corrutinas, con caché de memoria y disco. |

## 3. Diagrama de componentes (UML)

![Vinilos – UML Component Diagram](images/component-diagram.png)

> Fuente editable: [`uml/component-diagram.puml`](uml/component-diagram.puml) (PlantUML). Versión vectorial: [`images/component-diagram.svg`](images/component-diagram.svg).

**Notación UML 2.5 usada**

| Elemento | Símbolo | Significado en el diagrama |
|---|---|---|
| Nodo `«device»` / `«executionEnvironment»` | Caja 3D | Dónde se despliega cada componente: el teléfono Android (API 21+) y el servidor de BackVynils. |
| Componente | Rectángulo con el icono de componente | Unidad reemplazable de la app que encapsula su implementación (p. ej. *Repositories*). |
| Interfaz provista | Círculo ("lollipop") unido al componente | Servicio que el componente ofrece a otros (p. ej. *Repositories* provee `AlbumRepository`, `ArtistRepository`, `CollectorRepository`). |
| Interfaz requerida | Semicírculo ("socket") | Servicio que el componente necesita. Cuando socket y lollipop se encuentran, es un **conector de ensamble**. |
| Dependencia `«inject»` | Flecha punteada | Hilt crea la instancia y la entrega al componente que la usa. |
| Dependencia `«use»` | Flecha continua | Un componente invoca a otro sin una interfaz explícita (arranque y navegación). |

**Cómo se lee**

1. `MainActivity` arranca la app y usa *Navigation* (`MainScreen` + `WineMusicNavHost`), que decide qué *Compose Screen* mostrar.
2. Cada pantalla **requiere** la interfaz `StateFlow<UiState<T>>` + funciones de evento, que **proveen** los *ViewModels*. La pantalla solo observa estado y envía eventos; no conoce la red.
3. Los *ViewModels* requieren `invoke()` de los *Use Cases*, y los casos de uso requieren las interfaces de repositorio del dominio. Esa interfaz es el punto de cambio: en el sprint 2 se puede poner una caché detrás sin tocar pantallas ni ViewModels.
4. *Repositories* requiere las llamadas `suspend` del **Service Adapter** (interfaces Retrofit `AlbumApi`, `ArtistApi`, `CollectorApi`). Además envuelve cada llamada con `safeApiCall`, que traduce errores HTTP/red a `AppError`.
5. El *Service Adapter* usa el *HTTP Client* (OkHttp + kotlinx.serialization), que es el único componente que sale del dispositivo: consume la interfaz REST/JSON de **BackVynils** (NestJS + PostgreSQL).
6. Las pantallas requieren `AsyncImage` del *Image Loader* (Coil), que descarga portadas y fotos desde los servidores de imágenes con caché en memoria y disco.
7. El *DI Container* (Hilt) construye la cadena: `ApiConfigModule` entrega la URL base (`BuildConfig.API_BASE_URL`), `NetworkModule` crea OkHttp, Retrofit y las APIs, y `RepositoryModule` enlaza cada interfaz de repositorio con su implementación. Las pruebas E2E de errores reemplazan solo `ApiConfigModule` para apuntar a un backend falso.

| Componente | Interfaz que provee | Interfaz que requiere |
|---|---|---|
| Compose Screens | Lo que ve el usuario | `StateFlow<UiState<T>>`, `AsyncImage` |
| ViewModels | `StateFlow<UiState<T>>` + `loadAlbums()`, `onQueryChange()`, `onGenreSelect()`… | `invoke()` de los casos de uso |
| Use Cases | `operator fun invoke()` | `AlbumRepository`, `ArtistRepository`, `CollectorRepository` |
| Repositories | `getAlbums()`, `getAlbum(id)`, `getArtists()`, `getCollectors()` | Llamadas `suspend` del Service Adapter |
| Service Adapter | Una función `suspend` por endpoint (`GET /albums`, `/albums/{id}`, `/musicians`, `/bands`, `/collectors`) | `Call.Factory` (OkHttp) |
| HTTP Client | `Call.Factory` | REST/JSON de BackVynils |
| Image Loader | `AsyncImage` | HTTPS de los servidores de imágenes |

## 4. Diagrama de paquetes (UML)

![Vinilos – UML Package Diagram](images/package-diagram.png)

> Fuente editable: [`uml/package-diagram.puml`](uml/package-diagram.puml) (PlantUML). Versión vectorial: [`images/package-diagram.svg`](images/package-diagram.svg).

**Notación UML 2.5 usada**

| Elemento | Símbolo | Significado |
|---|---|---|
| Paquete | Carpeta con pestaña | Paquete Kotlin real del código (`com.dev.wine.music.app.<nombre>`). Los paquetes anidados se dibujan dentro de su paquete padre. |
| Dependencia `«import»` | Flecha punteada con estereotipo | Las clases del paquete origen importan elementos públicos del paquete destino. |
| Dependencia `«access»` | Flecha punteada con estereotipo | Uso de una librería externa sin reexportarla. |

**Cómo se lee**

- Paquete raíz `com.dev.wine.music.app`: `MainActivity` y `WineMusicApp` (`@HiltAndroidApp`) importan `ui.navigation` y `ui.theme`.
- **`ui`** (presentación): un subpaquete por HU (`ui.album.list` → HU01, `ui.album.detail` → HU02, `ui.artist` → HU03, `ui.collector` → HU05), más `ui.navigation` (rutas tipadas y barra inferior), `ui.common` (`UiState`, `ErrorType`), `ui.components` (tarjetas y vistas de carga/error/vacío) y `ui.theme`. `ui` importa solo `domain.usecase` y `domain.model`; **nunca importa `data`**.
- **`domain`**: Kotlin puro. `usecase` importa `repository` (interfaces) y `model`. No depende de Android, Retrofit ni Compose.
- **`data`**: `data.repository` implementa las interfaces de `domain.repository`, usa `data.remote.api` (Service Adapter Retrofit) y `data.mapper` para convertir `data.remote.dto` en `domain.model`.
- **`di`**: único paquete que conoce a la vez `data` y `domain`; con Hilt conecta cada interfaz con su implementación.
- **Regla de dependencias**: todas las flechas internas apuntan hacia `domain`, por lo que no hay ciclos y cada capa se puede probar con dobles (fakes) de la capa inferior.

| Paquete | Clases | Responsabilidad |
|---|---|---|
| *(raíz)* | `WineMusicApp`, `MainActivity` | Arranque de Hilt, splash y contenido Compose |
| `ui.album.list` | `AlbumListScreen`, `AlbumListContent`, `AlbumListViewModel`, `AlbumCatalog` | HU01: catálogo con búsqueda y filtro por género |
| `ui.album.detail` | `AlbumDetailScreen`, `AlbumDetailContent`, `AlbumDetailViewModel` | HU02: detalle de álbum |
| `ui.artist` | `ArtistListScreen`, `ArtistListContent`, `ArtistListViewModel` | HU03: listado de artistas |
| `ui.collector` | `CollectorListScreen`, `CollectorListContent`, `CollectorListViewModel` | HU05: listado de coleccionistas |
| `ui.navigation` | `MainScreen`, `WineMusicNavHost`, `Routes`, `TopLevelDestination` | Barra inferior, grafo y rutas tipadas |
| `ui.common` | `UiState`, `ErrorType`, `toUiError()` | Estados Loading / Success / Error y tipo de error |
| `ui.components` | `AlbumCard`, `LoadingView`, `ErrorView`, `EmptyView`, `NoResultsView`, `AlbumGenre.labelRes()` | Componentes reutilizables |
| `ui.theme` | `WineMusicAppTheme`, colores, tipografía | Sistema de diseño |
| `domain.model` | `Album`, `Track`, `NumberedTrack`, `Comment`, `AlbumGenre`, `Artist`, `ArtistType`, `Collector`, `AppError` | Entidades y errores del negocio |
| `domain.repository` | `AlbumRepository`, `ArtistRepository`, `CollectorRepository` | Contratos de acceso a datos |
| `domain.usecase` | `GetAlbumsUseCase`, `GetAlbumDetailUseCase`, `GetArtistsUseCase`, `GetCollectorsUseCase` | Acciones de negocio |
| `data.remote` | `safeApiCall` | Traduce excepciones HTTP/red a `AppError` |
| `data.remote.api` | `AlbumApi`, `ArtistApi`, `CollectorApi` | Service Adapter (Retrofit) |
| `data.remote.dto` | `AlbumDto`, `TrackDto`, `PerformerDto`, `CommentDto`, `CollectorDto` | Forma del JSON del backend |
| `data.mapper` | `AlbumDto.toDomain()`, `PerformerDto.toDomain()`, `CollectorDto.toDomain()` | DTO → modelo de dominio |
| `data.repository` | `AlbumRepositoryImpl`, `ArtistRepositoryImpl`, `CollectorRepositoryImpl` | Implementación de los contratos |
| `di` | `ApiConfigModule`, `NetworkModule`, `RepositoryModule` | Grafo de dependencias de Hilt |

## 5. Diagramas de clases

### 5.1 Modelo de dominio

```mermaid
classDiagram
    direction LR
    class Album {
        +id: Int
        +name: String
        +cover: String
        +releaseDate: String
        +description: String
        +genre: String
        +recordLabel: String
        +tracks: List~Track~
        +performers: List~Artist~
        +releaseYear: String
        +genreType: AlbumGenre?
        +performerNames: String
    }
    class Track {
        +id: Int
        +name: String
        +duration: String
    }
    class AlbumGenre {
        <<enumeration>>
        SALSA
        ROCK
        CLASSICAL
        FOLK
        +apiValue: String
        +fromApiValue(value) AlbumGenre?
    }
    class Artist {
        +id: Int
        +name: String
        +image: String
        +description: String
        +type: ArtistType
    }
    class ArtistType {
        <<enumeration>>
        MUSICIAN
        BAND
    }
    class Collector {
        +id: Int
        +name: String
        +telephone: String
        +email: String
    }

    Album "1" *-- "0..*" Track : tracks
    Album "0..*" --> "0..*" Artist : performers
    Album ..> AlbumGenre : genreType
    Artist --> ArtistType : type
```

### 5.2 Capa de datos (Service Adapter, DTO y repositorios)

```mermaid
classDiagram
    direction TB
    class AlbumRepository {
        <<interface>>
        +getAlbums() List~Album~
        +getAlbum(albumId: Int) Album
    }
    class ArtistRepository {
        <<interface>>
        +getArtists() List~Artist~
    }
    class CollectorRepository {
        <<interface>>
        +getCollectors() List~Collector~
    }
    class AlbumRepositoryImpl {
        -api: AlbumApi
    }
    class ArtistRepositoryImpl {
        -api: ArtistApi
        +getArtists() musicians and bands in parallel
    }
    class CollectorRepositoryImpl {
        -api: CollectorApi
    }
    class AlbumApi {
        <<Retrofit>>
        +getAlbums() List~AlbumDto~
        +getAlbum(id: Int) AlbumDto
    }
    class ArtistApi {
        <<Retrofit>>
        +getMusicians() List~PerformerDto~
        +getBands() List~PerformerDto~
    }
    class CollectorApi {
        <<Retrofit>>
        +getCollectors() List~CollectorDto~
    }
    class AlbumDto {
        <<Serializable>>
        +tracks: List~TrackDto~
        +performers: List~PerformerDto~
    }
    class TrackDto {
        <<Serializable>>
    }
    class PerformerDto {
        <<Serializable>>
        +birthDate: String?
        +creationDate: String?
    }
    class CollectorDto {
        <<Serializable>>
    }

    AlbumRepositoryImpl ..|> AlbumRepository
    ArtistRepositoryImpl ..|> ArtistRepository
    CollectorRepositoryImpl ..|> CollectorRepository
    AlbumRepositoryImpl --> AlbumApi
    ArtistRepositoryImpl --> ArtistApi
    CollectorRepositoryImpl --> CollectorApi
    AlbumApi ..> AlbumDto
    ArtistApi ..> PerformerDto
    CollectorApi ..> CollectorDto
    AlbumDto *-- TrackDto
    AlbumDto *-- PerformerDto
```

Los mappers (`data/mapper`) son funciones de extensión `toDomain()`. `PerformerDto.toDomain(type)` recibe el tipo cuando el endpoint lo indica (`/musicians` o `/bands`); dentro de un álbum lo deduce: si trae `creationDate` es banda, si no, músico.

### 5.3 Capa de presentación y dominio por HU

Las cuatro HU implementadas repiten la misma cadena **Screen → ViewModel → UseCase → Repository**.

```mermaid
classDiagram
    direction LR
    class UiState~T~ {
        <<sealed interface>>
    }
    class Loading
    class Success~T~ {
        +data: T
    }
    class Error {
        +message: String?
        +type: ErrorType
    }
    class ErrorType {
        <<enumeration>>
        NETWORK
        NOT_FOUND
        VALIDATION
        SERVER
        UNKNOWN
    }
    Error --> ErrorType
    UiState <|-- Loading
    UiState <|-- Success
    UiState <|-- Error

    class AlbumListViewModel {
        <<HiltViewModel>>
        +uiState: StateFlow~UiState~
        +query: StateFlow~String~
        +selectedGenre: StateFlow~AlbumGenre?~
        +loadAlbums()
        +onQueryChange(query)
        +onGenreSelect(genre)
        +clearFilters()
    }
    class AlbumCatalog {
        +albums: List~Album~
        +totalCount: Int
    }
    class AlbumDetailViewModel {
        <<HiltViewModel>>
        -albumId: Int
        +uiState: StateFlow~UiState~
        +loadAlbum()
    }
    class ArtistListViewModel {
        <<HiltViewModel>>
        +uiState: StateFlow~UiState~
        +loadArtists()
    }
    class CollectorListViewModel {
        <<HiltViewModel>>
        +uiState: StateFlow~UiState~
        +loadCollectors()
    }
    class GetAlbumsUseCase {
        +invoke() List~Album~
    }
    class GetAlbumDetailUseCase {
        +invoke(albumId) Album
    }
    class GetArtistsUseCase {
        +invoke() List~Artist~
    }
    class GetCollectorsUseCase {
        +invoke() List~Collector~
    }
    class AlbumRepository {
        <<interface>>
    }
    class ArtistRepository {
        <<interface>>
    }
    class CollectorRepository {
        <<interface>>
    }

    AlbumListViewModel --> GetAlbumsUseCase
    AlbumListViewModel ..> AlbumCatalog : emits
    AlbumDetailViewModel --> GetAlbumDetailUseCase
    ArtistListViewModel --> GetArtistsUseCase
    CollectorListViewModel --> GetCollectorsUseCase
    GetAlbumsUseCase --> AlbumRepository
    GetAlbumDetailUseCase --> AlbumRepository
    GetArtistsUseCase --> ArtistRepository
    GetCollectorsUseCase --> CollectorRepository
    AlbumListViewModel ..> UiState
    AlbumDetailViewModel ..> UiState
    ArtistListViewModel ..> UiState
    CollectorListViewModel ..> UiState
```

Cada pantalla tiene dos funciones Compose en el mismo archivo:

- **`XxxScreen`**: obtiene el ViewModel con `hiltViewModel()` y observa `uiState` con `collectAsStateWithLifecycle()`.
- **`XxxContent`**: no tiene estado. Recibe el `UiState` y lambdas de eventos y pinta carga, error, vacío o datos. Se prueba sin red ni ViewModel (por ejemplo, `AlbumListContentTest`).

### 5.4 Inyección de dependencias

```mermaid
classDiagram
    direction LR
    class NetworkModule {
        <<Hilt Module · SingletonComponent>>
        +provideJson() Json
        +provideOkHttpClient() OkHttpClient
        +provideRetrofit(okHttp, json) Retrofit
        +provideAlbumApi(retrofit) AlbumApi
        +provideArtistApi(retrofit) ArtistApi
        +provideCollectorApi(retrofit) CollectorApi
    }
    class ApiConfigModule {
        <<Hilt Module · SingletonComponent>>
        +provideApiBaseUrl() String
    }
    class RepositoryModule {
        <<Hilt Module · SingletonComponent>>
        +bindAlbumRepository(impl) AlbumRepository
        +bindArtistRepository(impl) ArtistRepository
        +bindCollectorRepository(impl) CollectorRepository
    }
    class WineMusicApp {
        <<HiltAndroidApp>>
    }
    class MainActivity {
        <<AndroidEntryPoint>>
    }
    WineMusicApp ..> NetworkModule : builds the container
    WineMusicApp ..> RepositoryModule
    WineMusicApp ..> ApiConfigModule
    NetworkModule ..> ApiConfigModule : @ApiBaseUrl
    MainActivity ..> WineMusicApp
```

- `Json` usa `ignoreUnknownKeys = true` y `coerceInputValues = true`: campos nuevos o nulos del backend no rompen la app.
- `OkHttpClient` registra el cuerpo de las peticiones solo en *debug*.
- La URL base viene de `BuildConfig.API_BASE_URL` que se toma de `api.base.url` en `gradle.properties` (por defecto `https://backvynils-q6yc.onrender.com/`; para un backend local se sobrescribe en `local.properties`, p. ej. `http://10.0.2.2:3000/` en emulador). `ApiConfigModule` la entrega con el calificador `@ApiBaseUrl`, y las pruebas E2E de errores reemplazan solo ese módulo.

## 6. Diagramas de secuencia

### 6.1 HU01 · Consultar catálogo de álbumes (carga y búsqueda)

```mermaid
sequenceDiagram
    actor U as User
    participant S as AlbumListScreen
    participant VM as AlbumListViewModel
    participant UC as GetAlbumsUseCase
    participant R as AlbumRepositoryImpl
    participant A as AlbumApi (Retrofit)
    participant B as BackVynils

    U->>S: Opens the Albums tab
    S->>VM: hiltViewModel() · init
    VM->>VM: loadedAlbums = Loading
    S-->>U: LoadingView
    VM->>UC: invoke()
    UC->>R: getAlbums()
    R->>A: getAlbums()
    A->>B: GET /albums
    B-->>A: 200 [AlbumDto]
    A-->>R: List<AlbumDto>
    R-->>UC: List<Album> (toDomain)
    UC-->>VM: List<Album> sorted by name
    VM->>VM: combine(albums, query, genre) → Success(AlbumCatalog)
    VM-->>S: uiState (StateFlow)
    S-->>U: Grid of AlbumCard

    U->>S: Types "ruben" / picks "Salsa"
    S->>VM: onQueryChange() / onGenreSelect()
    VM->>VM: Filters in memory (accent and case insensitive)
    VM-->>S: Success(filtered AlbumCatalog)
    S-->>U: Results or NoResultsView

    Note over VM,B: If the call fails, VM emits UiState.Error<br/>and the screen shows ErrorView with "Retry" → loadAlbums()
```

### 6.2 HU02 · Consultar detalle de álbum

```mermaid
sequenceDiagram
    actor U as User
    participant L as AlbumListScreen
    participant N as NavController
    participant S as AlbumDetailScreen
    participant VM as AlbumDetailViewModel
    participant UC as GetAlbumDetailUseCase
    participant R as AlbumRepositoryImpl
    participant B as BackVynils

    U->>L: Taps an AlbumCard
    L->>N: navigate(AlbumDetailRoute(albumId))
    N->>S: composable<AlbumDetailRoute>
    S->>VM: hiltViewModel() (albumId from SavedStateHandle)
    VM->>UC: invoke(albumId)
    UC->>R: getAlbum(albumId)
    R->>B: GET /albums/{id}
    B-->>R: AlbumDto with tracks and performers
    R-->>VM: Album
    VM-->>S: UiState.Success(album)
    S-->>U: Cover, data, tracks and performers
    U->>S: Back
    S->>N: popBackStack()
```

### 6.3 HU03 · Listado de artistas (llamadas en paralelo)

```mermaid
sequenceDiagram
    participant UC as GetArtistsUseCase
    participant R as ArtistRepositoryImpl
    participant A as ArtistApi
    participant B as BackVynils

    UC->>R: getArtists()
    par musicians
        R->>A: async getMusicians()
        A->>B: GET /musicians
    and bands
        R->>A: async getBands()
        A->>B: GET /bands
    end
    B-->>R: musicians + bands
    R-->>UC: List<Artist> (MUSICIAN + BAND)
    UC-->>UC: Sorts by name
```

## 7. Navegación

La app usa una sola `Activity` (`MainActivity`) y Navigation Compose. `MainScreen` dibuja la barra inferior solo en las pestañas principales.

```mermaid
stateDiagram-v2
    [*] --> Splash
    Splash --> AlbumsRoute : 2 s
    state "Bottom bar" as Tabs {
        AlbumsRoute --> ArtistsRoute
        ArtistsRoute --> CollectorsRoute
        CollectorsRoute --> AlbumsRoute
    }
    AlbumsRoute --> AlbumDetailRoute : tap album (albumId)
    AlbumDetailRoute --> AlbumsRoute : back
```

| Ruta (`Routes.kt`) | Pantalla | HU | Argumentos |
|---|---|---|---|
| `AlbumsRoute` (inicio) | `AlbumListScreen` | HU01 | — |
| `AlbumDetailRoute` | `AlbumDetailScreen` | HU02 | `albumId: Int` |
| `ArtistsRoute` | `ArtistListScreen` | HU03 | — |
| `CollectorsRoute` | `CollectorListScreen` | HU05 | — |

Al cambiar de pestaña se usa `popUpTo(start) { saveState = true }`, `launchSingleTop` y `restoreState`: cada pestaña existe una sola vez en la pila y conserva su scroll.

## 8. Patrones MVVM, Repository y Service Adapter

| Patrón | Dónde está | Cómo se aplica |
|---|---|---|
| **MVVM** | `ui/**/XxxViewModel` + `XxxScreen` | El ViewModel guarda el estado en `StateFlow<UiState<T>>` y sobrevive a la rotación. La vista solo observa y envía eventos; nunca llama a casos de uso ni a la red. |
| **Repository** | Interfaz en `domain/repository`, implementación en `data/repository` | Es la única puerta a los datos de cada entidad. Convierte DTO en modelos y oculta de dónde vienen (hoy la red; en el sprint 2, también caché). |
| **Service Adapter** | `data/remote/api` + `di/NetworkModule` | Interfaces Retrofit con una función `suspend` por endpoint, sobre un único `Retrofit` *singleton*. Solo los repositorios las usan. |
| **Use Case** (adicional) | `domain/usecase` | Una acción de negocio por clase (`operator fun invoke`), por ejemplo, ordenar alfabéticamente. |
| **Inyección de dependencias** | `di` + `@Inject` | Hilt entrega cada dependencia por constructor, lo que permite reemplazarlas en las pruebas. |

## 9. Trazabilidad HU → código → API

| HU | Pantalla | ViewModel | Caso de uso | Repositorio → Service Adapter | Endpoint | Estado en sprint 1 |
|---|---|---|---|---|---|---|
| HU01 Catálogo de álbumes | `AlbumListScreen` | `AlbumListViewModel` | `GetAlbumsUseCase` | `AlbumRepository.getAlbums()` → `AlbumApi.getAlbums()` | `GET /albums` | Implementada, con búsqueda, filtro por género y pruebas unitarias, de UI y E2E |
| HU02 Detalle de álbum | `AlbumDetailScreen` | `AlbumDetailViewModel` | `GetAlbumDetailUseCase` | `AlbumRepository.getAlbum(id)` → `AlbumApi.getAlbum(id)` | `GET /albums/{id}` | Cadena completa; faltan reseñas |
| HU03 Listado de artistas | `ArtistListScreen` | `ArtistListViewModel` | `GetArtistsUseCase` | `ArtistRepository.getArtists()` → `ArtistApi.getMusicians()`, `getBands()` | `GET /musicians`, `GET /bands` | Cadena completa (listado básico) |
| HU05 Listado de coleccionistas | `CollectorListScreen` | `CollectorListViewModel` | `GetCollectorsUseCase` | `CollectorRepository.getCollectors()` → `CollectorApi.getCollectors()` | `GET /collectors` | Cadena completa (listado básico) |
| HU04, HU06, HU07, HU08 | — | — | — | — | Ver [documento técnico](../App-technical-documentation.md#mapa-de-trazabilidad) | Planeadas para los sprints 2 y 3 |

## 10. Estado, errores y concurrencia

**Estado de pantalla.** Toda pantalla que carga datos usa `UiState<T>`:

| Estado | Qué se muestra |
|---|---|
| `Loading` | `LoadingView` (indicador de progreso) |
| `Success(data)` con datos | El contenido |
| `Success(data)` vacío | `EmptyView`, o `NoResultsView` si hay filtros activos |
| `Error(message)` | `ErrorView` con botón «Reintentar» |

**Errores.** El ViewModel captura cualquier excepción de la cadena y emite `UiState.Error`. `CancellationException` se vuelve a lanzar para no romper la cancelación estructurada de corrutinas.

**Concurrencia.**
- Las llamadas de red son funciones `suspend` que se lanzan en `viewModelScope`; Retrofit las ejecuta fuera del hilo principal, así que no bloquean la interfaz (prevención de ANR).
- `ArtistRepositoryImpl` pide músicos y bandas en paralelo con `async` dentro de `coroutineScope`.
- `AlbumListViewModel` combina la lista, la búsqueda y el género con `combine` + `stateIn(WhileSubscribed(5_000))`: el flujo se detiene 5 s después de que la pantalla deja de observarlo y sobrevive a la rotación.

## 11. Tecnologías

| Área | Librería | Versión |
|---|---|---|
| Lenguaje | Kotlin | 2.0.0 |
| Build | Android Gradle Plugin · KSP | 8.8.0 · 2.0.0-1.0.24 |
| SDK | `compileSdk` / `targetSdk` · `minSdk` | 35 · 24 |
| UI | Jetpack Compose (BOM) + Material 3 | 2024.09.03 |
| Navegación | Navigation Compose | 2.8.4 |
| Ciclo de vida | Lifecycle ViewModel / Runtime Compose | 2.8.7 |
| Inyección | Hilt · Hilt Navigation Compose | 2.52 · 1.2.0 |
| Red | Retrofit · OkHttp logging | 2.11.0 · 4.12.0 |
| JSON | kotlinx.serialization | 1.7.3 |
| Imágenes | Coil Compose | 2.7.0 |
| Pruebas | JUnit 4, kotlinx-coroutines-test, Compose UI Test, Espresso, Kraken | — |
| Backend | BackVynils (NestJS + TypeORM + PostgreSQL) | — |

## 12. Riesgos y evolución

**Riesgos abiertos**

| Riesgo | Impacto | Acción |
|---|---|---|
| `minSdk = 24`, pero el curso exige funcionar desde Android L (API 21) | La app no se instala en dispositivos con API 21–23 | Evaluar bajar `minSdk` a 21 y verificar que las librerías lo soporten |
| Sin caché local | Cada visita a una pantalla vuelve a llamar a la API | Agregar caché en los repositorios en el sprint 2 (el patrón Repository ya lo permite sin tocar la UI) |
| Los errores no se distinguen por tipo | Un 404 o un 400 se muestra igual que «sin conexión» | Agregar el tipo de error a `UiState.Error` (`IOException`, `HttpException` 404, 400/412) |
| Particularidades del backend (comentarios sin autor, banda del músico solo en `/bands`, POST que responden 200) | Datos incompletos en HU02, HU04 y HU06 | Cruzar datos en los repositorios, como se documenta en el documento técnico |

**Evolución prevista**

- **Sprint 2:** HU04 (detalle de artista) y HU06 (detalle de coleccionista) con nuevas rutas `ArtistDetailRoute` y `CollectorDetailRoute`; caché en repositorios; micro-optimizaciones y profiling.
- **Sprint 3:** HU07 (crear álbum, `POST /albums`) y HU08 (asociar tracks, `POST /albums/{id}/tracks`), con perfil visitante/coleccionista y validación de formularios.

Cada HU nueva sigue la misma cadena de clases y debe agregarse a los diagramas de esta página.
