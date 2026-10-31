# Wine Music (Vinilos) — contexto para Claude

App Android nativa del **Equipo 10** para el curso *Ingeniería de Software para Aplicaciones Móviles* (MISW4104, MISO Uniandes). Cliente ficticio: The Software Design Company (TSDC). Consume la API REST **BackVynils** (NestJS).

## Stack y arquitectura
- Kotlin, Jetpack Compose + Material 3, Hilt, Retrofit + kotlinx.serialization, OkHttp logging, Coil, Navigation Compose, corrutinas.
- MVVM + Clean Architecture: `ui/` (Screen + Content + ViewModel con `StateFlow<UiState<T>>`) → `domain/` (modelos, interfaces de repositorio, casos de uso) → `data/` (Api Retrofit = Service Adapter, DTO, mapper, RepositoryImpl), con DI en `di/`.
- Paquete base: `com.dev.wine.music.app`. URL de la API en `app/build.gradle.kts` → `BuildConfig.API_BASE_URL` (emulador: `http://10.0.2.2:3000/`).

## Documentos del repo (léelos antes de cambiar algo)
- `docs/App-technical-documentation.md`: mapa de trazabilidad HU → clases, endpoints y rutas.
- `docs/Backend-technical-documentation.md`: endpoints y trampas conocidas del backend.
- `docs/gitflow.md`: ramas, commits y PR.
- `docs/E2E-testing-guide.md`: pruebas E2E (Compose y Kraken).
- `docs/contexto-curso-modulos-1-6.md`: contexto del curso (módulos 1–6), reglas del proyecto, rúbricas de los sprints 1 y 2, endpoints por HU.

## Skills del proyecto (`.claude/skills/`)
- `mvvm-feature`: implementar una HU o feature nuevo (capas, plantillas, pruebas, documentación).
- `habiilidad-tecnica`: buenas prácticas, contexto técnico del curso y rendimiento (ANR, memoria, caché, micro-optimizaciones).
- `vinilos-plan-de-pruebas`: estrategia y plan de pruebas por sprint, inventario, defectos y profiling.
- `vinilos-entregas-sprint`: checklist y autoevaluación de cada entrega frente a la rúbrica.

## Reglas del curso que se califican
- Patrones **MVVM + Repository + Service Adapter** y consumo real de la API.
- **GitFlow**: `feature/*` desde `develop`, PR revisado por otro miembro, **squash and merge**; cada entrega queda **etiquetada como release en `main`**, con el **APK en el repo** (`distributions/`).
- Cada HU debe tener **pruebas automatizadas** (Espresso/Compose y Kraken) en verde; los defectos se registran como issues.
- La wiki se organiza por sprint: arquitectura (diagramas de clases, paquetes y componentes), estrategia de pruebas, resultados. Desde el sprint 2 también: burndown, micro-optimizaciones y profiling en ≥ 3 dispositivos físicos.
- Hitos de HU: ≥ 5 al cierre del sprint 2 y 8 (HU01–HU08) al cierre del sprint 3.
- Las entregas cierran el domingo a las 23:59 GMT-5.
- La app debe funcionar **desde Android L (API 21)**. Hoy `minSdk = 24`: es un riesgo abierto, coméntalo antes de tocar `minSdk`.

## Comandos
```bash
./gradlew test                   # unitarias
./gradlew assembleDebug          # compilar
./gradlew connectedAndroidTest   # UI/E2E (emulador + backend)
./gradlew lint                   # micro-optimizaciones
```

## Forma de trabajar
- Responde en español. Haz commit o push solo cuando el usuario lo pida.
