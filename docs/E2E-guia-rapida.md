# Guía rápida: cómo hacer las pruebas E2E

Resumen práctico para escribir y correr las pruebas de extremo a extremo (E2E) de **Wine Music (Vinilos)**. El detalle completo (instalación de Kraken, señales, faker, problemas comunes) está en [E2E-testing-guide.md](E2E-testing-guide.md).

Una prueba E2E usa la app completa como lo haría una persona: la abre, navega, toca y verifica lo que aparece en pantalla. En el proyecto se usan dos herramientas: **Compose UI Test** (dentro de la app, en Kotlin) y **Kraken** (desde fuera, sobre el APK, en Gherkin).

---

## Las piezas de cada HU

| Prueba | Archivo | Qué prueba | Backend |
|---|---|---|---|
| Pantalla aislada (no es E2E) | `app/src/androidTest/.../ui/<feature>/<Pantalla>ContentTest.kt` | Cómo se dibuja cada estado: carga, error, vacío, datos | Ninguno |
| Recorrido E2E | `app/src/androidTest/.../e2e/HUxx<Nombre>E2ETest.kt` | El camino feliz con datos reales | BackVynils real |
| Errores E2E | `app/src/androidTest/.../e2e/HUxx<Nombre>ErrorE2ETest.kt` | Error 500, sin red, vacío, 404 y Reintentar | Servidor falso (`FakeBackendRule`) |
| Kraken | `e2e/kraken/features/HUxx-<nombre>.feature` | El mismo recorrido, desde fuera del APK | BackVynils real |

Ejemplos en el repo: `AlbumListContentTest`, `HU01AlbumCatalogE2ETest`, `HU01AlbumCatalogErrorE2ETest` y `HU01-album-catalog.feature`.

---

## 1. Preparar la pantalla: `testTag`

Las pruebas encuentran los elementos por su *tag*. Al hacer la pantalla, ponga `Modifier.testTag(...)` en lo que la prueba va a tocar o verificar:

```kotlin
LazyColumn(modifier = Modifier.testTag("album_list")) { ... }
AlbumCard(modifier = Modifier.testTag("album_item_${album.id}"))
```

Convención: `<entidad>_list`, `<entidad>_item_<id>`, `<entidad>_<campo>_input`, `<entidad>_<accion>_button`, `back_button`, `retry_button`, `nav_<entidad>`.

Kraken ve los mismos tags como ids porque `MainScreen` tiene `testTagsAsResourceId = true`. Un tag sirve para las dos herramientas.

---

## 2. Recorrido E2E con Compose (backend real)

Abre `MainActivity` de verdad y habla con BackVynils en `http://10.0.2.2:3000/`.

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU01AlbumCatalogE2ETest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    // La app siempre abre en «¿Cómo quieres ingresar?»: entrar como visitante antes de cada prueba.
    @Before
    fun enterApp() {
        composeTestRule.enterAsVisitor()
    }

    @Test
    fun tappingAnAlbumOpensItsDetail() {
        // 1. Esperar a que llegue la respuesta del backend
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = 15_000)
        // 2. Hacer lo que haría el usuario
        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasText("Buscando América Prueba"))
        composeTestRule.firstNodeWithText("Buscando América Prueba").performClick()
        // 3. Verificar el resultado
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail"), timeoutMillis = 15_000)
    }
}
```

Las tres partes de siempre son **esperar → actuar → verificar**.

Reglas:

- `@HiltAndroidTest` y `HiltAndroidRule` en `order = 0` son obligatorios: el runner es `HiltTestRunner` y sin eso la prueba falla al arrancar.
- Espere con `waitUntil…`, nunca con `Thread.sleep`.
- En listas, haga scroll (`performScrollToNode`) antes de tocar o verificar un ítem.
- El backend es compartido y otros equipos crean álbumes con nombres repetidos (hay muchas copias vacías de «Buscando América»). Use el álbum de referencia «Buscando América Prueba» (id 100, el que tiene a Rubén Blades, canciones y reseñas) y búsquelo con `firstNodeWithText(...)` en lugar de `onNodeWithText(...)`, que falla si hay más de un nodo con ese texto (también pasa dentro del detalle: canciones y autores de reseñas repetidos). En Kraken, los datos están en `e2e/kraken/properties.json`.
- En Kraken, el texto que se escribe en un buscador no debe ser igual al texto que después se toca: `UiSelector().text(...)` encuentra primero el campo de búsqueda y toca ahí. Por eso `ALBUM_QUERY` es «america prueba» y no el nombre completo del álbum.
- En un filtro, verifique primero algo positivo con `waitUntilEveryItemMatches(...)` (de `e2e/E2EMatchers.kt`: todos los ítems mostrados cumplen el filtro) y la pestaña con `assertIsSelected()`. Después verifique que el otro ítem no está. Un "no existe" solo pasa también cuando el ítem está fuera de pantalla.
- Use datos semilla del backend y no dependa de conteos, del orden ni del "primer" elemento.
- Si la HU crea datos, use nombres únicos (`"E2E ${System.currentTimeMillis()}"`) y verifique solo lo que la prueba creó.
- Los textos de la app se leen de recursos (`context.getString(R.string.tracks)`), no se escriben fijos.

---

## 3. Errores E2E (backend falso)

Es la misma app, pero la URL apunta a un servidor local (`FakeBackendRule`, con MockWebServer) que responde lo que la prueba le pida. No necesita BackVynils encendido.

```kotlin
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)          // quita la URL real
@RunWith(AndroidJUnit4::class)
class HU01AlbumCatalogErrorE2ETest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val backend = FakeBackendRule()
    @get:Rule(order = 2) val composeTestRule = createEmptyComposeRule()

    @BindValue @field:ApiBaseUrl @JvmField
    val baseUrl: String = backend.baseUrl          // pone la URL del servidor falso

    @Test
    fun serverErrorThenRetryLoadsTheCatalog() {
        backend.respondStatus("/albums", 500)                  // 1. el backend falla
        ActivityScenario.launch(MainActivity::class.java)      // 2. abrir la app
        composeTestRule.enterAsVisitor()                       //    y entrar como visitante
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = 15_000)

        backend.respondJson("/albums", FakeBackendData.albums) // 3. el backend se recupera
        composeTestRule.onNodeWithTag("retry_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = 15_000)
    }
}
```

| Respuesta | Qué simula |
|---|---|
| `respondJson(path, json)` | Respuesta correcta (los JSON están en `FakeBackendData`) |
| `respondStatus(path, código)` | Error HTTP: 404, 500… |
| `respondNetworkError(path)` | Sin conexión |
| (sin configurar) | 404 |

Reglas:

- Configure las respuestas **antes** de abrir la app; por eso se usa `createEmptyComposeRule()` + `ActivityScenario.launch(...)`. Cierre el escenario en `@After`.
- Casos que debe cubrir cada pantalla: error con reintento, sin conexión (mensaje de red), lista vacía y, en pantallas de detalle, 404 sin botón de reintento.
- Use `@UninstallModules` en la clase, nunca `@TestInstallIn`: ese cambia el backend de **todas** las pruebas, también las del backend real.
- Si la HU usa un endpoint nuevo, agregue su JSON a `FakeBackendData`.

---

> **Onboarding.** Desde la selección de perfil, la app abre en «¿Cómo quieres ingresar?» y no en Álbumes. Toda prueba que arranca la app debe llamar `composeTestRule.enterAsVisitor()` (en `e2e/E2EMatchers.kt`) antes de esperar `album_list`; en Kraken, el primer paso es `When I tap the element with id "role_visitor"`. Las pruebas del propio onboarding están en `Onboarding*E2ETest` y `e2e/kraken/features/ONB-*.feature`.

## 4. Escenario de Kraken

Kraken maneja el APK instalado desde fuera, como un usuario. Se escribe en Gherkin (`Given` / `When` / `Then`).

```gherkin
Feature: HU03 - Consultar el listado de artistas

  @user1 @mobile
  Scenario: Como visitante filtro los artistas por tipo
    Given I wait for 5 seconds
    When I tap the element with id "role_visitor"
    When I tap the element with id "nav_artists"
    Then I should see the element with id "artist_list"
    When I scroll to the text "<MUSICIAN>" in the element with id "artist_list"
    Then I should see the text "<MUSICIAN>"
    When I tap the element with id "artist_type_tab_band"
    Then the element with id "artist_type_tab_band" should be selected
    And I should not see the text "<MUSICIAN>"
```

- Todo escenario lleva `@user1 @mobile`.
- `"<MUSICIAN>"` se toma de `e2e/kraken/properties.json`.
- En formularios, `"$name_1"` genera un dato falso y `"$$name_1"` lo reutiliza para verificarlo.
- Kraken no hace scroll solo: use `I scroll to the text "…" in the element with id "<lista>"` antes de tocar o verificar un ítem.
- `I should not see …` pasa solo si el elemento nunca se dibujó. Úselo después de haber visto el elemento en pantalla o después de una verificación positiva.
- Los pasos están en `e2e/kraken/features/mobile/step_definitions/step.js`. Antes de crear uno, revise si ya existe.
- Si la HU involucra a dos usuarios (uno crea, otro consulta), coordínelos con señales: `Then I send a signal to user 2 containing "album_created"`.

Pasos disponibles:

| Paso | Uso |
|---|---|
| `I wait for {int} seconds` | Pausa (solo al arrancar la app) |
| `I tap the text "…"` / `I tap the element with id "…"` | Tocar |
| `I type "…" into the element with id "…"` | Escribir |
| `I hide the keyboard` | Ocultar el teclado |
| `I scroll to the text "…" in the element with id "…"` | Scroll en una lista |
| `I should see the text "…"` / `I should see the element with id "…"` | Verificar que se ve |
| `I should not see the text "…"` / `I should not see the element with id "…"` | Verificar que no se ve |
| `the element with id "…" should be selected` | Verificar la pestaña seleccionada |

---

## 5. Ejecutar

**Atajo con backend local:** `./scripts/e2e-local.sh` revisa si BackVynils responde en `localhost:3000` (si no, lo levanta con Docker/Colima y carga `sql/data.sql` cuando la base está vacía), pone tu IP en `api.base.url` de `local.properties`, compila el APK, lo copia en `distributions/` y corre Compose y Kraken. Opciones: `--skip-tests`, `--skip-compose`, `--skip-kraken` y `--backend-dir=RUTA` (por defecto `../BackVynils`, al lado de este repo; también con la variable `BACKEND_DIR`); variable `HOST_IP` si la IP detectada no sirve.

**Antes del release en `main`:** `./scripts/e2e-local.sh --release` quita `api.base.url` de `local.properties`, compila y copia en `distributions/` un APK que apunta al dominio por defecto (Render).

Paso a paso:

```bash
# 1. Emulador encendido
adb devices

# 2. Backend arriba (no hace falta para las pruebas de error)
docker-compose up              # desde la carpeta de BackVynils

# 3. Pruebas de Compose (Content + E2E + errores)
./gradlew connectedDebugAndroidTest
#    Reporte: app/build/reports/androidTests/connected/index.html

#    Una sola clase:
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.dev.wine.music.app.e2e.HU01AlbumCatalogE2ETest

# 4. Kraken (Node 20)
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk distributions/app-debug.apk
cd e2e/kraken
NODE_OPTIONS=--no-deprecation npx kraken-node run
#    Reporte: e2e/kraken/reports/
```

Para la corrida oficial de la entrega, deje primero el backend con los datos semilla. Reiniciarlo no borra datos (`dropSchema: false`), así que hay que borrar la base:

```bash
docker-compose down -v && docker-compose up --build
curl http://localhost:3000/albums   # deben salir los 4 álbumes semilla
```

---

## Checklist rápido por HU

- [ ] Tags en la pantalla según la convención.
- [ ] `<Pantalla>ContentTest` con los estados (éxito, vacío, error, carga).
- [ ] `HUxx<Nombre>E2ETest` con el recorrido real.
- [ ] `HUxx<Nombre>ErrorE2ETest` con los errores (reintento, sin conexión, vacío, 404 en detalles).
- [ ] `HUxx-<nombre>.feature` en Kraken con el recorrido principal.
- [ ] Verificaciones sin conteos, orden ni "primer elemento"; datos creados con nombres únicos.
- [ ] APK actualizado en `distributions/`.
- [ ] `./gradlew connectedDebugAndroidTest` y `npx kraken-node run` en verde.
- [ ] Defectos encontrados registrados como issues con la etiqueta `bug`.
