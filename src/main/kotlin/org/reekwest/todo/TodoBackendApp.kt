package org.reekwest.todo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.reekwest.http.core.HttpMessage
import org.reekwest.http.core.Method.DELETE
import org.reekwest.http.core.Method.GET
import org.reekwest.http.core.Method.OPTIONS
import org.reekwest.http.core.Method.PATCH
import org.reekwest.http.core.Method.POST
import org.reekwest.http.core.Request
import org.reekwest.http.core.Response.Companion.notFound
import org.reekwest.http.core.Response.Companion.ok
import org.reekwest.http.core.then
import org.reekwest.http.filters.DebuggingFilters
import org.reekwest.http.routing.by
import org.reekwest.http.routing.path
import org.reekwest.http.routing.routes
import org.reekwest.http.server.startJettyServer
import java.nio.ByteBuffer


fun main(args: Array<String>) {

    val port = if (args.isNotEmpty()) args[0] else "5000"
    val baseUrl = if (args.size > 1) args[1] else "http://localhost:$port"
    val todos = TodoDatabase(baseUrl)

    DebuggingFilters
        .PrintRequestAndResponse()
        .then(cors(routes(
            OPTIONS to "/{any:.*}" by { _: Request -> ok() },
            GET to "/{id:.+}" by { request: Request -> todos.find(idFromPath(request))?.let { ok().body(it.toEntity()) } ?: notFound() },
            GET to "/" by { _: Request -> ok().body(todos.all().toJson()) },
            POST to "/" by { request: Request -> ok().body(todos.save(null, request.todoEntry()).toEntity()) },
            PATCH to "/{id:.+}" by { request: Request -> ok().body(todos.save(idFromPath(request), request.todoEntry()).toEntity()) },
            DELETE to "/{id:.+}" by { request: Request -> todos.delete(idFromPath(request))?.let { ok().body(it.toEntity()) } ?: notFound() },
            DELETE to "/" by { _: Request -> ok().body(todos.clear().toJson()) }
        )))
        .startJettyServer(port.toInt())
}

private fun idFromPath(request: Request) = request.path("id")!!.replace("/", "") // <- that should not be necessary

data class TodoEntry(val id: String? = null, val url: String? = null, val title: String? = null, val order: Int? = 0, val completed: Boolean? = false)

val mapper = jacksonObjectMapper()

fun HttpMessage.todoEntry() = body?.let { mapper.readValue<TodoEntry>(String(it.array())) } ?: throw RuntimeException("could not get todo from entity")
fun TodoEntry.toEntity(): ByteBuffer = ByteBuffer.wrap(mapper.writeValueAsString(this).toByteArray())
fun List<TodoEntry>.toJson(): String = jacksonObjectMapper().writeValueAsString(this)