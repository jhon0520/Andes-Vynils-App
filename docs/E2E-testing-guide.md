# Pruebas E2E: Espresso y Kraken

Guía de pruebas de extremo a extremo (E2E) para **Wine Music (Vinilos)**. Explica qué herramienta usar en cada caso, cómo configurarla en este proyecto y cómo escribir y ejecutar las pruebas.

Fuente de Kraken: [thesoftwaredesignlab.github.io/Kraken](https://thesoftwaredesignlab.github.io/Kraken/).

¿Solo necesita escribir las pruebas de una HU? Empiece por la [guía rápida](E2E-guia-rapida.md).

---

## Contenido

1. [Qué herramienta usar](#qué-herramienta-usar)
2. [Espresso y Compose UI Test](#espresso-y-compose-ui-test)
3. [Kraken: conceptos](#kraken-conceptos)
4. [Kraken: instalación](#kraken-instalación)
5. [Kraken: configuración para Vinilos](#kraken-configuración-para-vinilos)
6. [Hacer la app visible para Kraken (Compose)](#hacer-la-app-visible-para-kraken-compose)
7. [Escribir features en Gherkin](#escribir-features-en-gherkin)
8. [Step definitions propios](#step-definitions-propios)
9. [Señales entre dispositivos](#señales-entre-dispositivos)
10. [Propiedades y datos falsos](#propiedades-y-datos-falsos)
11. [Datos de prueba y estado del backend](#datos-de-prueba-y-estado-del-backend)
12. [Monkey testing y snapshots](#monkey-testing-y-snapshots)
13. [Ejecutar las pruebas](#ejecutar-las-pruebas)
14. [Buenas prácticas](#buenas-prácticas)
15. [Problemas comunes](#problemas-comunes)
16. [Checklist por HU](#checklist-por-hu)

---

## Qué herramienta usar

| | Espresso / Compose UI Test | Kraken |
|---|---|---|
| Tipo de prueba | Caja blanca: corre dentro del proceso de la app | Caja negra: maneja el APK desde fuera |
| Lenguaje | Kotlin, en `app/src/androidTest` | Gherkin (`.feature`) + JavaScript |
| Necesita el código fuente | Sí | No, solo el APK |
| Motor | AndroidJUnitRunner | Appium + WebdriverIO + Cucumber |
| Puntos fuertes | Rápido, estable, sincroniza solo con la UI | Escenarios legibles, varios usuarios o dispositivos a la vez, web + móvil |
| Usarlo para | Pantallas y flujos de una HU, con o sin backend | Recorridos completos sobre el APK instalado, con el backend real |

**Regla práctica:** cada HU tiene una prueba `*ContentTest` (estados de la pantalla), una `HUxx<Nombre>E2ETest` (recorrido con el backend real), una `HUxx<Nombre>ErrorE2ETest` (errores con backend falso) y **un escenario de Kraken con su recorrido principal**, aunque la HU sea de un solo usuario. Las señales de Kraken se agregan solo cuando la HU involucra a varios usuarios.

> La app está hecha solo con Jetpack Compose. Las vistas Compose no son `View` clásicas, así que en vez de `onView(withId(...))` de Espresso se usa **Compose UI Test** (`createComposeRule`, `onNodeWithText`), que corre sobre la misma infraestructura de Espresso. Espresso clásico solo hace falta si se agregan pantallas con XML.

---

## Espresso y Compose UI Test

### Dependencias

Ya están en `app/build.gradle.kts`:

```kotlin
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.ui.test.junit4)
debugImplementation(libs.androidx.ui.test.manifest)
```

### Dos niveles de prueba

1. **Pantalla sin estado (`*Content`)**: se le pasa un `UiState` fijo. No necesita Hilt ni red. Es el patrón de `AlbumListContentTest`.
2. **Flujo completo (`MainActivity`)**: arranca la app real con navegación y backend. Es una prueba E2E.

### Ejemplo 1: pantalla sin estado

```kotlin
@RunWith(AndroidJUnit4::class)
class AlbumListContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsAlbumsAndReportsClicks() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumListContent(
                    uiState = UiState.Success(listOf(album)),
                    onAlbumClick = { clickedId = it },
                    onRetry = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Buscando América").assertIsDisplayed().performClick()

        assertEquals(7, clickedId)
    }
}
```

### Ejemplo 2: flujo E2E sobre `MainActivity`

Requiere el backend corriendo en el dominio de `api.base.url` (`gradle.properties`, por defecto `https://backvynils-q6yc.onrender.com/`). Para correrlas contra un backend local, ver «Cambiar el dominio de la API» en el README.

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU02AlbumDetailE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun opensAlbumDetailFromList() {
        // Espera a que la red responda (máx. 10 s)
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText("Buscando América").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Buscando América").performClick()

        composeTestRule.onNodeWithText("Tracks").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Volver").performClick()
        composeTestRule.onNodeWithText("Albumes").assertIsDisplayed()
    }
}
```

> Las pruebas instrumentadas corren con `HiltTestRunner`. Toda prueba que abra `MainActivity` (que es `@AndroidEntryPoint`) debe llevar `@HiltAndroidTest` y `HiltAndroidRule` en `order = 0`; si no, falla al arrancar. Sin `@UninstallModules`, la prueba usa los módulos reales (backend real). Las pruebas `*Content` con `createComposeRule()` no necesitan nada de esto.

### Ejemplo 3: camino de error con un backend falso

Las pruebas `*Content` ya cubren cómo se dibujan los estados de error. Estas pruebas cubren el flujo completo: ViewModel → caso de uso → repositorio → Retrofit → pantalla de error → "Reintentar". La app corre igual que en producción; solo cambia la URL, que apunta a un servidor local (`FakeBackendRule`, con MockWebServer) que falla a propósito.

Piezas (ya están en el repo):

| Pieza | Dónde | Para qué |
|---|---|---|
| `HiltTestRunner` | `app/src/androidTest/.../HiltTestRunner.kt` | Runner de instrumentación que usa `HiltTestApplication` (configurado en `app/build.gradle.kts`) |
| `ApiConfigModule` + `@ApiBaseUrl` | `app/src/main/.../di/ApiConfigModule.kt` | Módulo de Hilt que solo provee la URL; la prueba lo desinstala y pone otra |
| `FakeBackendRule` | `androidTest/.../e2e/FakeBackendRule.kt` | Servidor falso: `respondJson`, `respondStatus(path, code)`, `respondNetworkError` |
| `FakeBackendData` | `androidTest/.../e2e/FakeBackendData.kt` | JSON pequeños con la forma de los datos semilla |

```kotlin
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU01AlbumCatalogErrorE2ETest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val backend = FakeBackendRule()
    @get:Rule(order = 2) val composeTestRule = createEmptyComposeRule()

    @BindValue @field:ApiBaseUrl @JvmField
    val baseUrl: String = backend.baseUrl

    @Test
    fun serverErrorShowsTheErrorAndRetryLoadsTheCatalog() {
        backend.respondStatus("/albums", 500)
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = 15_000)

        backend.respondJson("/albums", FakeBackendData.albums)
        composeTestRule.onNodeWithTag("retry_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = 15_000)
    }
}
```

Reglas:

- Configure las respuestas **antes** de abrir la app (`ActivityScenario.launch`); por eso se usa `createEmptyComposeRule()` y no `createAndroidComposeRule<MainActivity>()`.
- Cada ruta responde lo mismo hasta que la prueba lo cambie; las rutas sin respuesta devuelven 404.
- Use `@UninstallModules` en la clase, **no** `@TestInstallIn`: `@TestInstallIn` reemplaza el módulo en todas las pruebas del source set, incluidas las que usan el backend real.
- Una clase `HUxx<Nombre>ErrorE2ETest` por pantalla con datos: error del servidor + reintento, sin conexión (mensaje de red), lista vacía y, en detalles, 404 sin reintento.
- No necesitan BackVynils encendido: solo el emulador.

### API básica de Compose UI Test

| Acción | Código |
|---|---|
| Buscar por texto | `onNodeWithText("Albumes")` |
| Buscar por tag | `onNodeWithTag("album_list")` |
| Buscar por descripción | `onNodeWithContentDescription("Volver")` |
| Varios nodos | `onAllNodesWithText(...)` |
| Clic | `.performClick()` |
| Escribir | `.performTextInput("Rubén")` |
| Scroll a un nodo | `onNodeWithTag("album_list").performScrollToNode(hasText("..."))` |
| Visible | `.assertIsDisplayed()` |
| No existe | `.assertDoesNotExist()` |
| Texto exacto | `.assertTextEquals("...")` |
| Esperar | `composeTestRule.waitUntil(timeoutMillis) { ... }` |
| Imprimir el árbol | `onRoot().printToLog("TREE")` |

### Nombres de las pruebas

| Prueba | Archivo | Backend |
|---|---|---|
| Pantalla sin estado | `app/src/androidTest/.../ui/<feature>/<Pantalla>ContentTest.kt` | No usa |
| Recorrido E2E de la HU | `app/src/androidTest/.../e2e/HUxx<Nombre>E2ETest.kt` (ej. `HU01AlbumCatalogE2ETest`) | BackVynils real |
| Caminos de error de la HU | `app/src/androidTest/.../e2e/HUxx<Nombre>ErrorE2ETest.kt` (ej. `HU01AlbumCatalogErrorE2ETest`) | `FakeBackendRule` |
| Escenario Kraken | `e2e/kraken/features/HUxx-<nombre>.feature` (ej. `HU01-album-catalog.feature`) | BackVynils real |

Métodos en inglés y en forma de frase (`searchByPerformerShowsOnlyTheirAlbums`); features y escenarios en español.

### Ejecutar

```bash
# Con un emulador o dispositivo conectado
./gradlew connectedDebugAndroidTest

# Una sola clase
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.dev.wine.music.app.ui.album.AlbumListContentTest
```

Reporte HTML: `app/build/reports/androidTests/connected/index.html`.

---

## Kraken: conceptos

Kraken es una herramienta open source de **The Software Design Lab (Universidad de los Andes)** para pruebas E2E automatizadas de apps Android y web. Su foco son los escenarios donde **dos o más usuarios interactúan** a través de la app (por ejemplo, un pasajero y un conductor). Es de **caja negra**: recibe un APK o una URL, no el código.

- Usa **Appium** y **WebdriverIO** para manejar cada dispositivo o emulador.
- Usa **Cucumber** para ejecutar escenarios escritos en **Gherkin**.
- Coordina los dispositivos con un protocolo de **señales** basado en archivos.

Vocabulario:

| Término | Significado |
|---|---|
| Feature | Un archivo `.feature`: una prueba |
| Scenario | Un caso de prueba que corre en **un** dispositivo |
| `@userN` | Identifica el dispositivo o usuario N (1..N) |
| `@mobile` / `@web` | Tipo de dispositivo del escenario |
| Step | Una línea `Given` / `When` / `Then`, implementada en JavaScript |
| Señal | Mensaje que un usuario deja en la bandeja de otro |

---

## Kraken: instalación

### Requisitos

- Android SDK con `adb` y `aapt` en el `PATH`.
- Appium (viene como `devDependency` en `e2e/kraken/package.json`).
- Node.js 18 o superior; el equipo usa **Node 20** (`e2e/kraken/.nvmrc`). Con Node 20 o superior corra Kraken con `NODE_OPTIONS=--no-deprecation` (ver [Problemas comunes](#problemas-comunes)).
- Java (JDK).
- Un emulador o dispositivo encendido (`adb devices` debe listarlo).

Variables de entorno típicas en macOS (`~/.zshrc`):

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0"
export JAVA_HOME="$(/usr/libexec/java_home)"
```

### Crear el proyecto Kraken

Se propone una carpeta `e2e/kraken` en la raíz del repo, separada de Gradle:

```bash
mkdir -p e2e/kraken && cd e2e/kraken
npm init -y
npm install kraken-node --save
npm install -g appium        # si no está instalado
npx kraken-node doctor       # revisa adb, aapt, appium, java
npx kraken-node gen          # genera el esqueleto
```

Estructura generada:

```
e2e/kraken
├── features
│   ├── mobile
│   │   ├── step_definitions
│   │   │   └── step.js
│   │   └── support
│   │       ├── hooks.js
│   │       └── support.js
│   ├── web
│   │   ├── step_definitions
│   │   │   └── step.js
│   │   └── support
│   │       ├── hooks.js
│   │       └── support.js
│   └── my_first.feature
├── mobile.json
└── package.json
```

Agregue `node_modules/` y `reports/` de esta carpeta al `.gitignore`.

---

## Kraken: configuración para Vinilos

### `mobile.json`

Kraken necesita el APK, el paquete y la actividad de arranque.

```json
{
  "type": "singular",
  "apk_path": "../../distributions/app-debug.apk",
  "apk_package": "com.dev.wine.music.app",
  "apk_launch_activity": "com.dev.wine.music.app.MainActivity"
}
```

Para un APK de terceros, Kraken obtiene paquete y actividad con:

```bash
npx kraken-node apk-info <APK_PATH>
```

Si cada usuario usa un APK distinto:

```json
{
  "type": "multiple",
  "@user1": {
    "apk_path": "<APK_PATH>",
    "apk_package": "<APK_PACKAGE>",
    "apk_launch_activity": "<APK_LAUNCH_ACTIVITY>"
  },
  "@user2": {
    "apk_path": "<APK_PATH>",
    "apk_package": "<APK_PACKAGE>",
    "apk_launch_activity": "<APK_LAUNCH_ACTIVITY>"
  }
}
```

### Generar el APK

```bash
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk distributions/app-debug.apk
```

### Backend

La app apunta a `api.base.url` (por defecto `https://backvynils-q6yc.onrender.com/`). Para un backend local, levante **BackVynils** y cambie el dominio en `local.properties` como indica «Cambiar el dominio de la API» en el README (`http://10.0.2.2:3000/` en emulador, `http://localhost:3000/` con `adb reverse` o la IP del computador en un teléfono).

### Navegador (escenarios `@web`)

Por defecto se usa Chrome con ChromeDriver. Para Firefox (Geckodriver), en `features/web/support/hooks.js`:

```javascript
this.deviceClient = new WebClient('firefox', {}, this.userId);
```

---

## Hacer la app visible para Kraken (Compose)

Kraken usa Appium (UiAutomator2), que lee el árbol de accesibilidad. En Compose:

- **El texto visible** (`Text`) se puede encontrar por texto sin cambios.
- **Los ids** no existen por defecto. Para buscar por id hay que exponer los `testTag` como *resource-id*.

En la raíz de la UI (por ejemplo en `MainScreen` o en `setContent` de `MainActivity`):

```kotlin
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

@OptIn(ExperimentalComposeUiApi::class)
Scaffold(
    modifier = Modifier.semantics { testTagsAsResourceId = true },
    // ...
)
```

Y en cada elemento que la prueba necesite:

```kotlin
LazyColumn(modifier = Modifier.testTag("album_list")) { ... }
AlbumCard(modifier = Modifier.testTag("album_item_${album.id}"), ...)
```

Convención de tags propuesta (`snake_case`, `pantalla_elemento`):

| Pantalla | Tags |
|---|---|
| Navegación | `nav_albums`, `nav_artists`, `nav_collectors` |
| Lista de álbumes | `album_list`, `album_item_<id>` |
| Detalle de álbum | `album_detail`, `album_tracks`, `back_button` |
| Artistas | `artist_list`, `artist_item_<id>` |
| Coleccionistas | `collector_list`, `collector_item_<id>` |
| Estados | `loading_view`, `error_view`, `retry_button`, `empty_view` |

Los mismos tags sirven en Compose UI Test (`onNodeWithTag`), así que ambas herramientas comparten selectores.

Para ver qué expone la app use el paso de snapshot de Kraken ([ver abajo](#monkey-testing-y-snapshots)) o **Appium Inspector**.

---

## Escribir features en Gherkin

Reglas de Kraken:

- Cada escenario lleva `@userN` y `@mobile` o `@web`.
- Cada escenario corre en un dispositivo; todos los escenarios de un feature corren en paralelo.

Archivo `features/albums.feature` (un usuario):

```gherkin
Feature: HU01 - Consultar el catálogo de álbumes

  @user1 @mobile
  Scenario: Como visitante veo el catálogo y abro el detalle de un álbum
    Given I wait for 3 seconds
    Then I should see the text "Albumes"
    When I tap the text "Buscando América"
    And I wait for 2 seconds
    Then I should see the text "Tracks"
    When I tap the element with id "back_button"
    Then I should see the text "Albumes"
```

Archivo `features/navigation.feature`:

```gherkin
Feature: Navegación entre secciones

  @user1 @mobile
  Scenario: Como visitante recorro las tres secciones
    Given I wait for 3 seconds
    When I tap the text "Artistas"
    Then I should see the element with id "artist_list"
    When I tap the text "Coleccionistas"
    Then I should see the element with id "collector_list"
    When I tap the text "Albumes"
    Then I should see the element with id "album_list"
```

Kraken trae pasos ya implementados para móvil y web; la lista completa está en `src/steps/mobile.ts` y `src/steps/web.ts` del [repositorio de Kraken](https://github.com/TheSoftwareDesignLab/Kraken). Los pasos de los ejemplos anteriores se definen en la siguiente sección para no depender de la redacción exacta de esos archivos.

---

## Step definitions propios

`features/mobile/step_definitions/step.js`:

```javascript
const { Given, When, Then } = require('@cucumber/cucumber');
const assert = require('assert');

// Selectores de UiAutomator2
const byText = (text) => `android=new UiSelector().text("${text}")`;
const byId = (id) => `android=new UiSelector().resourceId("${id}")`;

Given('I wait for {int} seconds', async function (seconds) {
  await this.driver.pause(seconds * 1000);
});

When('I tap the text {kraken-string}', async function (text) {
  const el = await this.driver.$(byText(text));
  await el.waitForDisplayed({ timeout: 10000 });
  await el.click();
});

When('I tap the element with id {kraken-string}', async function (id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: 10000 });
  await el.click();
});

When('I type {kraken-string} into the element with id {kraken-string}', async function (value, id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: 10000 });
  await el.setValue(value);
});

Then('I should see the text {kraken-string}', async function (text) {
  const el = await this.driver.$(byText(text));
  assert.ok(await el.waitForDisplayed({ timeout: 10000 }));
});

Then('I should see the element with id {kraken-string}', async function (id) {
  const el = await this.driver.$(byId(id));
  assert.ok(await el.waitForDisplayed({ timeout: 10000 }));
});

Then('I should not see the text {kraken-string}', async function (text) {
  const el = await this.driver.$(byText(text));
  assert.ok(await el.waitForDisplayed({ timeout: 5000, reverse: true }));
});
```

Notas:

- Use `{kraken-string}` en lugar de `{string}` cuando el paso deba aceptar **propiedades** (`<PASSWORD>`) o **datos falsos** (`$name_1`). Kraken los resuelve antes de pasar el valor.
- `this.driver` es la sesión de WebdriverIO del dispositivo del escenario.
- Si un paso propio tiene el mismo texto que un paso de Kraken, Cucumber falla por ambigüedad: cambie la redacción.
- Kraken no hace scroll. Para verificar o tocar un ítem de una lista use primero `I scroll to the text "<texto>" in the element with id "<lista>"` (usa `UiScrollable` sobre el `resource-id` de la lista; por id evita que tome la fila de pestañas, que también es desplazable).
- Para filtros y pestañas están también `the element with id "<tab>" should be selected` y `I should not see the element with id "<id>"`. El paso completo está en `e2e/kraken/features/mobile/step_definitions/step.js`.
- Con `testTagsAsResourceId` el `resource-id` es el tag tal cual (`album_list`). Si no lo encuentra, revise el snapshot: algunas versiones lo prefijan con el paquete.

Ejemplo en web, el de la documentación de Kraken:

```javascript
When('I navigate to page {kraken-string}', async function (page) {
  return await this.driver.url(page);
});
```

---

## Señales entre dispositivos

Las señales son la característica central de Kraken. Cada dispositivo tiene una bandeja de entrada; un usuario escribe una señal y otro la espera antes de continuar.

Pasos incluidos:

```
Then I send a signal to user <N> containing "<texto>"
Then I wait for a signal containing "<texto>"                     # timeout por defecto: 10 s
Then I wait for a signal containing "<texto>" for <N> seconds
```

Funciones internas, para usarlas en steps propios:

| Función | Qué hace |
|---|---|
| `writeSignal(signal)` | Escribe la señal en la bandeja del dispositivo |
| `readSignal(content, timeout)` | Espera una señal con ese contenido; lanza excepción al pasar el timeout |

Ejemplo de la documentación (móvil + web):

```gherkin
Feature: Example feature

  @user1 @mobile
  Scenario: As a first user I say hi to a second user
    Given I wait
    Then I send a signal to user 2 containing "hi"

  @user2 @web
  Scenario: As a second user I wait for user 1 to say hi
    Given I wait for a signal containing "hi"
    Then I wait
```

Aplicado a Vinilos, cuando existan HU de escritura (por ejemplo, crear un álbum):

```gherkin
Feature: Un álbum creado aparece en otro dispositivo

  @user1 @mobile
  Scenario: El coleccionista crea un álbum
    Given I wait for 3 seconds
    When I tap the element with id "add_album_button"
    And I type "$name_1" into the element with id "album_name_input"
    And I tap the element with id "save_button"
    Then I send a signal to user 2 containing "album_created"

  @user2 @mobile
  Scenario: Otro visitante ve el álbum nuevo
    Given I wait for a signal containing "album_created" for 30 seconds
    When I tap the element with id "retry_button"
    Then I should see the element with id "album_list"
```

> El valor falso `$name_1` vive en el escenario de user1; para verificarlo en user2 hay que enviarlo en la señal o usar una propiedad fija.

Para varios usuarios móviles se necesitan tantos emuladores o dispositivos como `@userN` (`adb devices`).

---

## Propiedades y datos falsos

### `properties.json`

Valores sensibles o de entorno (no los suba si son secretos). Va en la raíz de `e2e/kraken`:

```json
{
  "PASSWORD": "test",
  "API_KEY": "test2",
  "ALBUM_NAME": "Buscando América"
}
```

Uso en un escenario, con `< >`:

```gherkin
@user1 @mobile
Scenario: Veo un álbum semilla
  Given I wait for 3 seconds
  Then I should see the text "<ALBUM_NAME>"
```

### Datos falsos (faker)

Kraken usa `@faker-js/faker`. Tipos: `name`, `number`, `email`, `string`, `string_date`, `url`.

| Formato | Significado |
|---|---|
| `$name_1` | Genera un nombre y lo guarda con id `1` |
| `$$name_1` | Reutiliza el valor guardado con id `1` |

```gherkin
@user1 @mobile
Scenario: Crear y verificar
  Given I wait
  Then I enter text "$name_1" into field with id "view"
  Then I press "add_button"
  Then I should see "$$name_1"
```

---

## Datos de prueba y estado del backend

Las E2E corren contra el BackVynils real. HU01–HU05 solo leen, pero las HU de escritura (crear álbum, agregar track, asociar artista o premio, comentar) **dejan datos en la base**, y esos datos pueden romper otras pruebas o la misma prueba en la siguiente corrida. Tenga en cuenta estas reglas al planear y escribir cada prueba E2E.

### 1. Lo que la prueba crea lleva un nombre único

Si la prueba crea "Mi álbum", la segunda corrida encuentra dos y `tap the text "Mi álbum"` no sabe cuál tocar (o el backend rechaza el duplicado).

- **Kraken:** genere el valor con faker y reutilícelo para verificar.

  ```gherkin
  When I type "$name_1" into the element with id "album_name_input"
  And I tap the element with id "album_save_button"
  Then I scroll to the text "$$name_1" in the element with id "album_list"
  ```

- **Compose:** agregue una marca de tiempo.

  ```kotlin
  val albumName = "E2E ${System.currentTimeMillis()}"
  ```

- Verifique **solo lo que la prueba creó** (por su nombre único), no el total de la lista.

### 2. Verificaciones que sigan valiendo cuando el backend tiene más datos

Una prueba de lectura debe pasar igual con los datos semilla que con los datos semilla más lo que crearon otras pruebas.

| Evitar | Preferir |
|---|---|
| "El primer álbum es X" | "El álbum X existe" (con scroll hasta él) |
| "La lista tiene 4 álbumes" | "Todos los ítems mostrados cumplen el filtro" (`waitUntilEveryItemMatches`) |
| "El filtro Rock + 'ruben' queda vacío" (deja de ser cierto si alguien crea un álbum de rock de Rubén) | Una búsqueda que nada puede cumplir (`NO_MATCH_QUERY = "zzz-e2e-no-results"`) para probar "sin resultados" |
| Un id fijo de un registro creado por otra prueba | Ids de los datos semilla (desde `100`) o el nombre único de lo que la prueba creó |
| Contar reseñas, tracks o artistas de un álbum semilla | Verificar que el elemento esperado aparece |

Aplica igual en Compose (`assertCountEquals`, `onFirst()`) y en Kraken (`I should see the text` sobre una posición).

### 3. Volver a los datos semilla antes de la corrida oficial

BackVynils tiene `dropSchema: false`: **reiniciarlo no borra los datos**. La migración de datos semilla corre una sola vez por base. Para empezar limpio hay que borrar la base:

```bash
# Con Docker (desde la carpeta de BackVynils): borra el volumen de PostgreSQL y vuelve a crear todo
docker-compose down -v
docker-compose up --build

# Con PostgreSQL local
dropdb vinyls && createdb vinyls
npm run start
```

Compruebe que quedó limpio: `curl http://localhost:3000/albums` debe devolver los 4 álbumes semilla. Haga esto antes de la corrida que se reporta en la wiki de cada entrega.

### 4. Separar pruebas de lectura y de escritura

- **Lectura** (listados y detalles): solo datos semilla, con las verificaciones del punto 2.
- **Escritura** (formularios y asociaciones): nombres únicos y verificación solo de lo creado.

Así una corrida de escritura no rompe las de lectura, y el orden en que corren no importa.

---

## Monkey testing y snapshots

Eventos aleatorios para buscar fallos (crashes, ANR):

```
Then I start a monkey with <N> events          # ADB monkey, solo móvil
Then I start kraken monkey with <N> events     # monkey de Kraken: botones, vistas clicables e inputs; móvil y web
```

Guardar el XML de la vista actual (para descubrir ids o verificar el estado tras una acción):

```
Then I save device snapshot in file with path "<ruta>"
```

Escenario de humo sugerido:

```gherkin
Feature: Estabilidad

  @user1 @mobile
  Scenario: La app resiste 200 eventos aleatorios
    Given I wait for 3 seconds
    Then I save device snapshot in file with path "./reports/snapshot_inicio.xml"
    Then I start kraken monkey with 200 events
```

---

## Ejecutar las pruebas

Orden completo:

```bash
# 1. Backend arriba (BackVynils en el puerto 3000)

# 2. Emulador encendido
adb devices

# 3. APK actualizado
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk distributions/app-debug.apk

# 4. Kraken
cd e2e/kraken
npx kraken-node doctor
npx kraken-node run
```

Comandos de Kraken:

| Comando | Uso |
|---|---|
| `npx kraken-node doctor` | Revisa los requisitos |
| `npx kraken-node gen` | Genera el esqueleto |
| `npx kraken-node apk-info <APK>` | Muestra paquete y actividad de un APK |
| `npx kraken-node run` | Ejecuta los features |

Kraken genera un reporte HTML por ejecución (en la carpeta `reports/`), con capturas por paso.

---

## Buenas prácticas

- **Un escenario, un objetivo.** Nombre el feature con la HU (`HU01 - ...`) para trazarlo.
- **Selectores estables.** Prefiera `testTag` a textos; los textos cambian con traducciones. Use texto cuando lo que se verifica es justamente el contenido.
- **Primero algo positivo, después la ausencia.** "No existe" o "no se ve" pasan solos si el ítem está fuera de pantalla (una lista *lazy* no compone lo que no se ve) o si la UI aún no se dibuja. En Compose use `waitUntilEveryItemMatches` (`e2e/E2EMatchers.kt`), que espera a que todos los ítems mostrados cumplan la condición, o `assertIsSelected` sobre la pestaña. En Kraken deje el ítem en pantalla antes de filtrar, o verifique primero el estado nuevo.
- **Esperas explícitas.** Use `waitForDisplayed` / `waitUntil` en lugar de pausas fijas. Las pausas solo al arrancar la app.
- **Datos conocidos.** Verifique contra los datos semilla del backend o contra valores que la misma prueba creó, nunca contra conteos ni posiciones ([ver Datos de prueba](#datos-de-prueba-y-estado-del-backend)).
- **Independencia.** Cada escenario debe poder correr solo; no dependa del orden.
- **Pruebas rápidas en el PR.** Compose UI Test con `*Content` en cada PR; Kraken antes de cerrar un sprint o una entrega.
- **Mismos tags en ambas herramientas.** Un `testTag` nuevo sirve para Compose UI Test y para Kraken.

---

## Problemas comunes

| Síntoma | Causa probable | Solución |
|---|---|---|
| `doctor` no encuentra `aapt` | `build-tools` fuera del `PATH` | Agregar `$ANDROID_HOME/build-tools/<versión>` |
| Kraken no encuentra un elemento por id | Compose no expone los tags | `testTagsAsResourceId = true` en la raíz |
| La app muestra "Algo salió mal." | Backend apagado o URL errada | Levantar BackVynils; revisar `API_BASE_URL` |
| `Ambiguous step definitions` | Un paso propio repite el texto de uno de Kraken | Cambiar la redacción del paso propio |
| Escenario `@user2` nunca arranca | Falta un segundo dispositivo | Encender otro emulador |
| Timeout en `wait for a signal` | El otro usuario falló antes de enviar la señal | Revisar el reporte del otro usuario; subir el timeout |
| Compose test no encuentra un texto | La prueba busca el texto en otro idioma | Usar el texto de `strings.xml` (por ejemplo `"Reintentar"`) o un tag |
| `Before` falla con `Error: Timeout` y luego `Cannot read properties of undefined (reading 'deleteSession')` | Con Node 20 o superior, Appium 1.22 escribe un `DeprecationWarning` en stderr y Kraken mata a Appium ante cualquier salida en stderr | Correr con `NODE_OPTIONS=--no-deprecation npx kraken-node run` |
| Kraken usa el teléfono conectado por USB en vez del emulador | Kraken toma el primer dispositivo de `adb devices` | Desconectar el teléfono mientras corre Kraken, o dejar conectado solo el dispositivo de prueba |
| Las pruebas de Compose fallan con `NoSuchMethodException: InputManager.getInstance` | Espresso anterior a 3.7.0 en Android 14 o superior | Usar `espressoCore = "3.7.0"` en `gradle/libs.versions.toml` |

---

## Checklist por HU

- [ ] Los elementos que la prueba usa tienen `testTag` según la convención.
- [ ] Prueba Compose UI Test de la pantalla `*Content` (éxito, vacío, error).
- [ ] `HUxx<Nombre>E2ETest` con `@HiltAndroidTest` y backend real.
- [ ] `HUxx<Nombre>ErrorE2ETest` con `FakeBackendRule`: error + reintento, sin conexión, vacío (y 404 en detalles).
- [ ] Feature de Kraken `HUxx-<nombre>.feature` con el recorrido principal.
- [ ] Las verificaciones no dependen de conteos, orden ni del "primer" elemento.
- [ ] Si la HU crea datos: nombres únicos (`$name_1` / `E2E <timestamp>`) y se verifica solo lo creado.
- [ ] Antes de la corrida oficial, backend reiniciado con base limpia (`docker-compose down -v`).
- [ ] APK de `distributions/` regenerado.
- [ ] `./gradlew connectedDebugAndroidTest` y `npx kraken-node run` pasan.
- [ ] Reporte de Kraken revisado.
