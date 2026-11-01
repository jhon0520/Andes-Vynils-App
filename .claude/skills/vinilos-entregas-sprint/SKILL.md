---
name: "vinilos-entregas-sprint"
description: "Úsala para preparar, revisar o autoevaluar una entrega de sprint de Vinilos (MISW4104): checklist por rúbrica, wiki, release/APK, retrospectiva, burndown y coevaluación."
---

# Entregas de sprint – Proyecto Vinilos (MISO Uniandes)

Equipo 10 "Wine Music" (repo GitHub jhon0520/Andes-Vynils-App). Cliente ficticio: The Software Design Company (TSDC). Revisor: "equipo Uniandes". Las entregas cierran **domingo 23:59 GMT-5**; solo cuenta lo actualizado en wiki/repo antes de esa hora (GitHub muestra la hora de cada edición). El avance de la 1.ª semana de cada iteración también se califica.

## Calendario
- Inception (S1–S2): backlog HU01–HU08 con puntos y tareas, milestones por sprint, tablero (TO-DO, Diseño, Implementación, Integración, Pruebas, Done), issues asignados, wireframe navegable, reglas del equipo y franja del lunes.
- Sprint 1 (S3–S4): 2 HU sugeridas. Retrospectiva del inception al inicio.
- Sprint 2 (S5–S6): acumulado **≥ 5 HU**. Al inicio: retrospectiva S1 + reflexión estimado vs real + **burndown chart** del S1 + coevaluación.
- Sprint 3 (S7–S8): acumulado **8 HU**, accesibilidad/usabilidad, consolidación.
- Valor: Inception 5 %, S1 10 %, S2 20 %, S3 25 % (multiplicado por coevaluación).

## Checklist común de cada release
- [ ] Repo accesible; rama `main` con el código del release y **tag/release de GitHub** (`vX.Y-sprintN`).
- [ ] **APK** listo para instalar subido al repo (y adjunto al release).
- [ ] GitFlow visible: `feature/*` → PR a `develop` revisado por otro miembro → **squash and merge**; merge `develop` → `main` para el release. Todos los miembros con PRs.
- [ ] README: requisitos, cómo levantar BackVynils, URL de API, `./gradlew assembleDebug`, cómo correr pruebas, problemas comunes.
- [ ] Wiki con sección del sprint: diseño arquitectónico (diagramas de **clases, paquetes y componentes** de las HU del sprint, coherentes con el código), estrategia de pruebas, inventario, resultados, enlaces a issues de defectos.
- [ ] Evidencia de reuniones semanales (acta/bitácora/video) de ambas semanas.
- [ ] Tablero e issues actualizados y asignados; milestones con las HU correctas.
- [ ] La app no falla al ejecutarse en dispositivos desde Android L.

## Rúbrica Sprint 1 (para autoevaluar)
Ágil 15 (repo 2, APK 2, release 2, GitFlow 2, reuniones 4, retro inception 3) · Documentación 5 (arquitectura 2, estrategia 3) · Diseño y construcción 40 (diagramas 5, refleja HU 5, código coherente 5, MVVM+Repository+Service Adapter 10, consume API 10, README 5) · Calidad 40 (estrategia clara 5, componentes 5, incluye E2E 5, HU implementadas 5, sin errores desde L 5, cada HU con pruebas automatizadas 5, Espresso/Kraken en verde 5, resultados y defectos como issues 5).

## Rúbrica Sprint 2
Ágil 12 (repo, APK, release, GitFlow, reuniones S5–S6, retro: 2 c/u) · Documentación 12 (arquitectura 1, estrategia 1, reflexión estimación S1 1, **burndown S1 3**, **detalle micro-optimizaciones 3**, **reporte de profiling 3**) · Diseño y construcción 48 (diagramas ≥5 HU 3, coherencia 5, patrones 5, micro-optimizaciones 5, **memoria 10**, **evitar ANRs 10**, API 5, README 5) · Calidad 28 (estrategia clara 3, componentes 3, E2E de HU S1+S2 3, análisis de desempeño 3, 5 HU implementadas 5, sin errores desde L 3, E2E automatizados en verde 5, resultados + profiling + issues 3).
- Técnicas a evidenciar en las 5 HU: micro-optimizaciones (Lint/SonarQube), corrutinas para evitar ANRs, reducción de memoria, profiling en ≥ 3 dispositivos físicos.

## Retrospectiva y estimación
- Hacer primero la auto y coevaluación individual (formulario del curso; dimensiones: compromiso, herramientas, comunicación, solidaridad, ética, capacidad técnica) y luego la retrospectiva de equipo.
- Retrospectiva: qué salió bien / qué mejorar / compromisos con responsable, fecha y criterio de éxito; revisar los compromisos de la retro anterior.
- Reflexión de estimación: tabla HU | puntos estimados | puntos/horas reales | desviación | causa; burndown con puntos restantes por día del sprint (ideal vs real) y explicación de las desviaciones.
- Luego, ceremonia de planeación del siguiente sprint ajustando lo planeado en el inception.

## Cómo responder
- Cuando el usuario pida "revisar la entrega", recorre el checklist y la rúbrica del sprint vigente contra el repo real (ramas, tags, PRs, wiki, archivos) y devuelve: puntos en riesgo ordenados por peso, qué falta y la acción concreta para cada uno, antes del cierre del domingo.
- Redacta los textos de wiki (retro, reflexión, estrategia, arquitectura) en Markdown listo para pegar.
## Relación con el repo
- Flujo de ramas, commits (`feat(ANDES): ...`) y PR: `docs/gitflow.md` y `.claude/skills/mvvm-feature/references/docs-and-git.md`.
- APK del release: `distributions/app-debug.apk` (actualizarlo antes de etiquetar).
- Arquitectura y trazabilidad a revisar en cada entrega: `docs/App-technical-documentation.md`.
- Contexto completo del curso y rúbricas: `docs/contexto-curso-modulos-1-6.md`.
