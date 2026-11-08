# Plan: selección de perfil (Visitante / Coleccionista)

> Estado: **implementado** el 2026-11-08 en la rama `feature/onboarding` (plan creado el 2026-11-01).
> Ejecutar con el skill `mvvm-feature`, rama `feature/seleccion-perfil` desde `develop`.

## Objetivo

Agregar dos pantallas entre el splash y la pantalla inicial actual (Álbumes):

```
Splash → ¿Cómo quieres ingresar? ─┬─ Visitante ─────────────────────────────→ Álbumes
                                  └─ Coleccionista → ¿Quién eres? → Continuar → Álbumes
```

1. **Selección de rol** ("¿Cómo quieres ingresar?"): ilustración de vinilo "Vinilos", lema *"Compra, vende e intercambia discos de vinilo."*, opciones **Visitante** ("Explora álbumes, artistas y coleccionistas") y **Coleccionista** ("Comenta, marca favoritos y gestiona tu colección").
2. **Perfil de coleccionista** ("¿Quién eres?"): barra con flecha atrás y título "PERFIL DE COLECCIONISTA", búsqueda por nombre o correo, lista de coleccionistas de la API con radio, botón "Continuar como {nombre}".

## Decisiones tomadas

| # | Decisión |
|---|----------|
| 1 | **No** se recuerda el rol: cada vez que se abre la app se debe escoger el tipo de usuario (también el Visitante). |
| 2 | La sesión vive **solo en memoria mientras el proceso de la app está vivo**. Al cerrar el proceso y reabrir, se vuelve a pedir el tipo de usuario. **No se usa DataStore ni SharedPreferences.** |
| 3 | El ícono de perfil (arriba a la derecha) para cambiar de rol queda para la **fase 2**. |

Consecuencias en el diseño:
- El texto del mockup *"Se recordará en este dispositivo. Podrás cambiarlo luego."* ya no es cierto. Cambiarlo por algo como *"Podrás cambiar de perfil luego."* (confirmar con el equipo).
- El texto de pie *"Puedes cambiar de perfil cuando quieras desde el ícono de perfil, arriba a la derecha."* depende de la fase 2. Ocultarlo o mantenerlo cuando la fase 2 esté lista.
- `startDestination` es **siempre** el onboarding, así que no hay que esperar ninguna lectura al arrancar.

## Contexto del código (al 2026-11-01)

- `ui/navigation/WineMusicNavHost.kt`: `startDestination = AlbumsRoute`.
- `ui/navigation/MainScreen.kt`: la barra inferior solo se ve en `TopLevelDestination`, así que las pantallas nuevas no la muestran.
- `MainActivity.kt`: el splash se mantiene con un `delay(2000)` fijo. Se deja igual.
- Reutilizable: `GetCollectorsUseCase`, `CollectorRepository`, `CollectorApi` (`GET /collectors`), `Collector(id, name, telephone, email, commentIds)`, `ui/components/StateViews.kt`, `ui/common/UiState.kt`.
- Tema: `LightBackground = #F3EEE4` ya existe; fuente serif `LibronFontFamily` en `Type.kt`.
- Las E2E HU01–HU03 abren `MainActivity` y esperan `album_list` de entrada, así que **se rompen** al cambiar la pantalla inicial (ver fase E).

## Fase A — Dominio (`domain/`)

- `model/UserSession.kt`
  ```kotlin
  sealed interface UserSession {
      data object Visitor : UserSession
      data class Collector(val id: Int, val name: String, val email: String) : UserSession
  }
  ```
- `repository/SessionRepository.kt`
  ```kotlin
  interface SessionRepository {
      val session: StateFlow<UserSession?>
      fun selectVisitor()
      fun selectCollector(collector: Collector)
      fun clear() // para la fase 2
  }
  ```
- Casos de uso: `ObserveSessionUseCase`, `SelectVisitorUseCase`, `SelectCollectorUseCase`.

## Fase B — Datos (`data/` + `di/`)

- `data/repository/InMemorySessionRepository.kt`: guarda la sesión en un `MutableStateFlow<UserSession?>(null)`. Es `@Singleton`, así que vive lo que vive el proceso.
- `di/SessionModule.kt`: `@Binds` de `SessionRepository`, en un módulo propio para poder reemplazarlo en pruebas con `@TestInstallIn`.
- **Muerte del proceso:** si Android mata el proceso en segundo plano, la sesión se pierde, pero Navigation restaura la pila de pantallas. Para respetar la decisión 2, cuando una pantalla protegida detecte `session == null`, debe volver a `RoleSelectionRoute` limpiando la pila. Hacerlo en un solo lugar, por ejemplo en `MainScreen` con un `LaunchedEffect` sobre la sesión.

## Fase C — Navegación

- `Routes.kt`: `@Serializable data object OnboardingGraph`, `RoleSelectionRoute`, `CollectorPickerRoute`.
- `WineMusicNavHost`: `startDestination = OnboardingGraph` y un `navigation<OnboardingGraph>(startDestination = RoleSelectionRoute) { ... }`.
- Al terminar el onboarding:
  ```kotlin
  navController.navigate(AlbumsRoute) { popUpTo<OnboardingGraph> { inclusive = true } }
  ```
  Así, el botón atrás en Álbumes sale de la app.
- Atrás en "¿Quién eres?" vuelve a la selección de rol.

## Fase D — UI (`ui/onboarding/`)

### Selección de rol
- `RoleSelectionScreen` (con estado) + `RoleSelectionContent` (sin estado, para previews y pruebas) + `RoleSelectionViewModel`.
- Componentes:
  - `VinylHero`: funda verde con "LADO A · 33⅓" y "Vinilos" sobre un disco negro con surcos y centro terracota, dibujado con `Canvas`/Compose y sin imagen raster.
  - Lema en serif itálica.
  - Rótulo "¿CÓMO QUIERES INGRESAR?" en monoespaciada con espaciado amplio y una línea debajo.
  - `RoleOption(icon, title, subtitle, highlighted)`: el ícono va en un círculo (borde para Visitante, relleno terracota para Coleccionista) con un chevron y un separador.
- Visitante → `selectVisitor()` → Álbumes. Coleccionista → `CollectorPickerRoute`.
- testTags: `role_selection`, `role_visitor`, `role_collector`.

### Perfil de coleccionista ("¿Quién eres?")
- `CollectorPickerScreen` + `CollectorPickerContent` + `CollectorPickerViewModel`.
- Estado:
  - `collectors: UiState<List<Collector>>`, cargado con `GetCollectorsUseCase`.
  - `query` y `selectedId` en `SavedStateHandle` para que sobrevivan a la rotación.
  - Lista visible = `combine(collectors, query)`, filtrando por nombre o correo sin distinguir mayúsculas ni tildes (`Normalizer`).
- `CollectorRow`: avatar circular con iniciales (por ejemplo "MB"; si no hay nombre, "··"), nombre en serif bold, correo y `RadioButton`. Seleccionada: fondo durazno y radio terracota. Accesibilidad con `Modifier.selectable(role = Role.RadioButton)` dentro de `Modifier.selectableGroup()`.
- Campo de búsqueda con ícono de lupa, placeholder "Buscar por nombre o correo" y solo línea inferior.
- Pie fijo: texto informativo (ver decisiones) y botón ancho terracota "Continuar como {nombre}", **deshabilitado** sin selección. Al tocarlo: `selectCollector()` → Álbumes.
- Estados: carga, error por tipo con reintento (`StateViews`), lista vacía y "sin resultados" para la búsqueda.
- testTags: `collector_picker`, `collector_search`, `collector_row_{id}`, `collector_continue`.

### Tema y recursos
- `Color.kt`, tokens del diseño (aproximados del mockup, ajustar si hay especificación):
  - `Terracotta ≈ #C2410C`
  - `RecordGreen ≈ #2E4A3F`
  - `Ink ≈ #1C1917`
  - `SelectionPeach ≈ #F6DCCB`
  - `Divider ≈ #D9D3C7`
- `Type.kt`: estilo monoespaciado para rótulos (`FontFamily.Monospace`, o agregar IBM Plex Mono a `res/font`).
- Todos los textos en `res/values/strings.xml` (sin textos fijos en el código).

## Fase E — Pruebas

- **Unitarias (`test/`):**
  - `CollectorPickerViewModelTest`: carga, error, filtro por nombre, filtro por correo, filtro sin tildes, selección y botón habilitado.
  - `RoleSelectionViewModelTest`.
  - `InMemorySessionRepositoryTest`.
- **Compose (`androidTest/ui/onboarding/`):** contenido de las dos pantallas, radio seleccionado, texto dinámico del botón y botón deshabilitado sin selección.
- **E2E nuevas (`androidTest/e2e/`):**
  - `OnboardingVisitorE2ETest`: Visitante → `album_list`.
  - `OnboardingCollectorE2ETest`: Coleccionista → buscar → seleccionar → Continuar → `album_list`, y atrás desde "¿Quién eres?".
  - `OnboardingCollectorErrorE2ETest`: error en `GET /collectors` y reintento.
  - Agregar coleccionistas a `FakeBackendData.kt`.
- **E2E existentes (HU01–HU03):** como la app siempre arranca en el onboarding, agregar a `E2EMatchers.kt` un helper `enterAsVisitor()` que espere `role_visitor` y lo toque. Llamarlo al inicio de cada prueba, antes de esperar `album_list`.
- **Kraken:** agregar el paso "tocar `role_visitor`" al inicio de los escenarios actuales, y crear escenarios para los dos flujos nuevos.

## Fase F — Documentación y proceso

- `docs/App-technical-documentation.md`: agregarlo como habilitador transversal (no corresponde a HU01–HU08), con clases, rutas y endpoint (`GET /collectors`).
- `docs/E2E-guia-rapida.md`: mencionar `enterAsVisitor()`.
- PR a `develop` revisado por otro miembro, con squash and merge.

## Fase 2 (fuera de este alcance)

- Ícono de perfil en la barra superior (arriba a la derecha) de las pantallas principales: muestra el rol actual y permite `clear()` + volver a `RoleSelectionRoute` limpiando la pila.
- Usar `UserSession.Collector` para comentarios y favoritos.

## Criterios de aceptación

- [ ] Al abrir la app (proceso nuevo) siempre aparece "¿Cómo quieres ingresar?" después del splash.
- [ ] Visitante lleva a Álbumes, y atrás sale de la app.
- [ ] Coleccionista muestra la lista real de `GET /collectors`, con búsqueda por nombre o correo.
- [ ] El botón solo se habilita con un coleccionista seleccionado y dice "Continuar como {nombre}".
- [ ] La sesión se mantiene mientras la app está viva y se pierde al cerrar el proceso.
- [ ] `./gradlew test`, `./gradlew connectedAndroidTest` y `./gradlew lint` en verde, y escenarios Kraken actualizados.
