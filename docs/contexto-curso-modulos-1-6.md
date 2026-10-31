# Contexto del curso — Ingeniería de Software para Aplicaciones Móviles (MISO, módulos 1–6)

Fuente: Coursera `ingenieria-de-software-para-aplicaciones-moviles` (Uniandes, prof. Mario Linares-Vásquez y Camilo Escobar-Velásquez), leído el 31-oct-2026. Incluye los codelabs de TheSoftwareDesignLab, el libro *Mobile App Development* (uniandes-se4ma) y el repositorio del backend BackVynils.

## 1. Estructura y evaluación

| Mód. | Semana | Tema | Proyecto |
|---|---|---|---|
| 1 | — | Bienvenida (programa, uso de IA generativa, MAAD) | — |
| 2 | S1 | Ecosistema móvil, tipos de apps, sistemas de diseño (cap. 1 y 5 del libro, Google Stitch) | Inception W1: equipo, contrato, backlog, wireframe |
| 3 | S2 | Kotlin, componentes Android (activities, fragments, temas), Firebase Studio, Espresso Recorder | Inception W2: entrega final del backlog + prototipo navegable |
| 4 | S3 | Patrones de arquitectura (cap. 8), Retrofit/Volley, guía de arquitectura de Jetpack, MVVM, agente Gemini | Sprint 1 (avance) + retrospectiva del inception + Parcial 1 |
| 5 | S4 | Repository y Service Adapter, E2E con Espresso, ADB/Telnet, Kraken | Sprint 1 (entrega final, vence el 1-nov 23:59) |
| 6 | S5 | Pirámide de pruebas, rendimiento (cap. 11–12), corrutinas, caché, Firebase Test Lab | Sprint 2 (avance, vence el 8-nov 23:59) |
| 7–9 | S6–S8 | Sprint 2 (cierre), Sprint 3: accesibilidad/usabilidad y consolidación | — |

- Proyecto 60 %: Inception 5, Iteración 1 10, Iteración 2 20, Iteración 3 25. Exámenes 40 % (parcial S3 20, final S8 20).
- La coevaluación funciona como multiplicador de la nota del equipo (puntos propios ÷ puntos del mejor del equipo). Dimensiones: compromiso, herramientas, comunicación, solidaridad, ética y capacidad técnica.
- Las entregas cierran el domingo a las 23:59 GMT-5. Solo se revisan las versiones de la wiki y del repositorio actualizadas antes de esa hora.

## 2. Reglas del proyecto Vinilos (TSDC)

- App Android nativa en **Kotlin** que consume la API REST de Vinilos. Equipos de 4 personas, 8 semanas, un inception y 3 sprints de 2 semanas.
- **MVP = 8 HU** (HU01–HU08). Mínimo 5 HU al cierre del sprint 2 y 8 al cierre del sprint 3. Se sugiere repartir 2 + 3 + 3.
- **GitFlow**: rama de release (`main`), rama `develop` y ramas de funcionalidad. Se integra con PR revisado y **squash and merge**. Cada entrega queda etiquetada como **release** en `main`.
- En cada sprint se entrega: estrategia de pruebas, artefactos de prueba (inventario, scripts, defectos como issues), enlace al repo, **APK en el repo**, README para construir localmente y diseño de arquitectura (diagramas de **clases, paquetes y componentes**). Todo se documenta en la **wiki**, organizado por sprint.
- Tablero con las columnas TO-DO, Diseño, Implementación, Integración, Pruebas y Done; un milestone por sprint; issues asignados.
- Las pruebas se hacen en dispositivos **Android Lollipop (API 21) o superior**.
- Hay una reunión de equipo semanal (lunes) y se debe dejar evidencia (acta, bitácora o video).

## 3. Historias de usuario y endpoints

Backend: NestJS `TheSoftwareDesignLab/BackVynils` (en local `http://localhost:3000`, desde el emulador `10.0.2.2:3000`). Instancia compartida del curso: `https://backvynils-q6yc.onrender.com/` (el curso recomienda desplegar una propia).

| HU | Pantalla | Endpoint |
|---|---|---|
| HU01 | Catálogo de álbumes | `GET /albums` |
| HU02 | Detalle de álbum | `GET /albums/{id}` (+ `/tracks`, `/comments`) |
| HU03 | Listado de artistas | `GET /musicians`, `GET /bands` |
| HU04 | Detalle de artista | `GET /musicians/{id}`, `GET /bands/{id}` (+ `/albums`) |
| HU05 | Listado de coleccionistas | `GET /collectors` |
| HU06 | Detalle de coleccionista | `GET /collectors/{id}`, `/collectors/{id}/albums`, `/collectors/{id}/performers` |
| HU07 | Crear álbum | `POST /albums` {name, cover, releaseDate, description, genre, recordLabel} |
| HU08 | Asociar tracks | `POST /albums/{id}/tracks` {name, duration} |
| HU09 | Comentar álbum | `POST /albums/{id}/comments` {description, rating, collector:{id}} |
| HU10 | Artistas favoritos | `POST /collectors/{cid}/musicians/{mid}` · `/bands/{bid}` |
| HU11 | Álbum a coleccionista | `POST /collectors/{cid}/albums/{aid}` {price, status} |
| HU12 | Músicos a banda | `POST /bands/{bid}/musicians/{mid}` |
| HU13 | Crear premio | `POST /prizes` {name, description, organization} |
| HU14 | Premio a artista | `POST /prizes/{pid}/musicians/{mid}` · `/bands/{bid}` {premiationDate} |
| HU15 | Álbum a artista | `POST /musicians/{mid}/albums/{aid}` · `/bands/{bid}/albums/{aid}` |

Enums:
- `genre`: Classical, Salsa, Rock, Folk.
- `recordLabel`: Sony Music, EMI, Discos Fuentes, Elektra, Fania Records.
- `status` (collector-album): Active, Inactive.
- Los performers tienen {id, name, image, description}. Los músicos además tienen `birthDate` y las bandas `creationDate`.

## 4. Contenido técnico clave

**Arquitectura (sem. 3–4).** MVVM con Jetpack (ViewModel, LiveData/StateFlow, Navigation). El **Repository** media entre el ViewModel y las fuentes de datos, lo que permite añadir caché más adelante. El **Service Adapter** (singleton) encapsula la red con Volley o Retrofit y expone métodos como `getAlbums()`. El ViewModel nunca toca el adapter directamente. La rúbrica del sprint 1 asigna 10 puntos a "implementa MVVM, Repository y Service Adapter" y 10 a "consume la API REST".

**Pruebas (sem. 2, 4, 5).**
- Espresso: ViewMatchers, ViewActions con `perform()` y ViewAssertions con `check()`, `ActivityScenarioRule` y `AndroidJUnitRunner`. Hay que **desactivar las animaciones** con `adb shell settings put global window_animation_scale 0.0`, igual para `transition_animation_scale` y `animator_duration_scale`.
- Espresso Recorder sirve para grabar escenarios.
- Kraken (Gherkin + Appium, Node 18) se usa para E2E con interacción entre varios usuarios.
- ADB/Telnet sirven para pruebas manuales y automatizadas en emulador y dispositivo.
- Firebase Test Lab permite pruebas en la nube sobre muchos dispositivos (fragmentación).
- Pirámide de pruebas: muchas unitarias, menos de integración y pocas E2E.

**Rendimiento (sem. 5–6, Iteración 2).**
- *Hilo principal*: el frame dura 16 ms. Hay ANR si la UI no responde en 5 s o si un BroadcastReceiver tarda más de 10 s. Se resuelve con corrutinas: `viewModelScope.launch` + `withContext(Dispatchers.IO)`, y `suspendCoroutine` para envolver callbacks.
- *Memoria*: hay memory bloat con estructuras enormes, referencias fuertes o cíclicas (usar `WeakReference`), bitmaps sin optimizar u objetos declarados dentro de bucles. Puede terminar en OOM. Conviene usar `onTrimMemory` (ComponentCallbacks2) y consultar `ActivityManager`.
- *Micro-optimizaciones*: Lint/SonarQube, eliminar recursos y strings que no se usan, sacar declaraciones de los bucles, preferir arrays primitivos, ArrayMap/SparseArray en vez de HashMap, `baselineAligned="false"`, evitar overdraw (`background="@null"`, jerarquías planas).
- *Caché*: un CacheManager singleton en el repositorio (HashMap → ArrayMap/SparseArray/**LruCache**), SharedPreferences o Room para persistencia, y caché de imágenes con Glide (`DiskCacheStrategy.ALL`, placeholder y error). En Compose, el equivalente es Coil.
- *Energía*: bugs de no-sleep (wakelocks, listeners sin desregistrar), loop, immortality y uso de APIs costosas.

## 5. Rúbricas

**Sprint 1 (100).**
- Ágil 15: repo accesible, APK, release etiquetado, todos usan GitFlow, evidencia de reuniones S3–S4, retrospectiva del inception.
- Documentación 5: arquitectura y estrategia de pruebas en la wiki.
- Diseño y construcción 40: diagramas, coherencia entre diseño y código, MVVM + Repository + Service Adapter (10), consumo de la API (10), README (5).
- Calidad 40: estrategia clara, completa y con E2E; HU implementadas; sin errores desde Android L; cada HU con pruebas automatizadas; Espresso/Kraken en verde; defectos reportados como issues.

**Sprint 2 (100). Requiere 5 HU.**
- Ágil 12.
- Documentación 12: arquitectura, estrategia, reflexión sobre la estimación del S1, **burndown chart del S1 (3)**, **detalle de las micro-optimizaciones (3)**, **reporte de profiling (3)**.
- Diseño y construcción 48: diagramas de 5 HU, coherencia, patrones, micro-optimizaciones (5), **buenas prácticas de memoria (10)**, **evitar ANR (10)**, API (5), README (5).
- Calidad 28: estrategia con E2E de las HU de S1 y S2 y con análisis de desempeño; 5 HU implementadas; sin errores desde L; Espresso/Kraken en verde; resultados y profiling en el repo con los defectos como issues.
- Profiling en **al menos 3 dispositivos físicos**.

## 6. Estado del repositorio `jhon0520/Andes-Vynils-App` (Equipo 10, "Wine Music")

- Stack: Jetpack Compose + Material 3, Hilt, Retrofit + kotlinx.serialization, OkHttp logging y Coil. Capas `ui/` (Screen + ViewModel + `UiState`), `domain/` (modelos, interfaces de repositorio, casos de uso) y `data/` (api, dto, mapper, RepositoryImpl), más `di/`.
- Tiene pruebas unitarias con fakes y `MainDispatcherRule`, y una prueba de UI con Compose (`AlbumListContentTest`).
- Ramas: `main`, `develop`, `h0_base_architecture` y `hu0_update_readme`.
- **Riesgo**: `minSdk = 24`, pero el curso prueba "desde Android L (API 21)". Compose admite minSdk 21.
- **Riesgo**: la rúbrica habla de "Espresso/Kraken". La API de pruebas de Compose corre sobre Espresso, pero conviene confirmar con los tutores que la aceptan.
