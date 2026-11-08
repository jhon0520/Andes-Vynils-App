# Inception · Retrospectiva

**Wine Music (Vinilos)** · Equipo 10 · Cierre del inception, antes de iniciar el sprint 1

## 1. Datos de la sesión

| Campo | Valor |
|---|---|
| Fecha | 25/10/2026 |
| Facilitación | Nicolas Zambrano Madero |
| Técnica | Empezar · Dejar · Continuar, con votación y análisis de causa raíz |
| Foco | Mejora continua de la participación del equipo |
| Participación | 3 de 4 integrantes (check-ins) · 8 notas |
| Asistentes | Jhon Edman Linares Rivera · Jesús Enrique Quesada Díaz · Nicolas Zambrano Madero (ausente: Juan Martin Santos Ayala) |
| Acta | [Sprint 1 · Acta semana 3](Sprint-1-Acta-Semana-3) |
| ROTI (retorno del tiempo invertido) | **5.0 / 5** |

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

El equipo valoró la sesión con un **ROTI de 5.0 / 5**. La mayor parte de los votos (4) fue para **la buena comunicación** como lo que mejor funcionó del inception. Los puntos débiles fueron la **asistencia a las reuniones** y el **cumplimiento de los compromisos del equipo** (2 votos cada uno): no todos los integrantes asistieron siempre, y eso dificultó repartir tareas y resolver bloqueantes a tiempo.

| Categoría | Nota | Votos |
|---|---|:---:|
| **Continuar** | La buena comunicación | 4 |
| Continuar | Trabajo en equipo | 1 |
| Continuar | Iteraciones continuas para mejorar nuestros trabajos | 1 |
| Continuar | Comunicación | 0 |
| **Empezar** | Asistir a las reuniones | 2 |
| Empezar | Creación de bloqueantes en las HU | 1 |
| Empezar | Respetar las sesiones de equipo para mejorar la comunicación | 1 |
| **Dejar de hacer** | Dejar de faltar a los compromisos del equipo | 2 |

## 4. Temas priorizados y causa raíz

1. **La buena comunicación (4 votos).**
   - Al compartir expectativas, compromisos y metas se puede trazar un plan claro.
   - Permite sacar mejores conclusiones sobre las ideas y entregar un trabajo en conjunto.
   - Con buena comunicación hay mejor desempeño.
2. **Asistir a las reuniones (2 votos).**
   - Permite organizarse mejor en las tareas y recibir apoyo con los bloqueantes.
   - Todos tienen compromisos personales, pero todos deben comprometerse con el estudio.
3. **Dejar de faltar a los compromisos del equipo (2 votos).**
   - Es la manera más efectiva de comunicar los bloqueantes a todo el equipo.

## 5. Decisiones tomadas

### 5.1 Compromisos de la retrospectiva

| # | Compromiso | Responsable | Fecha | Criterio de éxito |
|---|---|---|---|---|
| C1 | Revisar nuestro trabajo y encontrar mejoras para iterar y mejorar | Nicolas | 28/11/2026 | Calidad de las entregas de las HU |
| C2 | Crear un issue bloqueante en cada HU apenas aparezca y sea relevante | Jesús, Jhon, Nicolas | 26/10/2026 | Los puntos en *carry over* se explican con los bloqueantes que tuvo cada HU |
| C3 | Asistir a los compromisos del equipo | Juan Martin | 28/11/2026 | Demuestra interés por los compromisos |
| C4 | Planear mejor las reuniones sincrónicas | Jhon, Jesús, Nicolas | 30/10/2026 | Se aprecia una alta participación |

### 5.2 Decisiones técnicas y de proceso del inception

Tomadas por el equipo durante el inception (evidencia en el repositorio):

| # | Decisión | Motivo |
|---|---|---|
| D1 | Jetpack Compose + MVVM + Repository + Service Adapter (Retrofit) con Hilt | Lo exige la rúbrica y permite probar cada capa por separado |
| D2 | GitFlow: `feature/*` desde `develop`, PR revisado por otro integrante, *squash and merge* | Trazabilidad del trabajo de cada integrante |
| D3 | Una HU por integrante en el sprint 1 | Avanzar en paralelo y que todos aporten código |
| D4 | Pruebas unitarias y E2E (Espresso/Compose + Kraken) dentro de la misma HU | Una HU sin pruebas en verde no se considera terminada |
| D5 | APK en `distributions/` y release etiquetado en `main` por sprint | Requisito de la entrega |

## 6. Seguimiento

Revisado al inicio de la [retrospectiva del sprint 1](Sprint-1-Retrospectiva) (08/11/2026): **2 de 4 compromisos cumplidos**.

| Compromiso | Estado al 08/11/2026 |
|---|---|
| C1 · Revisar nuestro trabajo para iterar y mejorar | 🟡 En curso |
| C2 · Issue bloqueante en cada HU | ✅ Cumplido |
| C3 · Asistir a los compromisos del equipo | 🟡 En curso |
| C4 · Planear mejor las reuniones sincrónicas | ✅ Cumplido |
