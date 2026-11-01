# Pruebas E2E del feature

Cada HU termina con dos pruebas de extremo a extremo sobre la app real:

| Prueba | Herramienta | Dónde | Qué cubre |
|---|---|---|---|
| Flujo Compose | Compose UI Test (Espresso) con `createAndroidComposeRule<MainActivity>()` | `app/src/androidTest/java/com/dev/wine/music/app/e2e/HUxx<Nombre>E2ETest.kt` | Navegar hasta la pantalla, ver datos del backend, la interacción principal |
| Escenario Kraken | Kraken (Appium + Cucumber, Gherkin) sobre el APK | `e2e/kraken/features/HUxx-<nombre>.feature` | El mismo recorrido como caja negra; varios usuarios si la HU lo pide |

Guía completa (instalación, señales, faker, problemas comunes): `docs/E2E-testing-guide.md`. Léela si es la primera vez que se configura Kraken en el repo.

Ambas pruebas usan el **backend real** (BackVynils en `api.base.url`; por defecto `https://backvynils-q6yc.onrender.com/`) y sus datos semilla. Las pruebas del `Content` con fakes siguen en [testing.md](testing.md); las E2E no las reemplazan.

## 1. Selectores: `testTag` en la pantalla

Kraken (Appium) no ve los `testTag` de Compose como ids salvo que se expongan. Hazlo una sola vez en la raíz de la UI (`MainScreen`), si aún no está:

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
Scaffold(
    modifier = Modifier.semantics { testTagsAsResourceId = true },
    // ...
)
```

Antes de agregarlo, revisa con `grep -rn testTagsAsResourceId app/src/main`.

En el feature, pon `Modifier.testTag(...)` a lo que la prueba toca o verifica. Convención `snake_case`, `<entidad>_<elemento>`:

| Elemento | Tag |
|---|---|
| Lista | `<entidad>_list` |
| Ítem de lista | `<entidad>_item_<id>` |
| Contenedor de detalle | `<entidad>_detail` |
| Campo de formulario | `<entidad>_<campo>_input` |
| Botón principal | `<entidad>_<accion>_button` (ej. `album_save_button`) |
| Volver | `back_button` |
| Pestaña de navegación | `nav_<entidad>` |
| Estados (en `ui/components`) | `loading_view`, `error_view`, `retry_button`, `empty_view` |

Los tags van en el `Content` (o en el componente de `ui/components`), aplicados sobre el `modifier` recibido o en hijos; no cambian la firma de la función. Los mismos tags sirven en Compose UI Test (`onNodeWithTag`) y en Kraken (`resourceId`).

## 2. Flujo Compose sobre `MainActivity`

`MainActivity` es `@AndroidEntryPoint`, así que la prueba usa el grafo real de Hilt y la red. Textos desde recursos, nunca fijos; datos esperados de la semilla del backend.

```kotlin
@RunWith(AndroidJUnit4::class)
class HU02AlbumDetailE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun opensAlbumDetailFromCatalogAndGoesBack() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = 10_000)

        composeTestRule.onAllNodes(hasTestTagPrefix("album_item_")).onFirst().performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail"), timeoutMillis = 10_000)
        composeTestRule.onNodeWithText(context.getString(R.string.tracks)).assertIsDisplayed()

        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.onNodeWithTag("album_list").assertIsDisplayed()
    }

    /** Encuentra ítems por prefijo del tag (`album_item_<id>`) sin depender de un id fijo. */
    private fun hasTestTagPrefix(prefix: String) = SemanticsMatcher("testTag starts with $prefix") {
        it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
    }
}
```

Reglas:

- Espera con `waitUntilAtLeastOneExists` / `waitUntil`, nunca `Thread.sleep`.
- Si la pantalla no es pestaña, navega desde la pestaña inicial como lo haría el usuario.
- En listas largas usa `performScrollToNode(hasText(...))` sobre `<entidad>_list` antes de hacer clic.
- Formularios: escribe con `performTextInput`, guarda y verifica que el nuevo elemento aparece en la lista. Usa un nombre único (`"E2E ${System.currentTimeMillis()}"`) porque los datos quedan en el backend.
- Una clase por HU; uno o dos tests con el recorrido principal. Los casos de error y vacío ya están cubiertos con fakes.

## 3. Escenario Kraken

Si `e2e/kraken` no existe, avisa al usuario y propón crearlo con los pasos de `docs/E2E-testing-guide.md` (`npm install kraken-node`, `npx kraken-node gen`, `mobile.json`). No lo instales sin confirmar.

`mobile.json` del proyecto:

```json
{
  "type": "singular",
  "apk_path": "../../distributions/app-debug.apk",
  "apk_package": "com.dev.wine.music.app",
  "apk_launch_activity": "com.dev.wine.music.app.MainActivity"
}
```

Feature por HU, `e2e/kraken/features/HU02-detalle-album.feature`:

```gherkin
Feature: HU02 - Consultar el detalle de un álbum

  @user1 @mobile
  Scenario: Como visitante abro un álbum del catálogo y vuelvo
    Given I wait for 3 seconds
    Then I should see the element with id "album_list"
    When I tap the text "<ALBUM_NAME>"
    Then I should see the element with id "album_detail"
    And I should see the text "Tracks"
    When I tap the element with id "back_button"
    Then I should see the element with id "album_list"
```

- Cada escenario lleva `@userN` y `@mobile`.
- Valores de la semilla en `e2e/kraken/properties.json` y se usan como `"<CLAVE>"`.
- En formularios usa datos falsos: `"$name_1"` para generar y `"$$name_1"` para verificar el mismo valor.
- Si la HU involucra a dos usuarios (uno crea, otro consulta), usa señales: `Then I send a signal to user 2 containing "album_created"` y `Given I wait for a signal containing "album_created" for 30 seconds`.

Pasos propios en `e2e/kraken/features/mobile/step_definitions/step.js`. Antes de crear uno, revisa si ya existe ahí; reutiliza la redacción. Base:

```javascript
const { Given, When, Then } = require('@cucumber/cucumber');
const assert = require('assert');

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
  assert.ok(await (await this.driver.$(byText(text))).waitForDisplayed({ timeout: 10000 }));
});

Then('I should see the element with id {kraken-string}', async function (id) {
  assert.ok(await (await this.driver.$(byId(id))).waitForDisplayed({ timeout: 10000 }));
});
```

Usa `{kraken-string}` (no `{string}`) para que funcionen `<PROPIEDAD>` y `$faker`. Si un paso propio repite el texto de uno de Kraken, Cucumber falla por ambigüedad: cambia la redacción.

## 4. Ejecutar

Requisitos: backend arriba, emulador encendido (`adb devices`).

```bash
# Flujo Compose del feature
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.dev.wine.music.app.e2e.HU02AlbumDetailE2ETest

# Kraken: APK actualizado y ejecución
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk distributions/app-debug.apk
cd e2e/kraken && npx kraken-node run
```

Reportes: `app/build/reports/androidTests/connected/index.html` y la carpeta `reports/` de Kraken.

Si no hay emulador o backend, no ejecutes: deja las pruebas escritas y dile al usuario qué comandos correr. No declares las E2E aprobadas sin haberlas visto pasar.
