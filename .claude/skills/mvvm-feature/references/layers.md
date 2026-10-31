# Plantillas por capa (lectura de datos)

Paquete raíz: `com.dev.wine.music.app` → `app/src/main/java/com/dev/wine/music/app/`.

El ejemplo es **HU06 Detalle de coleccionista** (`GET /collectors/{id}` + `GET /collectors/{id}/albums`), porque combina dos endpoints, recibe un id por navegación y aún no existe. Para otra HU cambia los nombres según su fila del mapa de trazabilidad. Los archivos de referencia en el código son `ui/album/detail/*`, `ui/album/list/*` y `data/repository/ArtistRepositoryImpl.kt`.

## Convenciones de nombres

| Pieza | Nombre | Archivo |
|---|---|---|
| Pantalla | `<Entidad><Accion>Screen` + `<Entidad><Accion>Content` | `ui/<entidad>/<accion>/<Entidad><Accion>Screen.kt` |
| ViewModel | `<Entidad><Accion>ViewModel` | `ui/<entidad>/<accion>/<Entidad><Accion>ViewModel.kt` |
| Caso de uso | `<Verbo><Entidad>UseCase` (`GetCollectorDetailUseCase`) | `domain/usecase/` |
| Repositorio | `<Entidad>Repository` / `<Entidad>RepositoryImpl` | `domain/repository/` / `data/repository/` |
| Api | `<Entidad>Api` | `data/remote/api/` |
| DTO | `<Entidad>Dto`, `<Entidad>RequestDto` para cuerpos | `data/remote/dto/` |
| Ruta | `<Entidad><Accion>Route(val <entidad>Id: Int)` | `ui/navigation/Routes.kt` |

> Hoy `artist/` y `collector/` no tienen subcarpeta `list/`. Al agregar el detalle de esas entidades, crea `ui/collector/detail/` y deja la lista donde está, salvo que el usuario pida mover la lista a `ui/collector/list/` (en ese caso, en un commit `refactor` aparte).

---

## 1. DTO

Solo los campos que la app usa, con valores por defecto para lo opcional. `NetworkModule` ya configura `ignoreUnknownKeys = true` y `coerceInputValues = true`.

```kotlin
// data/remote/dto/CollectorAlbumDto.kt
package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CollectorAlbumDto(
    val id: Int,
    val price: Int = 0,
    val status: String = "",
    val album: AlbumDto? = null,
)
```

## 2. Api (Service Adapter)

Una función `suspend` por endpoint. Parámetros de ruta con `@Path`, cuerpos con `@Body`.

```kotlin
// data/remote/api/CollectorApi.kt
interface CollectorApi {
    @GET("collectors")
    suspend fun getCollectors(): List<CollectorDto>

    @GET("collectors/{id}")
    suspend fun getCollector(@Path("id") collectorId: Int): CollectorDto

    @GET("collectors/{id}/albums")
    suspend fun getCollectorAlbums(@Path("id") collectorId: Int): List<CollectorAlbumDto>
}
```

Si la Api es **nueva**, regístrala en `di/NetworkModule.kt`:

```kotlin
@Provides
@Singleton
fun provideXxxApi(retrofit: Retrofit): XxxApi = retrofit.create(XxxApi::class.java)
```

## 3. Modelo de dominio

Kotlin puro. Los valores calculados para mostrar (como `Album.releaseYear`) pueden ir como propiedades. Los valores cerrados del backend van como `enum`.

```kotlin
// domain/model/CollectorAlbum.kt
package com.dev.wine.music.app.domain.model

data class CollectorAlbum(
    val id: Int,
    val price: Int,
    val status: CollectorAlbumStatus,
    val album: Album?,
)

enum class CollectorAlbumStatus { ACTIVE, INACTIVE }

/** Lo que muestra la pantalla de detalle: el coleccionista y su colección. */
data class CollectorDetail(
    val collector: Collector,
    val albums: List<CollectorAlbum>,
)
```

## 4. Mapper

Funciones de extensión `toDomain()` en `data/mapper/<Entidad>Mapper.kt`. Aquí se traducen textos del backend a enums.

```kotlin
// data/mapper/CollectorMapper.kt
fun CollectorAlbumDto.toDomain(): CollectorAlbum = CollectorAlbum(
    id = id,
    price = price,
    status = if (status == "Active") CollectorAlbumStatus.ACTIVE else CollectorAlbumStatus.INACTIVE,
    album = album?.toDomain(),
)
```

## 5. Repository

Interfaz en `domain`, implementación en `data`. El repositorio devuelve modelos de dominio, nunca DTO.

```kotlin
// domain/repository/CollectorRepository.kt
interface CollectorRepository {
    suspend fun getCollectors(): List<Collector>
    suspend fun getCollector(collectorId: Int): Collector
    suspend fun getCollectorAlbums(collectorId: Int): List<CollectorAlbum>
}

// data/repository/CollectorRepositoryImpl.kt
class CollectorRepositoryImpl @Inject constructor(
    private val api: CollectorApi,
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> =
        api.getCollectors().map { it.toDomain() }

    override suspend fun getCollector(collectorId: Int): Collector =
        api.getCollector(collectorId).toDomain()

    override suspend fun getCollectorAlbums(collectorId: Int): List<CollectorAlbum> =
        api.getCollectorAlbums(collectorId).map { it.toDomain() }
}
```

Si ya existe `ui/common/ErrorType` / `data/remote/SafeApiCall.kt` (ver [errors.md](errors.md)), envuelve cada llamada: `= safeApiCall { api.getCollector(collectorId).toDomain() }`.

Si el repositorio es **nuevo**, agrégalo en `di/RepositoryModule.kt`:

```kotlin
@Binds
@Singleton
abstract fun bindXxxRepository(impl: XxxRepositoryImpl): XxxRepository
```

Al cambiar una interfaz, actualiza también su fake en `app/src/test/.../fakes/FakeRepositories.kt` o las pruebas no compilan.

## 6. Caso de uso

Una acción de negocio. Ordenar, combinar, cruzar datos de varios endpoints y reglas de negocio van aquí, no en el ViewModel ni en el repositorio. Llamadas independientes en paralelo.

```kotlin
// domain/usecase/GetCollectorDetailUseCase.kt
class GetCollectorDetailUseCase @Inject constructor(
    private val repository: CollectorRepository,
) {
    suspend operator fun invoke(collectorId: Int): CollectorDetail = coroutineScope {
        val collector = async { repository.getCollector(collectorId) }
        val albums = async { repository.getCollectorAlbums(collectorId) }
        CollectorDetail(
            collector = collector.await(),
            albums = albums.await().sortedBy { it.album?.name?.lowercase() },
        )
    }
}
```

Cruces conocidos del backend que van en un caso de uso: autor de comentarios (HU02) desde `GET /collectors`, nombre del premio (HU04) desde `GET /performerprizes`, banda de un músico (HU03/HU04) desde `GET /bands`.

## 7. ViewModel

### Detalle (id por navegación)

Navigation Compose guarda las propiedades de la ruta tipada en `SavedStateHandle` con el nombre de la propiedad. Declara la clave en un `companion object` para usarla en las pruebas.

```kotlin
// ui/collector/detail/CollectorDetailViewModel.kt
@HiltViewModel
class CollectorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCollectorDetail: GetCollectorDetailUseCase,
) : ViewModel() {

    // Navigation pone aquí CollectorDetailRoute.collectorId con el nombre de la propiedad.
    private val collectorId: Int = checkNotNull(savedStateHandle[COLLECTOR_ID_KEY])

    private val _uiState = MutableStateFlow<UiState<CollectorDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<CollectorDetail>> = _uiState.asStateFlow()

    init {
        loadCollector()
    }

    fun loadCollector() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getCollectorDetail(collectorId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.message)   // con errors.md: e.toUiError()
            }
        }
    }

    companion object {
        const val COLLECTOR_ID_KEY = "collectorId"
    }
}
```

### Lista con búsqueda y filtros (HU01, HU03, HU05)

La carga sigue igual; la búsqueda se combina con el resultado ya cargado, sin volver a llamar a la API.

```kotlin
@HiltViewModel
class CollectorListViewModel @Inject constructor(
    private val getCollectors: GetCollectorsUseCase,
) : ViewModel() {

    private val loadState = MutableStateFlow<UiState<List<Collector>>>(UiState.Loading)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<UiState<List<Collector>>> =
        combine(loadState, _query) { state, query ->
            if (state is UiState.Success && query.isNotBlank()) {
                UiState.Success(state.data.filter { it.name.contains(query.trim(), ignoreCase = true) })
            } else {
                state
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    init {
        loadCollectors()
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun loadCollectors() {
        viewModelScope.launch {
            loadState.value = UiState.Loading
            loadState.value = try {
                UiState.Success(getCollectors())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.message)
            }
        }
    }
}
```

- Un filtro por tipo/género es otro `MutableStateFlow` (por ejemplo `MutableStateFlow<ArtistType?>(null)`) en el mismo `combine`.
- Con búsqueda activa, una lista vacía debe mostrar «Sin resultados» y no el `EmptyView` genérico: agrega un string propio.
- `stateIn(WhileSubscribed)` cambia cómo se prueba: ver «ViewModel con stateIn» en [testing.md](testing.md).

## 8. Pantalla: Screen + Content

`Screen` conecta el ViewModel; `Content` solo pinta. Las acciones salen como lambdas.

```kotlin
// ui/collector/detail/CollectorDetailScreen.kt

/** Punto de entrada con estado que usa la navegación. */
@Composable
fun CollectorDetailScreen(
    onBack: () -> Unit,
    onAlbumClick: (albumId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectorDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectorDetailContent(
        uiState = uiState,
        onBack = onBack,
        onAlbumClick = onAlbumClick,
        onRetry = viewModel::loadCollector,
        modifier = modifier,
    )
}

/** Interfaz sin estado: se puede previsualizar y probar sin Hilt ni red. */
@Composable
fun CollectorDetailContent(
    uiState: UiState<CollectorDetail>,
    onBack: () -> Unit,
    onAlbumClick: (albumId: Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
        }
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry)
            is UiState.Success -> CollectorDetailBody(uiState.data, onAlbumClick)
        }
    }
}

@Composable
private fun CollectorDetailBody(detail: CollectorDetail, onAlbumClick: (Int) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(detail.collector.name, style = MaterialTheme.typography.headlineSmall) }
        if (detail.albums.isEmpty()) {
            item { Text(stringResource(R.string.collector_no_albums)) }
        }
        items(detail.albums, key = { it.id }) { item ->
            // Reutiliza componentes de ui/components cuando existan.
        }
    }
}
```

Para listas con búsqueda, `Screen` también observa `viewModel.query` y pasa `query` + `onQueryChange` al `Content`.

Agrega un `@Preview` del `Content` con datos de ejemplo cuando la pantalla tenga diseño propio (envuélvelo en `WineMusicAppTheme`).

## 9. Textos

En `app/src/main/res/values/strings.xml`, en español, con prefijo de la entidad para textos propios:

```xml
<string name="collector_no_albums">Este coleccionista aún no tiene álbumes.</string>
<string name="collector_album_price">Precio: %1$d</string>
```

## 10. Navegación

```kotlin
// ui/navigation/Routes.kt
@Serializable data class CollectorDetailRoute(val collectorId: Int)
```

El nombre de la propiedad (`collectorId`) debe ser igual a la clave `COLLECTOR_ID_KEY` del ViewModel.

```kotlin
// ui/navigation/WineMusicNavHost.kt
composable<CollectorsRoute> {
    CollectorListScreen(
        onCollectorClick = { id -> navController.navigate(CollectorDetailRoute(id)) },
    )
}
composable<CollectorDetailRoute> {
    CollectorDetailScreen(
        onBack = { navController.popBackStack() },
        onAlbumClick = { albumId -> navController.navigate(AlbumDetailRoute(albumId)) },
    )
}
```

- Si es una pestaña nueva de la barra inferior, agrégala en `TopLevelDestination` con su `labelRes` e ícono. Las pantallas de detalle no van ahí (así la barra se oculta sola).
- Varios argumentos van en la misma ruta, con tipos primitivos: `ArtistDetailRoute(val artistId: Int, val type: String)` y en el ViewModel `ArtistType.valueOf(checkNotNull(savedStateHandle[TYPE_KEY]))`. Leer con claves de `SavedStateHandle` (y no con `toRoute()`) permite probar el ViewModel en pruebas unitarias sin Robolectric.
