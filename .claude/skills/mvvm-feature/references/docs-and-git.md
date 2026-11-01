# Rama, commits, documentación y PR

Fuente: `docs/gitflow.md` y la sección «Coherencia código–diseño» de `docs/App-technical-documentation.md`. Si esos documentos cambian, mandan ellos.

## Rama

```bash
git status                     # si hay cambios sin commit, pregunta antes de seguir
git checkout develop
git pull origin develop
git checkout -b feature/hu06-detalle-coleccionista
```

| Tipo | Nombre |
|---|---|
| HU | `feature/huXX-descripcion` (minúsculas, guiones, sin tildes) |
| Tarea transversal | `feature/errores-por-tipo`, `feature/perfil-visitante` |
| Corrección urgente sobre `main` | `hotfix/descripcion` |

Nunca trabajes directo en `develop`, `release/*` ni `main`.

## Commits

Formato del repositorio: `tipo(ANDES): descripción en presente`, en español.

| Tipo | Ejemplo |
|---|---|
| `feat` | `feat(ANDES): HU06 api y repositorio de detalle de coleccionista` |
| `feat` | `feat(ANDES): HU06 pantalla de detalle de coleccionista` |
| `test` | `test(ANDES): CollectorDetailViewModel carga por id y reintenta` |
| `refactor` | `refactor(ANDES): UiState.Error con tipo de error` |
| `docs` | `docs(ANDES): trazabilidad y diagrama de clases HU06` |

Sugerencia de cortes: (1) data + domain, (2) ViewModel + pantalla + navegación, (3) pruebas, (4) docs. Cada commit debe compilar.

Haz commit o push solo si el usuario lo pide. Al final, propone los mensajes de commit y el comando de push.

## Documentación que se actualiza con cada HU

Todo en `docs/App-technical-documentation.md`:

1. **Mapa de trazabilidad**: confirma que Pantalla, ViewModel, Caso de uso, Repository · API y Endpoint coinciden con lo implementado. Si cambiaste un nombre, actualiza la fila.
2. **Navegación** (párrafo bajo el mapa): mueve la ruta nueva de «Faltan» a «Hoy existen».
3. **Consumo de la API REST**: en la tabla de endpoints, cambia «Por agregar» a «Existe»; actualiza el conteo «el código ya consume N».
4. **Modelo de dominio** (diagrama Mermaid `classDiagram`): quita la marca `<<HUxx · por agregar>>` de las clases que ya existen y agrega las nuevas.
5. **Diagrama de paquetes**: agrega las carpetas nuevas de `ui` (por ejemplo `collector/detail`) y las clases nuevas de `domain/model`.
6. **Reparto del trabajo**: cambia el Estado de la tarea a «En revisión».

Si agregaste errores por tipo, actualiza también la sección «Manejo de errores» (de propuesta a implementado).

No toques `README.md` salvo que cambien los pasos para construir o correr la app.

## Checklist del pull request

Revísala de verdad antes de declarar el feature terminado. Para las reglas de imports, comprueba con grep:

```bash
# ui no importa data ni Retrofit
grep -rn "import com.dev.wine.music.app.data\|import retrofit2" app/src/main/java/com/dev/wine/music/app/ui
# domain no importa Android, Compose, Retrofit ni serialización
grep -rn "import android\|import androidx\|import retrofit2\|import kotlinx.serialization" app/src/main/java/com/dev/wine/music/app/domain
```

Ambos comandos deben salir vacíos.

- [ ] La HU usa la pantalla, el ViewModel, el caso de uso, el repositorio y la API de su fila en el mapa de trazabilidad.
- [ ] Ningún archivo de `ui` importa algo de `data`; ningún archivo de `domain` importa Android, Retrofit ni Compose.
- [ ] El ViewModel expone `StateFlow` y no conoce DTO ni URLs.
- [ ] La pantalla tiene su función `Content` sin estado y muestra carga, error y vacío.
- [ ] Las acciones de coleccionista solo aparecen con ese perfil.
- [ ] Hay pruebas del caso de uso y del ViewModel con los repositorios falsos de `fakes`.
- [ ] Los elementos que usan las pruebas tienen `testTag` según la convención de [e2e.md](e2e.md).
- [ ] Hay prueba E2E Compose (`androidTest/.../e2e/HUxx...E2ETest.kt`) y escenario Kraken (`e2e/kraken/features/HUxx-....feature`) del recorrido principal, y pasaron (o se indica qué falta por correr).
- [ ] Si se agregó una clase, ya está en el diagrama de clases.
- [ ] Textos en `strings.xml`; colores y tipografía desde el tema.
- [ ] `./gradlew test` y `./gradlew assembleDebug` pasan.
- [ ] La app compila y corre siguiendo solo el README.

## Pull request

Hacia `develop`, nunca hacia `main`. Antes de abrirlo: `git pull origin develop` en la rama y resolver conflictos. Merge con **squash** después de una aprobación.

Cuerpo sugerido:

```markdown
## HU06 Detalle de coleccionista

**Qué hace:** muestra el coleccionista y los álbumes de su colección con precio y estado.

**Cadena:** CollectorDetailScreen → CollectorDetailViewModel → GetCollectorDetailUseCase → CollectorRepository → CollectorApi (`GET /collectors/{id}`, `GET /collectors/{id}/albums`)

**Pruebas:** GetCollectorDetailUseCaseTest, CollectorDetailViewModelTest, CollectorDetailContentTest

**Checklist:** (pegar la checklist marcada)
```

Abre el PR con `gh pr create --base develop` solo si el usuario lo pide.
