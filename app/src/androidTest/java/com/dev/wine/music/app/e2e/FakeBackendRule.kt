package com.dev.wine.music.app.e2e

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.junit.rules.ExternalResource
import java.util.concurrent.ConcurrentHashMap

/**
 * A fake BackVynils for the error-path E2E tests. The app runs for real (ViewModel, use case,
 * repository, Retrofit, mapper); only the server answers come from here.
 *
 * Each path keeps answering the same until the test changes it, so OkHttp retries and the
 * app's "Reintentar" see a predictable backend. Paths without an answer get a 404.
 *
 * The server starts in the constructor, so `@BindValue @field:ApiBaseUrl val baseUrl = backend.baseUrl`
 * is ready before Hilt builds the app.
 */
class FakeBackendRule : ExternalResource() {

    private val responses = ConcurrentHashMap<String, MockResponse>()

    private val server = MockWebServer().apply {
        dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty().substringBefore('?')
                return responses[path] ?: status(404, "Not found: $path")
            }
        }
        start()
    }

    /** "localhost" is allowed for plain HTTP in network_security_config.xml. */
    val baseUrl: String = "http://localhost:${server.port}/"

    fun respondJson(path: String, body: String) {
        responses[path] = MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody(body)
    }

    /** Answers with an error body shaped like the NestJS one: `{ statusCode, message }`. */
    fun respondStatus(path: String, code: Int, message: String = "Error $code") {
        responses[path] = status(code, message)
    }

    /** Closes the connection without answering, as when there is no network. */
    fun respondNetworkError(path: String) {
        responses[path] = MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
    }

    override fun after() {
        server.shutdown()
    }

    private fun status(code: Int, message: String) = MockResponse()
        .setResponseCode(code)
        .setHeader("Content-Type", "application/json")
        .setBody("""{"statusCode":$code,"message":"$message"}""")
}
