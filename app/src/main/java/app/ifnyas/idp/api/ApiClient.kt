package app.ifnyas.idp.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

object ApiClient {
    val ktorClient: HttpClient = HttpClient {
        install(JsonFeature) { serializer = KotlinxSerializer() }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        defaultRequest {
            host = "ifnyas-idp.builtwithdark.com/v1"
            url { protocol = URLProtocol.HTTPS }
        }
    }
}