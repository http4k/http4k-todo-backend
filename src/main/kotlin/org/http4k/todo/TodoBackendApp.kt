package org.http4k.todo

import org.http4k.core.HttpMessage
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters.Cors
import org.http4k.format.Jackson.asA
import org.http4k.format.Jackson.asJsonString
import org.http4k.routing.by
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0] else "5000"
    val baseUrl = if (args.size > 1) args[1] else "http://localhost:$port"
    val todos = TodoDatabase(baseUrl)

    DebuggingFilters
        .PrintRequestAndResponse()
        .then(Cors(UnsafeGlobalPermissive))
        .then(routes(
            OPTIONS to "/{any:.*}" by { _: Request -> Response(OK) },
            GET to "/{id:.+}" by { request: Request -> todos.find(idFromPath(request))?.let { Response(OK).body(it.asJsonString()) } ?: Response(NOT_FOUND) },
            GET to "/" by { _: Request -> Response(OK).body(todos.all().asJsonString()) },
            POST to "/" by { request: Request -> Response(OK).body(todos.save(null, request.todoEntry()).asJsonString()) },
            PATCH to "/{id:.+}" by { request: Request -> Response(OK).body(todos.save(idFromPath(request), request.todoEntry()).asJsonString()) },
            DELETE to "/{id:.+}" by { request: Request -> todos.delete(idFromPath(request))?.let { Response(OK).body(it.asJsonString()) } ?: Response(NOT_FOUND) },
            DELETE to "/" by { _: Request -> Response(OK).body(todos.clear().asJsonString()) }
        ))
        .asServer(Jetty(port.toInt())).start().block()
}

private fun idFromPath(request: Request) = request.path("id")!!.replace("/", "") // <- that should not be necessary

data class TodoEntry(val id: String? = null, val url: String? = null, val title: String? = null, val order: Int? = 0, val completed: Boolean? = false)

fun HttpMessage.todoEntry(): TodoEntry = bodyString().asA<TodoEntry>()
