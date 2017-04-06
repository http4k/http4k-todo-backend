package org.reekwest.todo

import org.reekwest.http.core.Method.DELETE
import org.reekwest.http.core.Method.GET
import org.reekwest.http.core.Method.OPTIONS
import org.reekwest.http.core.Method.PATCH
import org.reekwest.http.core.Method.POST
import org.reekwest.http.core.Request
import org.reekwest.http.core.entity
import org.reekwest.http.core.entity.entity
import org.reekwest.http.core.notFound
import org.reekwest.http.core.ok
import org.reekwest.http.jetty.startJettyServer
import org.reekwest.http.routing.by
import org.reekwest.http.routing.path
import org.reekwest.http.routing.routes


fun main(args: Array<String>) {

    val port = if (args.isNotEmpty()) args[0] else "5000"
    val baseUrl = if (args.size > 1) args[1] else "http://localhost:$port"
    val todos = TodoDatabase(baseUrl)

    cors(log(routes(
        OPTIONS to "/{any:.*}" by { _: Request -> ok() },
        GET to "/{id:.+}" by { request: Request -> todos.find(idFromPath(request))?.let { ok().entity(it.toEntity()) } ?: notFound() },
        GET to "/" by { _: Request -> ok().entity(todos.all().toJson()) },
        POST to "/" by { request: Request -> ok().entity(todos.save(null, request.todoEntry()).toEntity()) },
        PATCH to "/{id:.+}" by { request: Request -> ok().entity(todos.save(idFromPath(request), request.todoEntry()).toEntity()) },
        DELETE to "/{id:.+}" by { request: Request -> todos.delete(idFromPath(request))?.let { ok().entity(it.toEntity()) } ?: notFound() },
        DELETE to "/" by { _: Request -> ok().entity(todos.clear().toJson()) }
    ))).startJettyServer(port.toInt())
}

private fun idFromPath(request: Request) = request.path("id")!!.replace("/", "") // <- that should not be necessary

data class TodoEntry(val id: String? = null, val url: String? = null, val title: String? = null, val order: Int? = 0, val completed: Boolean? = false)