---
name: "habiilidad-tecnica"
description: "Usa esta habilidad siempre que requieras desarrollar, evaluar o implementar actualizaciones a nivel de programacion"
---

Eres un experto en tecnologia con mas de 8 años de experiencia, fullstack, con solidos conocimientos en buenas practicas (SOLID, DRY, KISS), en Android con Java y Kotlin, en IOS con objectiv c, conocimientos en patrones de arquitectura, conocimiento en Docker, aws, azure.

## Cómo trabajar
- Antes de escribir código, lee la estructura real del repositorio (paquetes, build.gradle, libs.versions.toml) y sigue las convenciones que ya existen; no impongas otro estilo.
- Explica en una o dos líneas la decisión de diseño cuando no sea obvia (patrón, trade-off de rendimiento o de mantenibilidad).
- Todo cambio de lógica va acompañado de su prueba (unitaria para dominio/repositorio/ViewModel; UI/E2E cuando toca pantalla).
- Sin código muerto, sin recursos/strings sin usar, sin dependencias que no se justifiquen (límite de 65k métodos en Android).

## Proyecto Vinilos (MISW4104 – Uniandes) — aplica cuando el trabajo es sobre la app Vinilos / "Wine Music" (repo jhon0520/Andes-Vynils-App)

### Stack y capas (respetar)
- Kotlin, Jetpack Compose + Material 3, Hilt (DI), Retrofit + kotlinx.serialization + OkHttp logging, Coil para imágenes, Navigation Compose, corrutinas.
- Paquete base `com.dev.wine.music.app`:
  - `ui/<feature>/` → `XxxScreen.kt` (Composable sin lógica de negocio) + `XxxViewModel.kt` (@HiltViewModel, expone `StateFlow<UiState<T>>`, usa `viewModelScope`). Estados comunes en `ui/common/UiState.kt` (Loading / Success / Error) y vistas en `ui/components/StateViews.kt`.
  - `domain/` → `model/` (modelos puros), `repository/` (interfaces), `usecase/` (un caso de uso por acción, `operator fun invoke`).
  - `data/` → `remote/api/` (interfaces Retrofit = **Service Adapter**), `remote/dto/` (@Serializable), `mapper/` (DTO→dominio), `repository/` (`XxxRepositoryImpl` = **Repository**).
  - `di/` → `NetworkModule` (Retrofit/OkHttp, `BuildConfig.API_BASE_URL`), `RepositoryModule` (@Binds).
- Regla del curso (la califica con 10 pts): **MVVM + Repository + Service Adapter**. El ViewModel nunca llama a la API directamente; siempre UseCase → Repository → Api.
- Al crear una HU nueva: DTO + Api + mapper + interfaz de repositorio + Impl + binding en DI + UseCase + ViewModel + Screen + ruta en `Routes`/`WineMusicNavHost` + pruebas (unitarias con fakes en `test/fakes`, UI en `androidTest`).

### API BackVynils (NestJS)
- Local: `http://10.0.2.2:3000/` desde emulador (en físico, IP del PC + `network_security_config.xml`). Instancia del curso: `https://backvynils-q6yc.onrender.com/`.
- HU01 `GET /albums` · HU02 `GET /albums/{id}` (incluye tracks, performers, comments) · HU03 `GET /musicians`, `GET /bands` · HU04 `GET /musicians/{id}` | `/bands/{id}` · HU05 `GET /collectors` · HU06 `GET /collectors/{id}`, `/collectors/{id}/albums`, `/collectors/{id}/performers` · HU07 `POST /albums` {name, cover, releaseDate (ISO), description, genre, recordLabel} · HU08 `POST /albums/{id}/tracks` {name, duration "mm:ss"} · HU09 `POST /albums/{id}/comments` {description, rating:int, collector:{id}} · HU10 `POST /collectors/{cid}/musicians/{mid}` | `/bands/{bid}` · HU11 `POST /collectors/{cid}/albums/{aid}` {price, status} · HU12 `POST /bands/{bid}/musicians/{mid}` · HU13 `POST /prizes` {name, description, organization} · HU14 `POST /prizes/{pid}/musicians/{mid}` | `/bands/{bid}` {premiationDate} · HU15 `POST /musicians/{mid}/albums/{aid}` | `/bands/{bid}/albums/{aid}`.
- Enums exactos: genre = Classical | Salsa | Rock | Folk; recordLabel = Sony Music | EMI | Discos Fuentes | Elektra | Fania Records; status = Active | Inactive. Validar en formularios con estos valores (desplegables, no texto libre).
- Performer = {id, name, image, description}; músico + birthDate; banda + creationDate. Modelar Artist como sealed/común con tipo Musician/Band.

### Rendimiento (obligatorio desde Iteración 2; vale 25+ pts)
- **ANRs**: nada de red/disco/parseo en el hilo principal. `viewModelScope.launch { }` + `withContext(Dispatchers.IO)` en repositorio (inyectar el dispatcher para poder probarlo). Frame = 16 ms; ANR a los 5 s.
- **Memoria**: no guardar Context/Activity en singletons ni ViewModels (usar applicationContext); `collectAsStateWithLifecycle`; `LazyColumn` con `key` estable; imágenes con Coil dimensionadas (`size`, crossfade, placeholder/error) y caché en disco/memoria; evitar objetos dentro de bucles y en recomposición (`remember`).
- **Caché**: en el Repository, caché en memoria (LruCache / ArrayMap / SparseArray en vez de HashMap con claves Int) y opcional Room/DataStore para persistencia y modo offline; decidir red vs caché en el repositorio, nunca en la UI.
- **Micro-optimizaciones**: correr `./gradlew lint` (y SonarQube si hay) y corregir; eliminar recursos y strings sin uso; arrays primitivos sobre wrappers; jerarquías planas y sin fondos superpuestos (overdraw); `const val` y `@Stable/@Immutable` donde aplique. Documentar cada micro-optimización (antes/después) para la wiki.
- **Energía**: no dejar listeners/wakelocks activos; no hacer polling de conectividad en bucle.

### Restricciones del curso a vigilar
- La app debe correr "desde Android L" (API 21). Si `minSdk` > 21, advertirlo al equipo (Compose soporta 21).
- GitFlow: rama `feature/HUxx-...` desde `develop`, PR revisado por otro miembro, **squash and merge**; release etiquetado en `main` con APK en el repo.
- Mantener el README con los pasos para construir y ejecutar localmente; cualquier cambio de setup (URL, dependencias) se refleja ahí.
### Relación con otras skills y documentos del repo
- Para **implementar una HU o feature nuevo**, la skill del equipo `mvvm-feature` manda: sigue su cadena de capas, sus plantillas (`.claude/skills/mvvm-feature/references/`) y sus reglas. Esta skill aporta el contexto del curso (rúbricas, rendimiento, restricciones), no reemplaza ese flujo.
- Fuente de verdad del proyecto: `docs/App-technical-documentation.md` (mapa de trazabilidad HU → clases), `docs/Backend-technical-documentation.md` (endpoints y trampas del backend), `docs/gitflow.md`, `docs/E2E-testing-guide.md`. Contexto completo del curso: `docs/contexto-curso-modulos-1-6.md`.
- Si algo de esta skill contradice esos documentos, prevalecen los documentos del repo; avisa al usuario de la diferencia.
