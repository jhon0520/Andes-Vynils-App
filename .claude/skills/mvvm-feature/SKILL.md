---
name: mvvm-feature
description: Implementa un feature o historia de usuario (HU) nueva en la app Android Wine Music / Vinilos siguiendo MVVM + Clean Architecture del proyecto (Compose, Hilt, Retrofit, StateFlow<UiState>). Úsala cuando pidan crear una pantalla, HU, listado, detalle, formulario, endpoint o caso de uso nuevo; completar una HU parcial (búsqueda, filtros, navegación); o agregar manejo de errores por tipo. Triggers: "nuevo feature", "implementar HU", "HU04", "crear pantalla", "agregar endpoint", "formulario", "detalle de artista/coleccionista", "new feature", "new screen".
---

# Implementar un feature con MVVM en Wine Music

La app ya tiene una arquitectura fija. Esta skill no inventa una nueva: copia la cadena que existe para que cada HU se vea igual que `album/list` y `album/detail`.

```
Screen (con estado) ─ Content (sin estado)        ui/<entidad>/<accion>/
        │ observa StateFlow<UiState<T>>
    ViewModel (@HiltViewModel)                     ui/<entidad>/<accion>/
        │ invoca
    UseCase (operator fun invoke)                  domain/usecase/
        │ depende de la interfaz
    Repository (interfaz)                          domain/repository/
        ▲ implementa
    RepositoryImpl ── mapper toDomain() ── Dto     data/repository · data/mapper · data/remote/dto
        │ llama con suspend
    <Entidad>Api (Retrofit)                        data/remote/api/
```

Hilt une todo: `di/NetworkModule` crea las APIs y `di/RepositoryModule` enlaza cada interfaz con su implementación.

## Paso 0 · Entender la HU antes de escribir código

1. Lee la fila de la HU en el **mapa de trazabilidad** de `docs/App-technical-documentation.md`. Ahí están los nombres esperados de Screen, ViewModel, UseCase, Repository, Api y endpoint. Úsalos tal cual.
2. Lee la sección **Consumo de la API REST** y **Detalles del backend** del mismo documento, y la sección del endpoint en `docs/Backend-technical-documentation.md`. El backend tiene trampas conocidas (POST responden 200, comentarios sin autor, premios solo con id, etc.).
3. Revisa qué ya existe: `grep` por el nombre de la entidad en `app/src/main/java`. Muchas HU solo agregan una función a un repositorio o API existente.
4. Si falta algo, **pregunta al usuario antes de seguir**: número de HU, endpoint, qué se muestra en pantalla, si la acción es solo para perfil coleccionista, y si hay mockup en Figma.
5. Clasifica el feature y lee la referencia que toca:

| Tipo de feature | Referencia |
|---|---|
| Lista o detalle que lee datos (GET), búsqueda o filtros | [references/layers.md](references/layers.md) |
| Formulario que crea o modifica datos (POST/PUT) | [references/layers.md](references/layers.md) + [references/forms.md](references/forms.md) |
| Distinguir errores (sin red, 404, 400/412) | [references/errors.md](references/errors.md) |
| Pruebas de cualquier capa | [references/testing.md](references/testing.md) |
| Rama, commits, documentación y PR | [references/docs-and-git.md](references/docs-and-git.md) |

Presenta al usuario un plan corto con la lista de archivos que vas a crear o tocar antes de empezar.

## Paso 1 · Rama

Desde `develop` actualizado: `feature/huXX-descripcion-corta` (o `feature/<tarea>` para tareas transversales). Detalles en [docs-and-git.md](references/docs-and-git.md). Si hay cambios sin commit en el árbol, pregunta antes de cambiar de rama.

## Paso 2 · Implementar de abajo hacia arriba

Cada capa compila sobre la anterior, así los errores salen pronto. Las plantillas exactas están en [layers.md](references/layers.md).

1. **DTO** `data/remote/dto/<Entidad>Dto.kt`: `@Serializable`, valores por defecto en campos opcionales.
2. **Api** `data/remote/api/<Entidad>Api.kt`: una función `suspend` por endpoint. Si la Api es nueva, agrégala en `NetworkModule`.
3. **Modelo de dominio** `domain/model/<Entidad>.kt`: `data class` pura, sin anotaciones de Android ni de serialización.
4. **Mapper** `data/mapper/<Entidad>Mapper.kt`: `fun <Entidad>Dto.toDomain()`.
5. **Repository**: interfaz en `domain/repository`, implementación `@Inject constructor` en `data/repository`. Si es nuevo, agrega el `@Binds` en `RepositoryModule`.
6. **UseCase** `domain/usecase/<Verbo><Entidad>UseCase.kt`: una acción, `suspend operator fun invoke`. Aquí van ordenar, combinar y cruzar datos.
7. **ViewModel** `ui/<entidad>/<accion>/<Entidad><Accion>ViewModel.kt`: `@HiltViewModel`, `MutableStateFlow` privado y `StateFlow` público, carga en `init`, función pública para reintentar.
8. **Screen + Content** en el mismo archivo `<Entidad><Accion>Screen.kt`: `Screen` obtiene el ViewModel con `hiltViewModel()` y usa `collectAsStateWithLifecycle()`; `Content` recibe estado y lambdas, y pinta Loading/Error/Empty/Success.
9. **Textos** en `res/values/strings.xml`, en español.
10. **Navegación**: ruta `@Serializable` en `ui/navigation/Routes.kt`, `composable<Ruta>` en `WineMusicNavHost.kt`. Si es pestaña, agrégala en `TopLevelDestination`.

## Paso 3 · Pruebas

Mínimo por HU (plantillas en [testing.md](references/testing.md)):

- Fake del repositorio en `app/src/test/.../fakes/FakeRepositories.kt` y builder en `TestData.kt`.
- Prueba del UseCase (orden, combinación, cruces).
- Prueba del ViewModel: éxito, error, reintento; en detalle, el id que llega por `SavedStateHandle`; en formularios, validación y envío.
- Prueba del mapper cuando hay lógica (inferir tipos, cruzar campos).
- Prueba Compose del `Content` en `androidTest` cuando hay interacción (clics, formulario).

## Paso 4 · Verificar

```bash
./gradlew test            # pruebas unitarias
./gradlew assembleDebug   # compila la app
```

Si hay emulador y backend corriendo, `./gradlew connectedDebugAndroidTest` para las pruebas Compose. No declares el feature terminado si algo falla: reporta qué falló y por qué.

## Paso 5 · Documentación, checklist y commits

Actualiza `docs/App-technical-documentation.md` (mapa de trazabilidad, diagrama de clases, tabla de endpoints, rutas) y pasa la checklist del PR. Ver [docs-and-git.md](references/docs-and-git.md). Propón los commits con el formato `feat(ANDES): ...`; haz commit o push solo si el usuario lo pide.

## Reglas que no se rompen

**Capas**
- `ui` nunca importa `data` ni `retrofit2`. `domain` nunca importa Android, Compose, Retrofit ni kotlinx.serialization.
- El ViewModel no conoce DTO, URLs ni `Context`. Recibe casos de uso por constructor, nunca repositorios ni APIs.
- Toda dependencia entra por constructor con `@Inject`; nada de singletons a mano ni `object` con estado.

**Estado y corrutinas**
- Estado de pantalla siempre como `StateFlow<UiState<T>>` (o un `data class` de estado en formularios). Nada de `LiveData` ni `mutableStateOf` dentro del ViewModel.
- Corrutinas solo en `viewModelScope`. En cada `catch (e: Exception)` vuelve a lanzar `CancellationException` antes.
- Llamadas independientes en paralelo con `coroutineScope { async {} }`, como `ArtistRepositoryImpl`.
- Búsqueda y filtros se aplican en el ViewModel sobre la lista ya cargada, combinando flujos; no vuelvas a llamar a la API por cada letra.

**Compose**
- `Content` es sin estado y recibe `modifier: Modifier = Modifier` como primer parámetro opcional.
- Listas con `LazyColumn`/`LazyVerticalGrid` y `key` estable (`it.id`; si los ids pueden repetirse entre tipos, combina tipo e id como en artistas).
- Reutiliza `LoadingView`, `ErrorView`, `EmptyView` y tarjetas de `ui/components`. Si un componente se usa en dos pantallas, muévelo ahí.
- Colores y tipografía solo de `MaterialTheme` (definidos en `ui/theme`); nada de `Color(0x...)` ni tamaños de fuente en las pantallas.
- Todo texto visible sale de `stringResource`. Imágenes con Coil `AsyncImage` y `contentDescription` (o `null` si es decorativa).
- Navegación por lambdas (`onAlbumClick: (Int) -> Unit`): la pantalla no recibe el `NavController`.

**Mobile**
- Tres estados siempre visibles: carga, error con reintento y lista vacía.
- Argumentos de navegación pequeños (ids), nunca objetos completos; el ViewModel los lee de `SavedStateHandle`.
- Acciones de coleccionista (crear álbum, agregar track) solo se muestran con ese perfil. Si `Profile` aún no existe, deja la acción visible y avisa al usuario que depende de la tarea de perfil.
- No bloquees el hilo principal ni hagas trabajo pesado en composables; los cálculos van en el ViewModel o el UseCase.
