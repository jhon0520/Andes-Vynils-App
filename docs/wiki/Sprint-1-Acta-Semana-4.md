# Sprint 1 · Acta de reunión · Semana 4

| Campo | Valor |
|---|---|
| Fecha | Domingo 1 de noviembre de 2026 |
| Medio | Reunión sincrónica del equipo |
| Asistentes | Jhon Edman Linares Rivera · Jesús Enrique Quesada Díaz · Nicolas Zambrano Madero |
| Ausentes | Juan Martin Santos Ayala |
| Evidencia | Captura de la reunión en el [documento de actas del equipo](https://docs.google.com/document/d/18FE3xbBaMrto_UNlFDcZVraxfUT13bbsTulDw_RbJc0/edit?tab=t.0) |

## Tema: avance y cierre del sprint 1

- Se revisó el estado del repositorio frente a la rúbrica de la entrega “Primera versión de Vinilos” y se identificaron los puntos pendientes.
- Se documentaron en la wiki el [diseño arquitectónico](Sprint-1-Diseno-arquitectonico) del sprint (diagramas de componentes, paquetes, clases y secuencia de HU01–HU03) y la [estrategia de pruebas](Sprint-1-Estrategia-de-pruebas).
- Se completaron la HU02 (detalle de álbum) y la HU03 (listado de artistas), cada una en su rama `feature/*` desde `develop`.
- Se incorporó el manejo de errores por tipo (sin red, recurso no encontrado, validación y servidor) en todas las pantallas.
- Se levantó BackVynils en Docker de forma local para ejecutar las pruebas E2E contra la API real.
- Se ejecutaron las pruebas automatizadas: **50 unitarias, 37 instrumentadas (Compose/Espresso y E2E) y 4 escenarios Kraken, todas en verde**.
- Se corrigieron problemas del entorno de pruebas (Espresso en Android 14+, Kraken con Node 20+) y quedaron documentados en la guía E2E.

## Seguimiento de compromisos de la retrospectiva

Se revisaron los compromisos de crear issues bloqueantes por HU y de planear mejor las reuniones sincrónicas (ambos se dieron por cumplidos en la [retrospectiva del sprint 1](Sprint-1-Retrospectiva)).

## Resultados

- HU01, HU02 y HU03 quedan implementadas y con pruebas automatizadas en verde.
- La wiki del sprint 1 cuenta con la arquitectura y la estrategia de pruebas.

## Tareas pendientes para el cierre de la entrega

| Tarea | Estado posterior |
|---|---|
| Abrir los PR de HU02 y HU03 para revisión y hacer *squash merge* a `develop` | Hecho (PR #20, #21, #22) |
| Generar el release etiquetado en `main` con el APK en `distributions/` | PR #23 y #25 |
| Registrar los resultados de pruebas y los defectos como issues | [Resultados de pruebas](Sprint-1-Resultados-de-pruebas) |
