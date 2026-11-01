# Formularios (POST / PUT)

Todavía no hay formularios en el código; este es el patrón para **HU07 Crear álbum** (`POST /albums`) y **HU08 Asociar tracks** (`POST /albums/{id}/tracks`). Las capas de abajo (DTO, Api, mapper, repositorio, caso de uso) siguen [layers.md](layers.md); lo que cambia es el estado del ViewModel y la pantalla.

Diferencias con una pantalla de lectura:

- El estado no es `UiState<T>` sino un `data class` con los campos, sus errores, si se está enviando y el resultado.
- La validación ocurre en el ViewModel antes de llamar al caso de uso.
- Un error del backend (400/412) se muestra **junto al formulario** sin borrar lo escrito.
- Al terminar, la pantalla navega una sola vez.

Si los errores por tipo aún no existen, implementa primero [errors.md](errors.md) o usa `UiState.Error(e.message)` y avisa al usuario.

## Reglas del backend que afectan el formulario

- Joi rechaza con 400 **cualquier campo extra** (por ejemplo `id`). El DTO de request lleva solo los campos del esquema.
- `genre`: `Classical`, `Salsa`, `Rock`, `Folk`. `recordLabel`: `Sony Music`, `EMI`, `Discos Fuentes`, `Elektra`, `Fania Records`. Texto exacto.
- Los POST responden `200`, no `201`; cualquier 2xx es éxito (Retrofit ya lo maneja así).
- `duration` de un track en formato `m:ss` o `mm:ss`.

## 1. Dominio

Los valores cerrados van como `enum` en dominio. El texto que espera el backend lo pone el mapper; el texto en pantalla lo pone `strings.xml`.

```kotlin
// domain/model/NewAlbum.kt
data class NewAlbum(
    val name: String,
    val cover: String,
    val releaseDate: String,   // "yyyy-MM-dd"
    val description: String,
    val genre: Genre,
    val recordLabel: RecordLabel,
)

enum class Genre { CLASSICAL, SALSA, ROCK, FOLK }
enum class RecordLabel { SONY_MUSIC, EMI, DISCOS_FUENTES, ELEKTRA, FANIA_RECORDS }
```

## 2. Data

```kotlin
// data/remote/dto/AlbumRequestDto.kt
@Serializable
data class AlbumRequestDto(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
)

// data/mapper/AlbumMapper.kt
fun NewAlbum.toRequest(): AlbumRequestDto = AlbumRequestDto(
    name = name.trim(),
    cover = cover.trim(),
    releaseDate = releaseDate,
    description = description.trim(),
    genre = genre.toApi(),
    recordLabel = recordLabel.toApi(),
)

private fun Genre.toApi(): String = when (this) {
    Genre.CLASSICAL -> "Classical"
    Genre.SALSA -> "Salsa"
    Genre.ROCK -> "Rock"
    Genre.FOLK -> "Folk"
}

private fun RecordLabel.toApi(): String = when (this) {
    RecordLabel.SONY_MUSIC -> "Sony Music"
    RecordLabel.EMI -> "EMI"
    RecordLabel.DISCOS_FUENTES -> "Discos Fuentes"
    RecordLabel.ELEKTRA -> "Elektra"
    RecordLabel.FANIA_RECORDS -> "Fania Records"
}

// data/remote/api/AlbumApi.kt
@POST("albums")
suspend fun createAlbum(@Body body: AlbumRequestDto): AlbumDto

// AlbumRepository / AlbumRepositoryImpl
suspend fun createAlbum(newAlbum: NewAlbum): Album
override suspend fun createAlbum(newAlbum: NewAlbum): Album =
    safeApiCall { api.createAlbum(newAlbum.toRequest()).toDomain() }

// domain/usecase/CreateAlbumUseCase.kt
class CreateAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
    suspend operator fun invoke(newAlbum: NewAlbum): Album = repository.createAlbum(newAlbum)
}
```

## 3. Estado del formulario

```kotlin
// ui/album/create/AlbumCreateUiState.kt
enum class AlbumField { NAME, COVER, RELEASE_DATE, DESCRIPTION, GENRE, RECORD_LABEL }

data class AlbumCreateUiState(
    val name: String = "",
    val cover: String = "",
    val releaseDate: String = "",
    val description: String = "",
    val genre: Genre? = null,
    val recordLabel: RecordLabel? = null,
    /** Error de cada campo como recurso de texto; vacío si todo es válido. */
    val fieldErrors: Map<AlbumField, Int> = emptyMap(),
    val isSubmitting: Boolean = false,
    /** Error al enviar (red o rechazo del backend). Lo escrito se conserva. */
    val submitError: UiState.Error? = null,
    /** Id del álbum creado: la pantalla navega cuando deja de ser null. */
    val createdAlbumId: Int? = null,
)
```

## 4. ViewModel

```kotlin
// ui/album/create/AlbumCreateViewModel.kt
@HiltViewModel
class AlbumCreateViewModel @Inject constructor(
    private val createAlbum: CreateAlbumUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumCreateUiState())
    val uiState: StateFlow<AlbumCreateUiState> = _uiState.asStateFlow()

    // Un evento por campo; al escribir se borra el error de ese campo.
    fun onNameChange(value: String) = _uiState.update {
        it.copy(name = value, fieldErrors = it.fieldErrors - AlbumField.NAME)
    }
    fun onGenreChange(value: Genre) = _uiState.update {
        it.copy(genre = value, fieldErrors = it.fieldErrors - AlbumField.GENRE)
    }
    // ... igual para cover, releaseDate, description, recordLabel

    fun submit() {
        val state = _uiState.value
        if (state.isSubmitting) return   // evita doble envío

        val errors = validate(state)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            try {
                val album = createAlbum(state.toNewAlbum())
                _uiState.update { it.copy(isSubmitting = false, createdAlbumId = album.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, submitError = e.toUiError()) }
            }
        }
    }

    /** La pantalla lo llama después de navegar, para no navegar dos veces. */
    fun onNavigationHandled() = _uiState.update { it.copy(createdAlbumId = null) }

    private fun validate(state: AlbumCreateUiState): Map<AlbumField, Int> = buildMap {
        if (state.name.isBlank()) put(AlbumField.NAME, R.string.error_required)
        if (!state.cover.startsWith("http")) put(AlbumField.COVER, R.string.error_invalid_url)
        if (!state.releaseDate.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) put(AlbumField.RELEASE_DATE, R.string.error_invalid_date)
        if (state.description.isBlank()) put(AlbumField.DESCRIPTION, R.string.error_required)
        if (state.genre == null) put(AlbumField.GENRE, R.string.error_required)
        if (state.recordLabel == null) put(AlbumField.RECORD_LABEL, R.string.error_required)
    }

    private fun AlbumCreateUiState.toNewAlbum() = NewAlbum(
        name = name,
        cover = cover,
        releaseDate = releaseDate,
        description = description,
        genre = checkNotNull(genre),
        recordLabel = checkNotNull(recordLabel),
    )
}
```

Notas:

- Guardar `@StringRes Int` en el estado mantiene al ViewModel sin `Context` y deja el texto a la pantalla.
- Usa `_uiState.update { }` (atómico) en vez de `_uiState.value = _uiState.value.copy(...)`.
- Navegar al terminar se modela como estado (`createdAlbumId`) + confirmación (`onNavigationHandled`), según la guía oficial de Android. No uses `Channel` ni `SharedFlow` para esto salvo que el usuario lo pida.
- Si el formulario es largo y debe sobrevivir a que Android mate el proceso, guarda los campos en `SavedStateHandle` (`savedStateHandle.getStateFlow`). Pregunta al usuario si lo necesita.

## 5. Pantalla

```kotlin
@Composable
fun AlbumCreateScreen(
    onBack: () -> Unit,
    onAlbumCreated: (albumId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumCreateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.createdAlbumId) {
        uiState.createdAlbumId?.let { id ->
            onAlbumCreated(id)
            viewModel.onNavigationHandled()
        }
    }

    AlbumCreateContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onGenreChange = viewModel::onGenreChange,
        // ...
        onSubmit = viewModel::submit,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun AlbumCreateContent(
    uiState: AlbumCreateUiState,
    onNameChange: (String) -> Unit,
    onGenreChange: (Genre) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.album_name)) },
            isError = AlbumField.NAME in uiState.fieldErrors,
            supportingText = uiState.fieldErrors[AlbumField.NAME]?.let { { Text(stringResource(it)) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            enabled = !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )
        // ... resto de campos. Para Genre/RecordLabel usa ExposedDropdownMenuBox
        //     y muestra stringResource(genre.labelRes()) definido en ui.

        uiState.submitError?.let { error ->
            Text(
                text = error.message?.takeIf { error.type == ErrorType.VALIDATION }
                    ?: stringResource(R.string.error_loading),
                color = MaterialTheme.colorScheme.error,
            )
        }

        Button(
            onClick = onSubmit,
            enabled = !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.save))
            }
        }
    }
}
```

Buenas prácticas del formulario:

- `keyboardOptions` según el campo: `KeyboardType.Uri` para la portada, `ImeAction.Next` entre campos y `ImeAction.Done` en el último.
- Fecha con `DatePickerDialog` de Material 3; convierte `selectedDateMillis` a `"yyyy-MM-dd"` en el ViewModel con `SimpleDateFormat("yyyy-MM-dd", Locale.US)` en UTC. No uses `java.time` sin revisar: `minSdk` es 24 y requiere desugaring.
- Etiquetas de enums en `ui` (por ejemplo `fun Genre.labelRes(): Int`), nunca `genre.name` en pantalla. El diseño muestra «Clásica» y se envía `Classical`.
- Deshabilita campos y botón mientras `isSubmitting`.
- Los mensajes de Joi vienen en inglés. Muéstralos solo si el usuario lo acepta; si no, usa un texto propio en español.

## 6. Navegación y perfil

```kotlin
@Serializable data object AlbumCreateRoute

composable<AlbumCreateRoute> {
    AlbumCreateScreen(
        onBack = { navController.popBackStack() },
        onAlbumCreated = { id ->
            navController.navigate(AlbumDetailRoute(id)) {
                popUpTo<AlbumCreateRoute> { inclusive = true }   // «Atrás» no vuelve al formulario
            }
        },
    )
}
```

El botón que abre el formulario («Crear álbum» en HU01) solo se muestra con perfil coleccionista.

## HU08: hoja dentro del detalle

«Agregar track» es una `ModalBottomSheet` dentro de `AlbumDetailScreen`, no una ruta nueva.

- `AddTrackViewModel` con `hiltViewModel()` dentro de la hoja queda ligado a la entrada `AlbumDetailRoute`, así que lee el mismo `albumId` de `SavedStateHandle` (`AlbumDetailViewModel.ALBUM_ID_KEY`).
- Estado propio `AddTrackUiState(name, duration, fieldErrors, isSubmitting, submitError, isSaved)`.
- Al guardar: cerrar la hoja, llamar `onTrackAdded`, que en `AlbumDetailScreen` ejecuta `viewModel.loadAlbum()` para refrescar la lista de tracks.
- Validación de duración: `Regex("""\d{1,2}:[0-5]\d""")`.
