# Changelog

Cambios importantes de **Wine Music (Vinilos)** por versión. Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/); las versiones siguen [Versionado Semántico](https://semver.org/lang/es/). Cada versión corresponde a un release etiquetado en `main`, con su APK en `distributions/`.

## [Sin publicar]

### Agregado

- **HU01 · Catálogo de álbumes completo**
  - Búsqueda por nombre de álbum o de intérprete sobre la lista ya cargada, sin nuevas llamadas al backend. Ignora mayúsculas y tildes («ruben» encuentra «Rubén»).
  - Filtro por género (Todos, Salsa, Rock, Clásica, Folk) que se combina con la búsqueda.
  - Pantalla «Esta funda está vacía» cuando la búsqueda o el filtro no tienen resultados, con el botón «Limpiar búsqueda».
  - Las tarjetas muestran el intérprete y el género en español.
  - Funda de color con el nombre del álbum y el sello cuando la portada no carga.
  - Cuadrícula fija de dos columnas y encabezado «Vinilos · Catálogo».
- `AlbumGenre` en el dominio, con los valores exactos que acepta el backend. Lo reutilizará el formulario de HU07.
- URL de la API configurable con `api.base.url` en `local.properties`, sin tocar código. Permite usar un teléfono físico con `adb reverse tcp:3000 tcp:3000`.
- Pruebas de HU01:
  - unitarias del ViewModel (búsqueda, filtro, combinación, limpiar) y del modelo `Album`;
  - de la pantalla (`AlbumListContentTest`);
  - E2E con Compose sobre `MainActivity` contra el backend real (`HU01AlbumCatalogE2ETest`).
- Proyecto Kraken en `e2e/kraken` (Appium 1.22.3) con dos escenarios de HU01.
- **HU02 · Detalle de álbum completo**
  - Portada con el vinilo, sello, fecha de lanzamiento (dd/mm/aaaa), género en español y cantidad de tracks.
  - Descripción recortada con «Leer más» / «Leer menos» cuando no cabe.
  - Tracklist numerado por lado del vinilo (A1, A2… B1) con la duración, y estado vacío si no hay tracks.
  - Reseñas con texto, calificación de 0 a 5 estrellas y autor. El autor sale de `GET /collectors`, porque el backend no lo envía con el álbum; si no hay autor se muestra «Anónimo». Estado vacío si no hay reseñas.
  - Un álbum que no existe (404) muestra «No encontramos lo que buscas», sin «Reintentar».
- **HU03 · Listado de artistas completo**
  - Filtro Todos / Músicos / Bandas sobre la lista ya cargada.
  - Avatar cuadrado para bandas y redondo para músicos.
  - Cada músico muestra su banda, tomada de los `musicians` de `GET /bands`, o «Sin banda».
  - Mensaje «No hay artistas de este tipo» con el botón «Ver todos» cuando un filtro queda vacío.
- Errores por tipo: `AppError` en el dominio, `safeApiCall` en los repositorios y `ErrorType` en `UiState.Error`. «Sin conexión» y «No encontrado» tienen su propio mensaje.
- Pruebas de HU02 y HU03: unitarias (casos de uso, ViewModels, mappers, `safeApiCall`), de pantalla (`AlbumDetailContentTest`, `ArtistListContentTest`), E2E con Compose (`HU02AlbumDetailE2ETest`, `HU03ArtistListE2ETest`) y dos escenarios Kraken.

### Cambiado

- `testTag` expuestos como resource id en la raíz de la UI, para que Kraken los encuentre.
- Tags en los estados de carga, error, vacío y sin resultados, y en el detalle de álbum (`album_detail`, `back_button`).
- El título de la pestaña ahora es «Álbumes» (antes «Albumes»).
- El README explica cómo correr la app en un teléfono físico.
- Pestañas de la barra inferior con tags `nav_albums`, `nav_artists` y `nav_collectors`.
- Espresso 3.7.0 y androidx-junit 1.3.0: con Espresso 3.5.1 ninguna prueba de interfaz corría en Android 14 o superior (`NoSuchMethodException: InputManager.getInstance`).

### Corregido

- `AlbumListContentTest` buscaba el texto «Retry»; ahora lee «Reintentar» desde `strings.xml`.
- «Musico» ahora se escribe «Músico».
- Con el teclado abierto, el mensaje de «sin resultados» quedaba tapado. Ahora la tecla «Buscar» del teclado lo cierra y esa vista se puede desplazar.

## [1.0.0] - 2026-10-25

Primer release: la arquitectura base de la app y las pantallas de consulta iniciales conectadas a BackVynils.

### Agregado

- **Arquitectura**
  - MVVM + Clean Architecture con los patrones Repository y Service Adapter. Capas `ui`, `domain` y `data`, inyectadas con Hilt.
  - Estado de cada pantalla como `StateFlow<UiState<T>>` (`Loading`, `Success`, `Error`).
- **Conexión con BackVynils**
  - Retrofit + kotlinx.serialization, con registro de peticiones en debug.
  - Consume `GET /albums`, `GET /albums/{id}`, `GET /musicians`, `GET /bands` y `GET /collectors`.
- **Navegación**
  - Barra inferior con tres pestañas: Álbumes, Artistas y Coleccionistas.
  - Rutas tipadas con Navigation Compose.
- **Pantallas**
  - Catálogo de álbumes (base de HU01): cuadrícula con portada, nombre, año y género, ordenada por nombre.
  - Detalle de álbum (base de HU02): portada, intérpretes, año, género, sello, descripción y lista de tracks.
  - Listado de artistas (base de HU03): músicos y bandas juntos, ordenados por nombre.
  - Listado de coleccionistas (base de HU05): nombre, correo y teléfono, ordenados por nombre.
  - Estados de carga, error con «Reintentar» y lista vacía en todas las pantallas.
- **Identidad visual**
  - Splash screen con tocadiscos e ícono propio.
  - Tipografía Libron y tema Material 3 con fondo claro y oscuro.
- **Pruebas**
  - Unitarias de casos de uso, ViewModels, mapper y repositorio de artistas, con repositorios falsos.
  - Prueba de Compose de la lista de álbumes.
- **Documentación y entregables**
  - Documentación técnica de la app y del backend, y guía de GitFlow.
  - APK de pruebas en `distributions/`.

[Sin publicar]: https://github.com/jhon0520/Andes-Vynils-App/compare/release-1.0.0...develop
[1.0.0]: https://github.com/jhon0520/Andes-Vynils-App/releases/tag/release-1.0.0
