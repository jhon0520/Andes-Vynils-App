# Micro-optimizaciones, ANR y memoria (sprint 2) · rama `feature/sp2-rendimiento`

Qué agrega esta rama a la base de rendimiento que ya estaba en `develop`, con la herramienta usada y la evidencia. Es la fuente de la parte **E** de la página de la wiki *SP02 · Micro-optimizaciones*. Plan y estado: `docs/plans/sprint2-rendimiento.md` §6. Cómo se mide en los teléfonos: `docs/performance/README.md`.


## Resumen de evidencia

| Evidencia | Resultado |
|---|---|
| `./gradlew lint` | 0 errores; solo avisos de versión de dependencias (`GradleDependency`, `NewerVersionAvailable`, `AndroidGradlePluginVersion`) y `MonochromeLauncherIcon` |
| Pruebas | 115 unitarias y 84 instrumentadas en verde (emulador API 30, backend local) |
| APK que se perfila | 2,6 MB (`app-profiling.apk`, R8 + `shrinkResources`), frente a 12,1 MB del `app-debug.apk` |
| Reabrir el mismo artista (HU04) | 1 × `/musicians/{id}` o `/bands/{id}` y 1 × `/performerprizes` en la sesión; la segunda apertura no pide nada (pruebas de repositorio con llamadas contadas) |
| Reabrir un álbum tras `adb shell am send-trim-memory com.dev.wine.music.app RUNNING_CRITICAL` | Vuelve a pedir `/albums/{id}` y `/collectors`, sin errores: la caché se vació |
| Violaciones de StrictMode (disco/red en el hilo principal) en HU01 y HU04 | 0 |
| ANR en los recorridos | 0 |

## Cambios de esta rama

| # | Problema | Cambio | Archivos | Herramienta / verificación |
|---|---|---|---|---|
| E1 | Filtrar por búsqueda, género o tipo corría en el hilo principal en cada tecla o cambio de filtro: `combine` se ejecuta en el contexto de quien recolecta (`viewModelScope`, es decir, `main`) | `combine(...).flowOn(defaultDispatcher)` antes de `stateIn`; la pantalla sigue recolectando en `main` | `AlbumListViewModel`, `ArtistListViewModel`, `CollectorPickerViewModel` | `AlbumListViewModelTest` comprueba con `RecordingDispatcher` que el filtro pasa por el dispatcher Default; *System Trace* en el profiling |
| E2 | La búsqueda de HU01 normalizaba (Unicode NFD + regex) el nombre de cada álbum y de cada intérprete **en cada tecla** | `SearchableAlbum`: el texto normalizado se calcula una vez por álbum al cargar el catálogo (en `defaultDispatcher`); en cada tecla solo se normaliza la consulta y se hace `contains` | `AlbumListViewModel.kt` | Mismas pruebas de búsqueda en verde («ruben» encuentra «Rubén»); CPU del recorrido HU01 |
| E3 | Cada apertura del detalle de artista (HU04) pedía otra vez el artista y el catálogo completo de premios | `RepositoryCache` guarda los detalles en dos `LruCache` de 20 (músicos y bandas por separado, porque pueden compartir id) y la lista de premios; `invalidateArtists()` para las HU de escritura (HU12, HU14, HU15) | `RepositoryCache.kt`, `ArtistRepositoryImpl.kt` | `ArtistRepositoryImplTest`: *reopening an artist answers from the cache without calling the api*, *a musician and a band with the same id are cached apart*, *the prize catalog is fetched once*, *after invalidating artists the detail and prizes are fetched again* |
| E4 | `onTrimMemory` solo liberaba la caché con la app en segundo plano; con la app al frente y el sistema a punto de matar procesos no hacía nada | También con `TRIM_MEMORY_RUNNING_CRITICAL`; `clear()` vacía álbumes, artistas, premios y coleccionistas | `WineMusicApp.kt`, `RepositoryCache.kt` | `adb shell am send-trim-memory com.dev.wine.music.app RUNNING_CRITICAL` y reabrir un álbum: vuelve a la red; `RepositoryCacheTest` |
| E5 | Sin detector de trabajo de disco o red en el hilo principal | `StrictMode` solo en debug (hilo: `detectAll`; VM: closables, SQLite, activities filtradas) con `penaltyLog` | `WineMusicApp.kt` | `adb logcat -s StrictMode`: 0 violaciones en HU01 y HU04 |
| E6 | Se perfilaba el build debug: Compose sin optimizar y logging HTTP `BODY`, que copia cada respuesta en memoria, inflan CPU, memoria y *jank* | Build type `profiling` (`initWith(release)`: R8, `shrinkResources`, logging `NONE`), con `<profileable android:shell="true">` y firmado con la clave debug | `app/build.gradle.kts`, `app/src/profiling/AndroidManifest.xml` | `./gradlew installProfiling`; `device.txt` muestra `debuggable: no` |
| E7 | Cada integrante medía a su manera | `scripts/perf-capture.sh`: arranque en frío (3 veces y mediana), `gfxinfo`, `meminfo`, CPU y ANR por HU en `docs/performance/results/<dispositivo>/` | `scripts/perf-capture.sh`, `docs/performance/README.md` | Probado en el emulador API 30 (solo para validar el script; sus números no entran al reporte) |
| E8 | `CollectorListViewModel` (HU05) no tenía pruebas unitarias | `CollectorListViewModelTest` | `app/src/test/.../ui/collector/` | `./gradlew test` |

> **Pestañas:** volver a Álbumes, Artistas o Coleccionistas no repite peticiones porque Navigation conserva el ViewModel de cada pestaña. La caché del repositorio aporta cuando se crea un ViewModel nuevo (cada detalle, el onboarding y HU05 compartiendo `/collectors`, HU02 nombrando a los autores de las reseñas) o después de que Android destruye el ViewModel.

> **«Reintentar»:** no hizo falta un `forceRefresh`. La caché solo guarda respuestas exitosas, así que después de un error «Reintentar» siempre vuelve a la red.

