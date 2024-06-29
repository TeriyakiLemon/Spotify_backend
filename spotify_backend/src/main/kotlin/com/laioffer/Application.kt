package com.laioffer

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class Playlist (
    val id: Long,
    val songs: List<Song>
)

@Serializable
data class Song(
    val name: String,
    val lyric: String,
    val src: String,
    val length: String
)


fun main() {
    val server = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
    server.start(wait = true)
}

// extension
fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }

    // TODO: adding the routing configuration here
    // routing(() -> unit)
    routing {
        // get, put, delete, post
        // get(path: String, block: () -> Unit)
        get("/") {
            call.respondText("Hello World!")
        }

        get("/feed") {
            val jsonString = this::class.java.classLoader.getResource("feed.json").readText()
            call.respondText(jsonString)
        }

        get("/playlists") {
            val jsonString = this::class.java.classLoader.getResource("playlists.json").readText()
            call.respondText(jsonString)
        }

        get("/playlist/{id}") {
            val id = call.parameters["id"]
            // String -> List<Playlist> -> List<Playlist>.find(id == "1") -> Playlist

            val jsonString = this::class.java.classLoader.getResource("playlists.json")?.readText()

            jsonString?.let {
                val playlists = Json.decodeFromString(ListSerializer(Playlist.serializer()), it)
                val playlist = playlists.firstOrNull { p ->
                    p.id.toString() == id
                }
                call.respondNullable(playlist)
            }  ?: call.respond("null")
        }

        static("/") {
            staticBasePackage = "static"
            static("songs") {
                resources("songs")
            }
        }
    }
}

fun hello() {

}

