# Pruebas

Herramientas que ya están en el proyecto: JUnit 4, `kotlinx-coroutines-test` y `ui-test-junit4` para Compose. No hay MockK ni Mockito: se usan **fakes** escritos a mano. No agregues librerías de mocks sin preguntar.

| Qué | Dónde | Tipo |
|---|---|---|
| Fakes de repositorios | `app/src/test/java/com/dev/wine/music/app/fakes/FakeRepositories.kt` | apoyo |
| Builders de datos | `app/src/test/.../fakes/TestData.kt` | apoyo |
| Regla para `Dispatchers.Main` | `app/src/test/.../util/MainDispatcherRule.kt` | apoyo |
| Mappers | `app/src/test/.../data/mapper/` | unitaria |
| Repositorios (con Api falsa) | `app/src/test/.../data/repository/` | unitaria |
| Casos de uso | `app/src/test/.../domain/usecase/` | unitaria |
| ViewModels | `app/src/test/.../ui/<entidad>/<accion>/` | unitaria |
| `Content` de Compose | `app/src/androidTest/.../ui/<entidad>/` | instrumentada |

Nombres de prueba en inglés con backticks, describiendo el comportamiento: `` `emits Error when the repository fails` ``. Estructura preparar / ejecutar / verificar separada por líneas en blanco.

## Fakes y TestData

Al agregar funciones a un repositorio, agrégalas a su fake. Sigue el patrón actual: datos en `var` y `shouldFail`.

```kotlin
class FakeCollectorRepository(
    var collectors: List<Collector> = emptyList(),
    var collectorAlbums: Map<Int, List<CollectorAlbum>> = emptyMap(),
    var shouldFail: Boolean = false,
    /** Error a lanzar cuando shouldFail es true. */
    var error: Exception = IOException("Network error"),
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        if (shouldFail) throw error
        return collectors
    }

    override suspend fun getCollector(collectorId: Int): Collector {
        if (shouldFail) throw error
        return collectors.first { it.id == collectorId }
    }

    override suspend fun getCollectorAlbums(collectorId: Int): List<CollectorAlbum> {
        if (shouldFail) throw error
        return collectorAlbums[collectorId].orEmpty()
    }
}
```

Para formularios, el fake guarda lo recibido para poder verificarlo:

```kotlin
var createdAlbums = mutableListOf<NewAlbum>()
override suspend fun createAlbum(newAlbum: NewAlbum): Album {
    if (shouldFail) throw error
    createdAlbums += newAlbum
    return TestData.album(id = 999, name = newAlbum.name)
}
```

Builders con valores por defecto en `TestData`, para que cada prueba solo fije lo que le importa:

```kotlin
fun collectorAlbum(
    id: Int = 1,
    price: Int = 35,
    status: CollectorAlbumStatus = CollectorAlbumStatus.ACTIVE,
    album: Album? = album(),
) = CollectorAlbum(id = id, price = price, status = status, album = album)
```

## Caso de uso

```kotlin
class GetCollectorDetailUseCaseTest {

    @Test
    fun `returns the collector with its albums sorted by name`() = runTest {
        val repository = FakeCollectorRepository(
            collectors = listOf(TestData.collector(id = 1)),
            collectorAlbums = mapOf(
                1 to listOf(
                    TestData.collectorAlbum(id = 1, album = TestData.album(name = "Poeta del pueblo")),
                    TestData.collectorAlbum(id = 2, album = TestData.album(name = "Buscando América")),
                ),
            ),
        )

        val detail = GetCollectorDetailUseCase(repository)(collectorId = 1)

        assertEquals(listOf(2, 1), detail.albums.map { it.id })
    }
}
```

## ViewModel con `MutableStateFlow`

`MainDispatcherRule` usa `UnconfinedTestDispatcher`, así que lo lanzado en `init` ya terminó cuando se lee `uiState.value`. Casos mínimos: éxito, error y reintento.

```kotlin
class CollectorDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCollectorRepository(collectors = listOf(TestData.collector(id = 5)))

    private fun createViewModel(id: Int = 5) = CollectorDetailViewModel(
        SavedStateHandle(mapOf(CollectorDetailViewModel.COLLECTOR_ID_KEY to id)),
        GetCollectorDetailUseCase(repository),
    )

    @Test
    fun `loads the collector whose id comes from navigation`() {
        val state = createViewModel().uiState.value

        assertTrue(state is UiState.Success)
        assertEquals(5, (state as UiState.Success).data.collector.id)
    }

    @Test
    fun `emits Error when the repository fails`() {
        repository.shouldFail = true

        assertTrue(createViewModel().uiState.value is UiState.Error)
    }

    @Test
    fun `retry after an error loads the collector`() {
        repository.shouldFail = true
        val viewModel = createViewModel()

        repository.shouldFail = false
        viewModel.loadCollector()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
```

Con errores por tipo ([errors.md](errors.md)), agrega:

```kotlin
@Test
fun `a missing collector shows NOT_FOUND`() {
    repository.shouldFail = true
    repository.error = AppError.NotFound("The collector with the given id was not found")

    val state = createViewModel().uiState.value as UiState.Error

    assertEquals(ErrorType.NOT_FOUND, state.type)
}
```

## ViewModel con `stateIn` (búsqueda y filtros)

Un `StateFlow` creado con `stateIn(WhileSubscribed)` no se calcula si nadie lo observa. En la prueba, suscríbete en `backgroundScope` antes de leer `value`:

```kotlin
@Test
fun `search filters collectors by name ignoring case`() = runTest {
    val viewModel = CollectorListViewModel(
        GetCollectorsUseCase(
            FakeCollectorRepository(
                collectors = listOf(TestData.collector(id = 1, name = "Manolo Bellon"), TestData.collector(id = 2, name = "Jaime Monsalve")),
            ),
        ),
    )
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

    viewModel.onQueryChange("mano")

    val state = viewModel.uiState.value as UiState.Success
    assertEquals(listOf("Manolo Bellon"), state.data.map { it.name })
}
```

## ViewModel de formulario

Casos mínimos: campos vacíos marcan error y no llaman al repositorio; datos válidos envían y exponen el id creado; un rechazo del backend conserva lo escrito.

```kotlin
@Test
fun `submit with empty fields shows errors and does not call the repository`() {
    val viewModel = AlbumCreateViewModel(CreateAlbumUseCase(repository))

    viewModel.submit()

    assertTrue(AlbumField.NAME in viewModel.uiState.value.fieldErrors)
    assertTrue(repository.createdAlbums.isEmpty())
}

@Test
fun `a backend rejection keeps the typed values`() {
    repository.shouldFail = true
    repository.error = AppError.Validation("\"genre\" must be one of [Classical, Salsa, Rock, Folk]")
    val viewModel = AlbumCreateViewModel(CreateAlbumUseCase(repository)).apply { fillValidForm() }

    viewModel.submit()

    val state = viewModel.uiState.value
    assertEquals("Buscando América", state.name)
    assertEquals(ErrorType.VALIDATION, state.submitError?.type)
    assertFalse(state.isSubmitting)
}
```

`fillValidForm()` es una función privada de la clase de prueba que llama a cada `onXxxChange` con datos válidos.

## Mapper y repositorio

Mapper cuando hay lógica (inferir tipos, traducir textos a enums, valores por defecto). Repositorio con una Api falsa cuando combina llamadas, como `ArtistRepositoryImplTest`:

```kotlin
private class FakeCollectorApi : CollectorApi {
    override suspend fun getCollectors() = listOf(CollectorDto(id = 1, name = "Manolo Bellon"))
    override suspend fun getCollector(collectorId: Int) = CollectorDto(id = collectorId, name = "Manolo Bellon")
    override suspend fun getCollectorAlbums(collectorId: Int) = listOf(CollectorAlbumDto(id = 1, status = "Inactive"))
}
```

Al agregar una función a una Api, actualiza también las Api falsas de las pruebas existentes.

## Compose: probar el `Content`

Se prueba el `Content` sin estado, así no hace falta Hilt ni red (ver `AlbumListContentTest`).

**No escribas textos fijos en las pruebas.** Lee el texto desde los recursos: así la prueba no se rompe cuando cambia el idioma. (Hoy `AlbumListContentTest` busca `"Retry"` mientras `strings.xml` dice «Reintentar»; si tocas esa prueba, corrígela con este patrón.)

```kotlin
@RunWith(AndroidJUnit4::class)
class CollectorDetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun errorStateShowsRetryButton() {
        var retried = false
        composeTestRule.setContent {
            WineMusicAppTheme {
                CollectorDetailContent(
                    uiState = UiState.Error(),
                    onBack = {},
                    onAlbumClick = {},
                    onRetry = { retried = true },
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.retry)).performClick()

        assertTrue(retried)
    }
}
```

En `androidTest` no están los builders de `TestData` (viven en `test`). Crea los objetos de dominio en la misma prueba, como hace `AlbumListContentTest`.

## Correr las pruebas

```bash
./gradlew test                              # unitarias
./gradlew testDebugUnitTest --tests "*CollectorDetail*"   # solo las del feature
./gradlew connectedDebugAndroidTest         # Compose, requiere emulador
```
