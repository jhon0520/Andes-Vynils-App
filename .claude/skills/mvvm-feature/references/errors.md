# Errores por tipo

Hoy cada ViewModel guarda cualquier excepción en `UiState.Error(message)` y la pantalla muestra `ErrorView` con «Reintentar». El diseño pide distinguir:

| Excepción | Significado | Qué muestra la app |
|---|---|---|
| `IOException` | Sin conexión o servidor caído | `ErrorView` con «Reintentar» |
| `HttpException` 404 | El recurso no existe | «No encontrado», sin reintentar |
| `HttpException` 400 / 412 | El backend rechazó los datos | Mensaje en el formulario, sin borrar lo escrito |
| `HttpException` 500 u otro | Error del servidor | `ErrorView` con «Reintentar» |

Cuerpo de error del backend: `{ "statusCode": 404, "message": "The album with the given id was not found" }`. En un 400, `message` trae el texto de Joi.

## Antes de empezar

1. Busca si ya existe: `grep -rn "ErrorType\|AppError\|safeApiCall" app/src/main/java`.
2. Si existe, úsalo como está y salta a «Uso en cada HU».
3. Si no existe, es la **tarea 4 del reparto** (transversal). Propón al usuario hacerla en su propia rama `feature/errores-por-tipo` antes de la HU. Si prefiere hacerla dentro de la HU, sepárala en un commit `refactor(ANDES): UiState.Error con tipo de error`.

## Diseño: cada capa traduce lo suyo

`ui` no puede importar Retrofit, así que no puede leer `HttpException`. Por eso:

1. **data** traduce `HttpException`/`IOException` a un error de dominio (`AppError`).
2. **domain** define `AppError`, sin dependencias de red.
3. **ui** traduce `AppError` a `ErrorType` para decidir qué pintar.

### 1. domain: `AppError`

```kotlin
// domain/model/AppError.kt
package com.dev.wine.music.app.domain.model

/** Errores que la app sabe manejar. El data layer los lanza; ui decide qué mostrar. */
sealed class AppError(message: String? = null) : Exception(message) {
    class Network : AppError()
    class NotFound(message: String? = null) : AppError(message)
    class Validation(message: String? = null) : AppError(message)
    class Server(message: String? = null) : AppError(message)
}
```

### 2. data: `safeApiCall`

```kotlin
// data/remote/SafeApiCall.kt
package com.dev.wine.music.app.data.remote

import com.dev.wine.music.app.domain.model.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

@Serializable
private data class ApiErrorDto(val statusCode: Int = 0, val message: String = "")

private val errorJson = Json { ignoreUnknownKeys = true; coerceInputValues = true }

/** Ejecuta una llamada de Retrofit y convierte sus fallas en [AppError]. */
suspend fun <T> safeApiCall(block: suspend () -> T): T = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    val message = e.backendMessage()
    throw when (e.code()) {
        404 -> AppError.NotFound(message)
        400, 412 -> AppError.Validation(message)
        else -> AppError.Server(message)
    }
} catch (e: IOException) {
    throw AppError.Network()
} catch (e: SerializationException) {
    throw AppError.Server(e.message)
}

private fun HttpException.backendMessage(): String? {
    val body = response()?.errorBody()?.string() ?: return message()
    return runCatching { errorJson.decodeFromString<ApiErrorDto>(body).message }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: message()
}
```

Los repositorios envuelven cada llamada:

```kotlin
override suspend fun getAlbum(albumId: Int): Album =
    safeApiCall { api.getAlbum(albumId).toDomain() }
```

Al agregar esto, envuelve las llamadas de **todos** los repositorios existentes en el mismo commit, para que el comportamiento sea igual en toda la app.

### 3. ui: `ErrorType` en `UiState`

`message` queda como primer parámetro para no romper los `UiState.Error(e.message)` existentes.

```kotlin
// ui/common/UiState.kt
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(
        val message: String? = null,
        val type: ErrorType = ErrorType.UNKNOWN,
    ) : UiState<Nothing>
}

enum class ErrorType { NETWORK, NOT_FOUND, VALIDATION, SERVER, UNKNOWN }

/** Traduce cualquier excepción a un estado de error para la pantalla. */
fun Throwable.toUiError(): UiState.Error = UiState.Error(
    message = message,
    type = when (this) {
        is AppError.Network -> ErrorType.NETWORK
        is AppError.NotFound -> ErrorType.NOT_FOUND
        is AppError.Validation -> ErrorType.VALIDATION
        is AppError.Server -> ErrorType.SERVER
        else -> ErrorType.UNKNOWN
    },
)
```

`ui` puede importar `domain.model.AppError`: `ui` depende de `domain`, no al revés.

### 4. ui/components: vista según el tipo

`ErrorView` recibe el tipo con valor por defecto, así las pantallas existentes siguen compilando.

```kotlin
@Composable
fun ErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    type: ErrorType = ErrorType.UNKNOWN,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                when (type) {
                    ErrorType.NETWORK -> R.string.error_network
                    ErrorType.NOT_FOUND -> R.string.error_not_found
                    else -> R.string.error_loading
                },
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (type != ErrorType.NOT_FOUND) {
            Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        }
    }
}
```

```xml
<string name="error_network">No hay conexión. Revisa tu red e intenta de nuevo.</string>
<string name="error_not_found">No encontramos lo que buscas.</string>
```

## Uso en cada HU

ViewModel:

```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    e.toUiError()
}
```

Content:

```kotlin
is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type, modifier = modifier)
```

Formularios: un `AppError.Validation` no reemplaza la pantalla; se muestra junto al formulario conservando lo escrito. Ver [forms.md](forms.md).

## Pruebas

- `SafeApiCallTest` (test unitario): 404 → `NotFound`, 400 → `Validation` con el `message` del cuerpo, `IOException` → `Network`.

```kotlin
private fun httpError(code: Int, body: String) = HttpException(
    Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())),
)

@Test
fun `404 becomes NotFound with the backend message`() = runTest {
    val error = runCatching {
        safeApiCall<Unit> { throw httpError(404, """{"statusCode":404,"message":"The album with the given id was not found"}""") }
    }.exceptionOrNull()

    assertTrue(error is AppError.NotFound)
    assertEquals("The album with the given id was not found", error?.message)
}
```

- En los fakes, permite elegir qué error lanzar (ver [testing.md](testing.md)) y prueba que el ViewModel emite el `ErrorType` correcto.
