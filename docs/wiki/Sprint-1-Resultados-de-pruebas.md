# Sprint 1 · Inventario y resultados de pruebas

Complementa la [estrategia de pruebas del sprint 1](Sprint-1-Estrategia-de-pruebas). Los ID de objetivo (OBJ-n) son los de esa página.

## 1. Inventario de pruebas automatizadas

| ID | HU | Objetivo | Escenario / clase | Tipo | Nivel | Automatizada | Archivo | Resultado |
|---|---|---|---|---|---|---|---|---|
| T-01 | — | OBJ-7 | Mapeo de álbum, tracks e intérpretes | Funcional | Unidad | Sí | `app/src/test/.../data/mapper/AlbumMapperTest.kt` (3) | ☐ |
| T-02 | HU05 | OBJ-7 | Mapeo de coleccionista | Funcional | Unidad | Sí | `.../data/mapper/CollectorMapperTest.kt` (1) | ☐ |
| T-03 | — | OBJ-5 | `safeApiCall` traduce 404/400/5xx/IO a `AppError` | Robustez | Unidad | Sí | `.../data/remote/SafeApiCallTest.kt` (5) | ☐ |
| T-04 | HU01, HU02 | OBJ-7 | Repositorio de álbumes con API falsa | Funcional | Unidad | Sí | `.../data/repository/AlbumRepositoryImplTest.kt` (4) | ☐ |
| T-05 | HU03 | OBJ-7 | Repositorio de artistas une músicos y bandas | Funcional | Unidad | Sí | `.../data/repository/ArtistRepositoryImplTest.kt` (2) | ☐ |
| T-06 | HU05 | OBJ-7 | Repositorio de coleccionistas | Funcional | Unidad | Sí | `.../data/repository/CollectorRepositoryImplTest.kt` (2) | ☐ |
| T-07 | HU01 | OBJ-7 | Modelo `Album` (año, género, intérpretes) | Funcional | Unidad | Sí | `.../domain/model/AlbumTest.kt` (6) | ☐ |
| T-08 | HU01 | OBJ-1 | `GetAlbumsUseCase` ordena por nombre | Funcional | Unidad | Sí | `.../domain/usecase/GetAlbumsUseCaseTest.kt` (1) | ☐ |
| T-09 | HU02 | OBJ-2 | `GetAlbumDetailUseCase` | Funcional | Unidad | Sí | `.../domain/usecase/GetAlbumDetailUseCaseTest.kt` (4) | ☐ |
| T-10 | HU03 | OBJ-3 | `GetArtistsUseCase` | Funcional | Unidad | Sí | `.../domain/usecase/GetArtistsUseCaseTest.kt` (3) | ☐ |
| T-11 | HU01 | OBJ-1, OBJ-5 | Búsqueda, filtro por género, errores | Funcional | Unidad (ViewModel) | Sí | `.../ui/album/list/AlbumListViewModelTest.kt` (10) | ☐ |
| T-12 | HU02 | OBJ-2, OBJ-5 | Carga del detalle y errores | Funcional | Unidad (ViewModel) | Sí | `.../ui/album/detail/AlbumDetailViewModelTest.kt` (5) | ☐ |
| T-13 | HU03 | OBJ-3, OBJ-5 | Filtro por tipo y errores | Funcional | Unidad (ViewModel) | Sí | `.../ui/artist/ArtistListViewModelTest.kt` (9) | ☐ |
| T-14 | HU01 | OBJ-1, OBJ-5 | Estados de la pantalla del catálogo | Funcional | Integración (Compose) | Sí | `app/src/androidTest/.../ui/album/AlbumListContentTest.kt` (8) | ☐ |
| T-15 | HU02 | OBJ-2, OBJ-5 | Estados de la pantalla de detalle | Funcional | Integración (Compose) | Sí | `.../ui/album/AlbumDetailContentTest.kt` (10) | ☐ |
| T-16 | HU03 | OBJ-3, OBJ-5 | Estados de la pantalla de artistas | Funcional | Integración (Compose) | Sí | `.../ui/artist/ArtistListContentTest.kt` (7) | ☐ |
| E-01 | HU01 | OBJ-1 | Catálogo con intérprete, búsqueda, búsqueda + género, sin resultados, abrir detalle | Funcional | Sistema (E2E) | Sí | `.../e2e/HU01AlbumCatalogE2ETest.kt` (5) | ☐ |
| E-02 | HU01 | OBJ-5 | Error 500 + reintento, sin red, catálogo vacío | Robustez | Sistema (E2E) | Sí | `.../e2e/HU01AlbumCatalogErrorE2ETest.kt` (3) | ☐ |
| E-03 | HU02 | OBJ-2 | Sello y fecha, tracks, comentarios con autor, volver | Funcional | Sistema (E2E) | Sí | `.../e2e/HU02AlbumDetailE2ETest.kt` (4) | ☐ |
| E-04 | HU02 | OBJ-5 | Error 500 + reintento, 404 sin reintento, falla de coleccionistas | Robustez | Sistema (E2E) | Sí | `.../e2e/HU02AlbumDetailErrorE2ETest.kt` (3) | ☐ |
| E-05 | HU03 | OBJ-3 | Músicos y bandas juntos, filtro por banda, por músico y “Todos” | Funcional | Sistema (E2E) | Sí | `.../e2e/HU03ArtistListE2ETest.kt` (3) | ☐ |
| E-06 | HU03 | OBJ-5 | Sin red + reintento, sin artistas | Robustez | Sistema (E2E) | Sí | `.../e2e/HU03ArtistListErrorE2ETest.kt` (2) | ☐ |
| K-01 | HU01 | OBJ-1 | Search by performer, filter by genre and clear the search | Aceptación | Sistema (Kraken) | Sí | `e2e/kraken/features/HU01-album-catalog.feature` | ☐ |
| K-02 | HU01 | OBJ-1 | Open an album from the catalog and go back | Aceptación | Sistema (Kraken) | Sí | `e2e/kraken/features/HU01-open-detail-from-catalog.feature` | ☐ |
| K-03 | HU02 | OBJ-2 | Open the album detail and see its information | Aceptación | Sistema (Kraken) | Sí | `e2e/kraken/features/HU02-album-detail.feature` | ☐ |
| K-04 | HU03 | OBJ-3 | See the artists and filter them by type | Aceptación | Sistema (Kraken) | Sí | `e2e/kraken/features/HU03-artist-list.feature` | ☐ |
| M-01 | HU05 | OBJ-4 | Listado de coleccionistas: datos, vacío y error | Funcional | Sistema | **No** (pendiente) | Exploratoria manual | ☐ |
| M-02 | Todas | OBJ-6 | Instalar el APK y recorrer HU01–HU03 y HU05 en API 21, 30 y 35 | Compatibilidad | Sistema | No | Manual con `adb install -r` | ☐ |

Totales: **55** pruebas unitarias · **25** de contenido Compose · **20** E2E Espresso/Compose · **4** escenarios Kraken.

> Brecha conocida: HU05 aún no tiene `CollectorListViewModelTest`, `CollectorListContentTest` ni E2E. Crear el issue correspondiente y cerrarlo en el sprint 2.

## 2. Resultados de la ejecución

Completar después de la corrida oficial (backend en datos semilla y animaciones desactivadas).

| Fecha | Suite | Comando | Dispositivo / emulador | API | Pasaron | Fallaron | Evidencia |
|---|---|---|---|---|---:|---:|---|
| 2026-11-01 | Unitarias + instrumentadas + Kraken (cierre del sprint, ver [acta semana 4](Sprint-1-Acta-Semana-4)) | `./gradlew test`, `./gradlew connectedAndroidTest`, `npm run start` | ✏️ | ✏️ | 50 + 37 + 4 | 0 | ✏️ adjuntar reportes |
| AAAA-MM-DD | Unitarias | `./gradlew test` | JVM | — | | | `app/build/reports/tests/testDebugUnitTest/index.html` |
| AAAA-MM-DD | Instrumentadas + E2E | `./gradlew connectedAndroidTest` | Emulador Pixel | 21 | | | `app/build/reports/androidTests/connected/` |
| AAAA-MM-DD | Instrumentadas + E2E | `./gradlew connectedAndroidTest` | Emulador Pixel | 35 | | | `app/build/reports/androidTests/connected/` |
| AAAA-MM-DD | Kraken | `npm run start` (en `e2e/kraken`) | Teléfono físico | | | | `e2e/kraken/reports/<id>/index.html` |

## 3. Defectos reportados

| Issue | HU | Severidad | Título | Estado |
|---|---|---|---|---|
| #— | | | | |
