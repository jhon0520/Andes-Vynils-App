---
name: "vinilos-plan-de-pruebas"
description: "Úsala para diseñar o actualizar la estrategia y plan de pruebas de la app Vinilos (MISW4104): inventario, casos E2E Espresso/Compose/Kraken, profiling y reporte de defectos por sprint."
---

# Plan de pruebas – App Vinilos (Ingeniería de Software para Apps Móviles, MISO Uniandes)

Contexto: la app es Android nativa en Kotlin (Compose + Hilt + Retrofit) que consume BackVynils. El curso exige "pruebas continuas": cada sprint define su estrategia al inicio, cada HU tiene escenarios automatizados y los resultados quedan en la wiki del repo; los defectos se reportan como issues.

## Qué entregar en cada sprint (wiki → sección del sprint)
1. **Estrategia de pruebas** con estos componentes (la rúbrica revisa que sea clara y completa):
   - Objetivo y alcance (HU del sprint + regresión de las anteriores).
   - Niveles y tipos: unitarias (UseCase, Repository con fakes, ViewModel con `MainDispatcherRule`), integración (Repository + MockWebServer opcional), E2E de UI (Espresso / Compose UI Test), E2E con Kraken si hay interacción entre usuarios, exploratorias/manuales con ADB.
   - Pirámide: muchas unitarias, menos integración, pocas E2E críticas por HU.
   - Entorno: dispositivos/emuladores y versiones de Android (desde API 21 si es posible; mínimo cubrir una versión antigua, una media y la última), backend usado, datos de prueba.
   - **Gestión de datos de prueba** (obligatoria desde que haya HU de escritura): qué pruebas solo leen datos semilla y cuáles crean datos; cómo se generan nombres únicos (`$name_1` en Kraken, `E2E <timestamp>` en Compose); cómo se deja el backend en datos semilla antes de la corrida oficial (`docker-compose down -v`, porque BackVynils tiene `dropSchema: false` y reiniciar no borra datos). Detalle en `docs/E2E-testing-guide.md` → "Datos de prueba y estado del backend".
   - Herramientas y comandos: `./gradlew test`, `./gradlew connectedAndroidTest`, `./gradlew lint`, Android Studio Profiler, Firebase Test Lab.
   - Criterios de entrada/salida (p. ej. 100 % de escenarios automatizados en verde, 0 defectos críticos abiertos para release).
   - Cronograma por semana y responsables.
2. **Inventario de pruebas**: tabla `ID | HU | Escenario | Tipo | Nivel | Automatizada (sí/no) | Archivo/clase | Resultado`.
3. **Casos E2E** por HU (formato Dado/Cuando/Entonces). Cada "Entonces" debe seguir siendo cierto aunque el backend tenga más datos que la semilla: verificar que un elemento existe (no conteos, orden ni "el primero"), comprobar filtros con "todos los ítems mostrados cumplen" y no solo con "X no aparece", y en las HU que crean datos verificar solo lo creado por su nombre único. Incluir: camino feliz, lista vacía, error de red (pantalla de error + reintento), estado de carga, navegación ida y vuelta; para formularios (HU07, HU08, HU09, HU11, HU13, HU14): campos obligatorios, valores de enum válidos (genre, recordLabel, status), formato de fecha/duración, respuesta exitosa y error 4xx/5xx.
4. **Scripts**, con los nombres que ya usa el repo:
   - `app/src/androidTest/.../ui/<feature>/<Pantalla>ContentTest.kt`: estados de la pantalla sin backend.
   - `app/src/androidTest/.../e2e/HUxx<Nombre>E2ETest.kt` (ej. `HU01AlbumCatalogE2ETest`): recorrido con el backend real.
   - `app/src/androidTest/.../e2e/HUxx<Nombre>ErrorE2ETest.kt` (ej. `HU01AlbumCatalogErrorE2ETest`): errores con `FakeBackendRule`.
   - `e2e/kraken/features/HUxx-<nombre>.feature` (ej. `HU01-catalogo-albumes.feature`).
   - Unitarias en `app/src/test/...` con el nombre de la clase probada + `Test`.
5. **Reporte de resultados**: tabla de ejecución (fecha, dispositivo, API, pasaron/fallaron) + capturas o reporte HTML de Gradle (`app/build/reports/androidTests/connected/`).
6. **Defectos**: cada fallo es un issue con etiqueta `bug`, HU, pasos para reproducir, resultado esperado/obtenido, dispositivo/API, severidad y evidencia; enlazar el issue en el inventario.

## Automatización UI
- Compose: `createAndroidComposeRule<MainActivity>()` o `createComposeRule()` para contenido aislado; localizar por `testTag` (declarar tags constantes en la Screen), `onNodeWithText`, `performClick`, `performTextInput`, `assertIsDisplayed`, `onAllNodesWithTag(...).assertCountEquals`. La API de Compose corre sobre Espresso; si el tutor exige Espresso explícito, añadir al menos un flujo con `onView(...)`/`ActivityScenarioRule` o confirmar que se acepta.
- Para E2E contra backend real, usar `waitUntil { ... }` con timeout en vez de `Thread.sleep`. Para errores deterministas (500, sin red, 404, vacío): `@HiltAndroidTest` + `@UninstallModules(ApiConfigModule::class)` + `FakeBackendRule` (no `@TestInstallIn`, que afecta a todas las pruebas). Toda prueba que abra `MainActivity` lleva `@HiltAndroidTest` porque el runner es `HiltTestRunner`.
- Desactivar animaciones en el dispositivo antes de correr:
  `adb shell settings put global window_animation_scale 0.0` (igual para `transition_animation_scale` y `animator_duration_scale`).
- Kraken (Gherkin + Appium, Node 20 según `e2e/kraken/.nvmrc`; correr con `NODE_OPTIONS=--no-deprecation`): **un escenario por HU con su recorrido principal**, como pide el curso; las señales (`@user2`) solo cuando la HU involucra a varios usuarios. `@user1 @mobile`, `properties.json` con datos, `mobile.json` con `apk_path`, `apk_package`, `apk_launch_activity`.
- ADB útil para manuales: `adb install -r app-debug.apk`, `adb shell am start -n <pkg>/<activity>`, `adb shell svc wifi disable` (probar sin red), `adb shell input text`, `adb logcat`.

## Sprint 2 en adelante: análisis de desempeño (obligatorio)
- Perfilar las 5 HU de los sprints 1 y 2 en **al menos 3 dispositivos físicos distintos** con Android Studio Profiler (CPU, memoria, red, energía).
- Métricas por HU y dispositivo: tiempo de carga de la pantalla, uso de memoria (pico y estable), % CPU, frames lentos/jank, ANRs (debe ser 0), tráfico de red y si la caché evita peticiones repetidas.
- Comparar antes/después de micro-optimizaciones y caché; incluir capturas del profiler y una conclusión por HU.
- Opcional/recomendado: Firebase Test Lab (Robo test + instrumentadas) para cubrir fragmentación.
- La estrategia debe describir el análisis de desempeño (vale puntos propios) y los resultados del perfilamiento van al repo/wiki.

## Sprint 3
- Añadir accesibilidad y usabilidad (Accessibility Scanner, contentDescription, tamaños táctiles ≥ 48dp, contraste) a la estrategia, más regresión completa de las 8 HU.

## Formato de salida
- Entregar en Markdown listo para pegar en la wiki de GitHub (encabezados, tablas). Si se pide código de prueba, generar los archivos Kotlin con los nombres y paquetes reales del repo.
## Relación con el repo
- Convenciones de pruebas E2E (testTags, Compose sobre `MainActivity`, Kraken en `e2e/kraken`): `docs/E2E-testing-guide.md` y `.claude/skills/mvvm-feature/references/e2e.md` y `testing.md`. Úsalas para los scripts; esta skill define qué documentar y entregar por sprint.
- APK para Kraken y para la entrega: `distributions/app-debug.apk`.
- Contexto completo del curso y rúbricas: `docs/contexto-curso-modulos-1-6.md`.
