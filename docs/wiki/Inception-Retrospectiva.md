# Inception · Retrospectiva

**Wine Music (Vinilos)** · Equipo 10 · Realizada al inicio del sprint 1 (semana 3)

> ✏️ **Completar con el equipo.** Las secciones marcadas con ✏️ deben llenarse con lo que se dijo en la reunión de retrospectiva (fecha, asistentes, votos y comentarios reales). Lo demás se armó a partir de la evidencia del repositorio (commits, PR y documentos del inception) y debe validarse en la reunión.

## 1. Datos de la sesión

| Campo | Valor |
|---|---|
| Fecha y hora | ✏️ AAAA-MM-DD, hh:mm (franja del lunes) |
| Medio | ✏️ Meet / Teams (enlace a la grabación si existe) |
| Facilitador | ✏️ |
| Asistentes | ✏️ Jhon Edman Linares Rivera · Nicolas Zambrano Madero · Jesus Enrique Quesada Diaz · Juan Martin Santos Ayala |
| Dinámica | *Start · Stop · Continue* + valoración 1–5 por dimensión |
| Duración | ✏️ 45 min |

## 2. Qué se entregó en el inception

| Entregable | Evidencia |
|---|---|
| Backlog HU01–HU08 con puntos de historia y tareas | Issues y milestones del repositorio |
| Tablero con columnas TO-DO, Diseño, Implementación, Integración, Pruebas, Done | GitHub Projects |
| Wireframe navegable de las 8 HU | Carpeta *Vinilos – Wireframes* / Figma |
| Proyecto Android base y arquitectura inicial (H0) | PR #10 (`inception`), PR #11 y #13 (`h0_base_architecture`) |
| Flujo de ramas GitFlow documentado | `docs/gitflow.md`, PR #15 |
| README y carpeta `distributions/` para el APK | PR #12, #14 |
| Primer release `release → main` | PR #17 |

## 3. Valoración del proceso por el equipo

Escala 1 (muy mal) – 5 (muy bien). ✏️ Reemplazar los valores con el promedio de los votos de los cuatro integrantes.

| Dimensión | Valoración (1–5) | Comentario del equipo |
|---|:---:|---|
| Planeación y estimación del backlog | ✏️ | ✏️ ¿Los puntos asignados a HU01–HU08 reflejan el esfuerzo esperado? |
| Comunicación y reuniones semanales | ✏️ | ✏️ ¿Se cumplió la franja del lunes? ¿Se dejaron actas? |
| Uso del tablero e issues | ✏️ | ✏️ ¿Cada tarea tenía responsable y estado actualizado? |
| GitFlow y revisión de PR | ✏️ | ✏️ ¿Todos abrieron PR y otro integrante los revisó? |
| Diseño (wireframes) | ✏️ | ✏️ ¿El wireframe cubrió todas las HU y estados (carga, error, vacío)? |
| Documentación en la wiki | ✏️ | ✏️ La wiki no estaba organizada por sprint ni tenía actas. |
| Distribución del trabajo | ✏️ | ✏️ |
| **Promedio** | ✏️ | |

## 4. Qué salió bien (Continue)

- La arquitectura base (MVVM + Clean Architecture con Hilt, Retrofit y Compose) quedó lista antes del sprint 1, lo que permitió desarrollar HU01, HU02 y HU03 en paralelo con la misma estructura.
- GitFlow quedó documentado (`docs/gitflow.md`) y se usó desde el inicio con PR hacia `develop`.
- El README explica cómo compilar y apuntar la app a BackVynils.
- ✏️ Agregar lo que el equipo mencione.

## 5. Qué se debe mejorar (Start / Stop)

- **Start:** dejar acta de cada reunión semanal en la wiki el mismo día.
- **Start:** organizar la wiki por sprint en la barra lateral (`_Sidebar`).
- **Start:** definir la estrategia de pruebas con la plantilla del curso al inicio de cada sprint, no al final.
- **Stop:** subir commits directos o PR sin revisión de otro integrante.
- **Stop:** dejar los diagramas y el código con nombres en español; todo artefacto técnico va en inglés.
- ✏️ Agregar lo que el equipo mencione.

## 6. Decisiones tomadas

| # | Decisión | Motivo | Responsable | Fecha límite | Criterio de éxito |
|---|---|---|---|---|---|
| D1 | Usar Jetpack Compose + MVVM + Repository + Service Adapter (Retrofit) con Hilt | Lo exige la rúbrica (10 + 10 puntos) y permite probar cada capa por separado | Todo el equipo | Inicio S1 | HU01–HU03 implementadas con la misma estructura |
| D2 | Aplicar GitFlow: `feature/*` desde `develop`, PR revisado por otro integrante, *squash and merge* | Trazabilidad y nota de prácticas ágiles | Todo el equipo | Continuo | Todos los integrantes con al menos un PR por sprint |
| D3 | Asignar una HU por integrante en el sprint 1 | Avanzar en paralelo y que todos aporten código | ✏️ | S3 | Cada HU con su PR |
| D4 | Escribir pruebas unitarias y E2E (Espresso/Compose + Kraken) dentro de la misma HU | Definición de terminado: una HU sin pruebas en verde no se cierra | Responsable de cada HU | Fin de S4 | Cada HU con E2E en verde |
| D5 | Publicar el APK en `distributions/` y un release etiquetado en `main` por sprint | Requisito de la entrega | ✏️ | Domingo de S4 | Release `v1.0-sprint1` con el APK adjunto |
| D6 | Mantener toda la documentación en la wiki ordenada por sprint, con actas semanales | Retroalimentación del inception | Nicolas Zambrano | Cada lunes | Acta publicada el mismo día |
| D7 | ✏️ | ✏️ | ✏️ | ✏️ | ✏️ |

## 7. Seguimiento

Los compromisos de esta retrospectiva se revisan al inicio de la [retrospectiva del sprint 1](Sprint-1-Retrospectiva).

| Compromiso | ¿Se cumplió? | Comentario |
|---|---|---|
| D1–D6 | ✏️ se diligencia en la retro del sprint 1 | |
