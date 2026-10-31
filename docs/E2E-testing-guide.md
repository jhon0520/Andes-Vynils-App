# Pruebas E2E: Espresso y Kraken

Guía de pruebas de extremo a extremo (E2E) para **Wine Music (Vinilos)**. Explica qué herramienta usar en cada caso, cómo configurarla en este proyecto y cómo escribir y ejecutar las pruebas.

Fuente de Kraken: [thesoftwaredesignlab.github.io/Kraken](https://thesoftwaredesignlab.github.io/Kraken/).

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
11. [Monkey testing y snapshots](#monkey-testing-y-snapshots)
12. [Ejecutar las pruebas](#ejecutar-las-pruebas)
13. [Buenas prácticas](#buenas-prácticas)
14. [Problemas comunes](#problemas-comunes)
15. [Checklist por HU](#checklist-por-hu)

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

**Regla práctica:** cada HU tiene al menos una prueba de Compose UI Test (rápida, en el PR) y los recorridos principales se cubren también con un escenario de Kraken sobre el APK.

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

Requiere el backend corriendo en `http://10.0.2.2:3000/` (ver `API_BASE_URL`).

```kotlin
@RunWith(AndroidJUnit4::class)
class AlbumFlowE2ETest {

    @get:Rule
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

> Si `MainActivity` usa `@AndroidEntryPoint`, la prueba arranca con el grafo real de Hilt. Para reemplazar repositorios por fakes hace falta `hilt-android-testing`, `@HiltAndroidTest` y un runner propio; agréguelo solo cuando se necesite.

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
- Appium.
- Node.js 12 o superior.
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

La app apunta a `http://10.0.2.2:3000/`, que es el `localhost` del computador visto desde el emulador. Levante **BackVynils** antes de correr Kraken; con un dispositivo físico cambie `API_BASE_URL` a la IP del computador.

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
- **Esperas explícitas.** Use `waitForDisplayed` / `waitUntil` en lugar de pausas fijas. Las pausas solo al arrancar la app.
- **Datos conocidos.** Verifique contra los datos semilla del backend o contra valores que la misma prueba creó.
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

---

## Checklist por HU

- [ ] Los elementos que la prueba usa tienen `testTag` según la convención.
- [ ] Prueba Compose UI Test de la pantalla `*Content` (éxito, vacío, error).
- [ ] Feature de Kraken `HUxx-<nombre>.feature` con el recorrido principal.
- [ ] APK de `distributions/` regenerado.
- [ ] `./gradlew connectedDebugAndroidTest` y `npx kraken-node run` pasan.
- [ ] Reporte de Kraken revisado.
