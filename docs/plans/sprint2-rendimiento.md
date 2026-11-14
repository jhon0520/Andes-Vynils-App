# Plan: rendimiento del sprint 2 (micro-optimizaciones, ANR, memoria y profiling)

> Estado: **implementado** el 2026-11-14 en `feature/sp2-rendimiento` (desde `develop` `c691e76`, sin commit). Una primera versión en `feature/sp2_w6` chocó con el trabajo del equipo en `develop`, que ya tenía `RepositoryCache` y los dispatchers; se conservó lo de `develop` y se agregó lo que faltaba (§6). Pendiente del equipo: profiling en 3 teléfonos (fases 0 y 4), wiki y release (fase 5).
> Ejecutar con la skill `habiilidad-tecnica` (sección *Rendimiento*); el profiling y su reporte con `vinilos-plan-de-pruebas`; la autoevaluación final con `vinilos-entregas-sprint`.

## 1. Qué pide la entrega y cuánto vale

| Requisito del enunciado | Dónde se califica (rúbrica sprint 2, `docs/contexto-curso-modulos-1-6.md`) |
|---|---|
| Micro-optimizaciones con Lint de Android u otra herramienta (SonarQube) | Diseño y construcción: micro-optimizaciones (5) · Documentación: detalle de las micro-optimizaciones (3) |
| Hilos/corrutinas para evitar ANR | Diseño y construcción: evitar ANR (10) |
| Reducir el consumo de memoria | Diseño y construcción: buenas prácticas de memoria (10) |
| Perfilar las HU en 3 dispositivos **físicos** distintos | Documentación: reporte de profiling (3) · Calidad: estrategia con análisis de desempeño y profiling en el repo |

Cada cambio debe quedar **medido (antes/después)** y **documentado en la wiki**; el código solo, sin evidencia, no suma los puntos de documentación.

## 2. Diagnóstico del estado actual

Lo que ya está bien (se documenta como práctica existente, no hay que rehacerlo):

| Práctica | Evidencia en el código |
|---|---|
| Lint estricto y limpieza de recursos | `app/build.gradle.kts` → bloque `lint` (`checkAllWarnings`, `UnusedResources`, `Overdraw`… como error); commit `d5e97e7` |
| R8 + `shrinkResources` en release | `app/build.gradle.kts` → `buildTypes.release`; commit `f9d018e` |
| Red fuera del hilo principal | Retrofit con funciones `suspend` (OkHttp ejecuta la llamada en su propio pool) + `viewModelScope.launch` en todos los ViewModels |
| Llamadas en paralelo | `ArtistRepositoryImpl.getArtists()` pide músicos y bandas con `async` |
| Recolección consciente del ciclo de vida | `collectAsStateWithLifecycle()` en todas las Screens; `stateIn(WhileSubscribed(5_000))` |
| Listas eficientes | `LazyColumn`/`LazyGrid` con `key` estable en HU01, HU03, HU05 y el onboarding |
| Estabilidad para Compose | `@Immutable` en `AlbumCatalog`, `ArtistCatalog`, estado del picker |
| Filtros sin red | Búsqueda y filtros de HU01/HU03 sobre la lista en memoria |
| `ImageRequest` recordado | `AlbumRecord.kt` usa `remember(album.cover)` |

Brechas detectadas (son el trabajo de esta entrega):

| # | Brecha | Riesgo | Archivo |
|---|---|---|---|
| G1 | No hay `Dispatcher` inyectado: el mapeo DTO→dominio y el cruce de datos (`GetAlbumDetailUseCase`, `GetArtistsUseCase`) corren en `Dispatchers.Main` | Jank con listas grandes; el curso pide explícitamente `withContext(Dispatchers.IO)` y que sea testeable | `data/repository/*RepositoryImpl.kt`, `di/` |
| G2 | El filtro de HU01 normaliza (`Normalizer` + regex) cada nombre en **cada pulsación** y en el hilo principal | Frames lentos al escribir en la búsqueda | `ui/album/list/AlbumListViewModel.kt` (`combine` sin `flowOn`, `matches()`) |
| G3 | Sin caché en repositorios: HU02 vuelve a pedir `GET /collectors` en cada detalle; volver a una pestaña repite `GET /albums`, `/musicians`, `/bands` | Tráfico y memoria transitoria repetidos; más tiempo de carga (Render tiene *cold start*) | `AlbumRepositoryImpl`, `ArtistRepositoryImpl`, `CollectorRepositoryImpl` |
| G4 | Coil usa el `ImageLoader` por defecto: sin límites explícitos de caché ni tamaño de decodificación en HU03 | Bitmaps decodificados más grandes de lo que se pintan (56 dp) | `WineMusicApp.kt`, `ui/artist/ArtistListScreen.kt` |
| G5 | No se reacciona a presión de memoria (`onTrimMemory`) | Se pierde la práctica que menciona el módulo 6 | `WineMusicApp.kt` |
| G6 | `HttpLoggingInterceptor` en nivel `BODY` en debug: copia cada respuesta completa en memoria | Infla las mediciones si se perfila el build debug | `di/NetworkModule.kt` |
| G7 | No hay build "perfilable" con R8: perfilar el debug de Compose exagera CPU y jank (Compose en debug no está optimizado) | Números del reporte poco representativos | `app/build.gradle.kts` |
| G8 | Solo hay SonarQube como opción mencionada; no está configurado | Opcional: suma evidencia pero no es obligatorio | — |

> **Alerta de alcance:** el hito del sprint 2 son **≥ 5 HU**. Según `docs/App-technical-documentation.md` hoy están HU01, HU02, HU03 y HU05 (+ onboarding, que no es HU). El profiling debe cubrir las 5 HU entregadas, así que la quinta HU (HU04 o HU06) tiene que estar lista **antes** de la fase de medición final.

## 3. Plan de ejecución

Una rama por frente desde `develop`, PR revisado y *squash and merge* (`docs/gitflow.md`). Cada tarea lleva su prueba unitaria cuando cambia lógica.

### Fase 0 — Línea base (antes de tocar código)
Rama: ninguna (solo medir).
1. Crear el build type `profiling` (ver G7 y §5.2) en una rama corta `feature/profiling-build` y mergearlo primero: sin él la línea base no es comparable.
2. Medir HU01, HU02, HU03, HU05 (y la quinta HU si existe) en **un** dispositivo de referencia con el protocolo de §5.3. Guardar capturas y CSV en `docs/performance/baseline/`.
3. Correr `./gradlew lint` y guardar `app/build/reports/lint-results-debug.html` como evidencia del "antes".

### Fase 1 — Micro-optimizaciones (`feature/micro-optimizations-sp2`)
| Tarea | Detalle | Evidencia |
|---|---|---|
| M1 | `./gradlew lint` en verde; revisar también los avisos de *Performance* y *Compose* (no solo errores) | Reporte HTML antes/después |
| M2 | Precalcular el texto normalizado de búsqueda una vez por álbum (al cargar) en lugar de por pulsación; `DIACRITICS` ya es constante | Tiempo de filtrado en el CPU profiler |
| M3 | Sustituir `HashMap`/`mapOf` con claves `Int` por `SparseArray`/`ArrayMap` donde aparezcan (p. ej. índice de coleccionistas por id en `GetAlbumDetailUseCase`) | Diff + explicación |
| M4 | `const val` para tags y constantes; revisar `remember` en cálculos de `Canvas` de `AlbumRecord` | Diff |
| M5 (opcional) | SonarQube/SonarCloud con el plugin `org.sonarqube` (Sonar Cloud es gratis para repos públicos) y corregir *code smells* de severidad alta | Captura del panel antes/después |

### Fase 2 — ANR y corrutinas (`feature/anr-dispatchers`)
| Tarea | Detalle |
|---|---|
| A1 | Calificador `@IoDispatcher` (y `@DefaultDispatcher`) en un `DispatchersModule` de Hilt |
| A2 | Repositorios: `withContext(ioDispatcher) { safeApiCall { … } }`; mapeo y cruces pesados en `defaultDispatcher` |
| A3 | `AlbumListViewModel`/`ArtistListViewModel`: `combine(...).flowOn(defaultDispatcher)` para que filtrar no ocupe el hilo principal |
| A4 | Pruebas unitarias con `StandardTestDispatcher` inyectado (el motivo de inyectar el dispatcher) |
| A5 | `StrictMode` (thread + VM policy, `penaltyLog`) solo en debug, dentro de `WineMusicApp`, para detectar disco/red en el hilo principal en las corridas manuales (las E2E usan `HiltTestApplication`, que no lo activa) |
| A6 | Verificación: 0 ANR en `adb shell dumpsys activity anr`; 0 avisos de StrictMode en logcat tras correr `connectedAndroidTest` |

### Fase 3 — Memoria y caché (`feature/memory-cache`)
| Tarea | Detalle |
|---|---|
| R1 | Caché en memoria en los repositorios (`LruCache` para detalle por id, lista completa). La decisión red vs caché vive en el repositorio, nunca en la UI. *(Implementado por el equipo como `RepositoryCache`; ver §6.)* |
| R2 | HU02 reutiliza la lista de coleccionistas cacheada en lugar de pedir `GET /collectors` por cada detalle |
| R3 | `ImageLoader` único en `WineMusicApp` (`ImageLoaderFactory`): `memoryCache` al 15–20 % de la memoria disponible, `diskCache` acotada (p. ej. 50 MB), `crossfade` |
| R4 | `size(...)` explícito en los `ImageRequest` de avatares (HU03) y portadas para no decodificar a resolución completa |
| R5 | `onTrimMemory`/`ComponentCallbacks2` en `WineMusicApp`: vaciar caché de imágenes y repositorios en `TRIM_MEMORY_UI_HIDDEN`/`RUNNING_LOW` |
| R6 | `HttpLoggingInterceptor` en `HEADERS` (o `NONE`) en el build `profiling` |
| R7 | LeakCanary solo en `debugImplementation` para verificar 0 fugas al navegar entre pestañas y detalle (se retira o se deja en debug; no entra al APK release) |

### Fase 4 — Profiling en 3 dispositivos físicos
Ver §5. Medir con el **mismo** build `profiling`, la misma versión del backend y los mismos datos.

### Fase 5 — Documentación y entrega
1. Wiki, sección sprint 2: página *Micro-optimizaciones* (tabla: problema → cambio → herramienta → antes/después → commit/PR) y página *Profiling* (§5.4).
2. Actualizar la estrategia de pruebas con el análisis de desempeño (skill `vinilos-plan-de-pruebas`).
3. Registrar como issue (`bug`/`performance`) cada hallazgo que no se corrija.
4. Release etiquetado en `main` con APK en `distributions/`, probado en emulador API 21.

### Orden y dependencias

```
Fase 0 (build profiling + línea base) ─┬─ Fase 1 micro-opt ─┐
                                       ├─ Fase 2 ANR ───────┼─ Fase 4 profiling (3 físicos) ─ Fase 5 wiki + release
                                       └─ Fase 3 memoria ───┘
```
Las fases 1–3 son independientes entre sí y se pueden repartir entre miembros del equipo; la fase 4 se ejecuta cuando las tres estén en `develop` (y la quinta HU también).

## 4. Criterios de terminado
- `./gradlew lint`, `./gradlew test` y `./gradlew connectedAndroidTest` en verde; Kraken en verde.
- 0 ANR y 0 violaciones de StrictMode en las E2E; 0 fugas reportadas por LeakCanary en los recorridos de las HU.
- Volver a una pestaña ya visitada **no** genera peticiones de red (verificado en el Network Inspector).
- Tabla de profiling completa: 5 HU × 3 dispositivos físicos, con antes/después en al menos el dispositivo de referencia.
- Wiki actualizada y enlazada en `_Sidebar` del sprint 2.

## 5. Recomendación sobre los dispositivos para las pruebas de rendimiento

### 5.1 Evaluación: `docker-android` vs. emuladores locales vs. dispositivos físicos

Se evaluó [HQarroum/docker-android](https://github.com/HQarroum/docker-android) (emulador Android *headless* dentro de un contenedor, controlado por ADB) frente a los AVD de Android Studio en la máquina del equipo (MacBook Pro Intel i7‑8850H, 16 GB RAM, Hypervisor.framework disponible; AVD ya creados: `Pixel_API_26`, `Pixel_8_API_28`, `Pixel_3a_API_30_x86`).

| Criterio | docker-android | AVD locales (Android Studio) | Dispositivos físicos |
|---|---|---|---|
| **Cumple la rúbrica** ("3 dispositivos físicos") | ❌ Es un emulador | ❌ Es un emulador | ✅ |
| Aceleración en esta máquina | ❌ Necesita **KVM** (`/dev/kvm`), que solo existe en hosts Linux. Docker Desktop en macOS corre en una VM sin virtualización anidada, así que el emulador caería en emulación por software (decenas de veces más lento) o no arrancaría | ✅ Aceleración nativa con Hypervisor.framework | ✅ Hardware real |
| Representatividad de CPU/memoria/jank | Baja: el contenedor añade una capa y comparte CPU con Docker | Media: mide el comportamiento de la app, pero sobre la CPU del Mac | Alta: CPU, GPU, RAM, térmica y fabricante reales |
| Integración con Android Studio Profiler | Posible por `adb connect` a un puerto TCP; más pasos y latencia | Directa | Directa por USB/Wi‑Fi |
| Costo de preparación | Alto: imagen de varios GB, configurar puertos, ADB remoto, sin GUI (headless) para observar la app | Bajo: ya existen 3 AVD | Bajo si el equipo aporta sus teléfonos |
| Diversidad de versiones | Una imagen por API; se pueden levantar varias | Igual, y con GUI | Depende de los teléfonos del equipo |
| Punto fuerte real | CI en Linux con KVM (p. ej. GitHub Actions con runners Linux) y entornos reproducibles | Validación rápida, API 21 obligatoria, pruebas E2E | Evidencia de profiling que exige el curso |

**Conclusión:** `docker-android` **no es viable** para esta entrega:
1. No cumple el requisito: el enunciado pide dispositivos **físicos**; ningún emulador (en Docker o local) cuenta para el reporte de profiling.
2. En macOS no hay KVM, así que el contenedor no tendría aceleración y las mediciones serían inútiles incluso como complemento.
3. Su ventaja (reproducibilidad en CI) no aporta a esta tarea; tendría sentido solo si más adelante se corren las E2E en un pipeline Linux.

Entre las dos opciones de emulación, los **AVD locales son claramente mejores** y ya están creados, pero sirven como **complemento**, no como sustituto.

### 5.2 Recomendación

1. **Profiling oficial: 3 teléfonos físicos del equipo**, elegidos para cubrir gama y versión:

   | Rol | Perfil sugerido | Por qué |
   |---|---|---|
   | Gama baja / Android antiguo | ≤ 3 GB RAM, Android 8–10 | Donde aparecen jank, presión de memoria y riesgo de ANR |
   | Gama media | 4–6 GB RAM, Android 11–13 | Usuario típico |
   | Gama alta / Android reciente | ≥ 8 GB RAM, Android 14–15 | Referencia de mejor caso y comportamiento con `targetSdk 35` |

   Idealmente de fabricantes distintos (Samsung, Xiaomi/Motorola, Pixel), porque las capas de personalización cambian memoria disponible y gestión de procesos. Registrar en el reporte: modelo, fabricante, versión de Android/API, RAM, CPU.

2. **Si el equipo no reúne 3 teléfonos distintos**, alternativas con hardware real (en este orden):
   - **Firebase Test Lab** (plan gratuito Spark, cupo diario limitado de pruebas en dispositivos físicos): correr un *Robo test* o las pruebas instrumentadas sobre teléfonos reales; el reporte incluye gráficas de CPU, memoria, red y FPS por dispositivo. No se conecta al Profiler de Android Studio, así que sus métricas se presentan como fuente distinta.
   - **Android Device Streaming** de Android Studio (dispositivos reales remotos de Google con cuota gratuita): permite instalar y depurar; validar con el equipo si el Profiler se conecta antes de depender de ello.
   - Pedir prestado un teléfono de gama baja: aporta más al análisis que dos de gama alta.

3. **Emuladores locales como complemento** (no van en la tabla "física"):
   - Crear un AVD **API 21** (obligatorio por `CLAUDE.md`: cada release se prueba en API 21) y usar los existentes (API 26, 28, 30) para la línea base rápida, depurar regresiones y correr las E2E.
   - Correr un emulador a la vez: con 16 GB de RAM, varios en paralelo distorsionan las métricas.

4. **Backend estable durante la medición:** el backend de Render tiene *cold start*, que mete segundos de latencia aleatoria en "tiempo de carga". Para medir, usar el backend local (`docker-compose` de BackVynils) y en físico `adb reverse tcp:3000 tcp:3000` + `api.base.url=http://localhost:3000/` en `local.properties`; o, si se usa Render, hacer una petición de calentamiento antes de cada serie. Anotar en el reporte cuál se usó.

### 5.3 Protocolo de medición (igual en los 3 dispositivos)

**Build:** tipo `profiling` = `initWith(release)` (R8 activo), firmado con la clave debug, `isDebuggable = false`, `<profileable android:shell="true"/>` en `src/profiling/AndroidManifest.xml` (Android 10+; en Android < 10 el Profiler necesita un build debuggable: anotarlo como limitación) y logging HTTP reducido.

**Preparación:** batería > 50 %, modo avión con Wi‑Fi encendido, sin otras apps abiertas, animaciones del sistema activas (son parte de la experiencia), app desinstalada y reinstalada antes de cada serie, 3 repeticiones por medida (se reporta la mediana).

**Recorrido por HU:**

| HU | Recorrido medido |
|---|---|
| HU01 | Arranque en frío → Visitante → catálogo cargado → *scroll* completo → escribir "a" y borrar en la búsqueda → filtrar por género |
| HU02 | Desde el catálogo abrir 3 álbumes distintos, volver entre cada uno |
| HU03 | Pestaña Artistas → *scroll* → alternar filtro músicos/bandas → salir y volver (debe salir de caché) |
| HU05 | Pestaña Coleccionistas → *scroll* → salir y volver |
| 5.ª HU | Según la HU entregada (HU04 o HU06): abrir 3 detalles |

**Métricas y herramienta:**

| Métrica | Herramienta / comando |
|---|---|
| Tiempo de arranque (TTID) | `adb shell am start -W -S -n com.dev.wine.music.app/.MainActivity` (`TotalTime`) |
| Memoria (PSS total, Java heap, nativo, gráficos) pico y estable | Android Studio Profiler → Memory; `adb shell dumpsys meminfo com.dev.wine.music.app` |
| CPU (% medio y pico durante el recorrido) | Profiler → CPU (*System Trace* para ver el hilo principal) |
| Jank (% frames lentos, percentil 90/95 de duración de frame) | `adb shell dumpsys gfxinfo com.dev.wine.music.app reset` antes y `dumpsys gfxinfo …` después del recorrido |
| Red (número de peticiones y KB por HU; segunda visita = 0) | Profiler → Network Inspector |
| ANR | Debe ser 0; logcat + `adb shell dumpsys activity anr` |
| Energía (cualitativo) | Profiler → Energy (Android 8+) |

Sugerencia: un script `scripts/perf-capture.sh <etiqueta-dispositivo>` que ejecute `am start -W`, `gfxinfo` y `meminfo` y guarde la salida en `docs/performance/<dispositivo>/`, para que los tres miembros midan igual.

### 5.4 Formato del reporte (wiki, sprint 2 → *Profiling*)
1. Tabla de dispositivos (modelo, API, RAM, CPU, quién lo midió, fecha, backend usado).
2. Por HU, tabla dispositivo × métrica (arranque, PSS pico/estable, CPU medio, % jank, peticiones) + capturas del Profiler.
3. Comparación antes/después (línea base de la fase 0 vs. final) en el dispositivo de referencia.
4. Conclusión por HU y hallazgos convertidos en issues.
5. Limitaciones (p. ej. dispositivo < Android 10 medido con build debuggable).

## 6. Estado de ejecución (2026-11-14)

La base de rendimiento ya estaba en `develop` (PR del equipo): `@IoDispatcher`/`@DefaultDispatcher` en `domain/coroutines`, `RepositoryCache` con `invalidateAlbums()`, `fastMap`/`fastForEach`, `SparseArrayCompat`, orden con `CASE_INSENSITIVE_ORDER`, Coil con RGB_565 y `onTrimMemory`. Sobre esa base, esta rama agrega lo de la tabla; el detalle y la evidencia están en `docs/performance/micro-optimizaciones.md`.

| Tarea | Estado | Nota |
|---|---|---|
| Build `profiling` (G7) | ✅ | `./gradlew installProfiling`; APK de 2,6 MB con R8 |
| A1–A2 dispatchers en repositorios y casos de uso | ✅ `develop` | Se conservó la versión del equipo |
| A3 `flowOn` en los filtros | ✅ | HU01, HU03, selector de coleccionista; prueba con `RecordingDispatcher` |
| A5–A6 StrictMode | ✅ | 0 violaciones en HU01 y HU04 (emulador API 30) |
| M1 lint | ✅ | 0 errores; solo avisos de versión de dependencias |
| M2 búsqueda precalculada | ✅ | `AlbumListViewModel.SearchableAlbum` |
| M3 `SparseArrayCompat` | ✅ `develop` | |
| M5 SonarQube | ⏭️ No hecho | Opcional; requiere cuenta del equipo en SonarCloud |
| R1–R3 caché en repositorios | ✅ `develop` + HU04 | `RepositoryCache` del equipo; esta rama agrega detalle de artista, premios e `invalidateArtists()`. No se agregó `forceRefresh`: los errores no se guardan, así que «Reintentar» ya vuelve a la red |
| R4 Coil | ✅ `develop` | |
| R5 `onTrimMemory` | ✅ | Se agregó `RUNNING_CRITICAL`; verificado con `am send-trim-memory` |
| R6 logging HTTP | ✅ | `NONE` en `profiling` |
| R7 LeakCanary | ⏭️ Descartado | El APK entregado es el debug; se usa el *heap dump* del Profiler |
| Script de captura | ✅ | `scripts/perf-capture.sh`, guía en `docs/performance/README.md` |
| Pruebas | ✅ | 115 unitarias y 84 instrumentadas en verde (emulador API 30, backend local). Kraken no se corrió |
| Fase 4 profiling en 3 físicos | ⏳ Equipo | Seguir `docs/performance/README.md`; para el «antes», instalar `develop` (`c691e76`) |
| Fase 5 wiki + release | ⏳ Equipo | Agregar `docs/performance/micro-optimizaciones.md` a la página SP02 · Micro-optimizaciones |
