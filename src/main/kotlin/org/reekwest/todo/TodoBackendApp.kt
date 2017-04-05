package org.reekwest.todo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.reekwest.http.core.HttpMessage
import org.reekwest.http.core.Method.DELETE
import org.reekwest.http.core.Method.GET
import org.reekwest.http.core.Method.OPTIONS
import org.reekwest.http.core.Method.POST
import org.reekwest.http.core.Request
import org.reekwest.http.core.entity.Entity
import org.reekwest.http.core.entity.EntityTransformer
import org.reekwest.http.core.entity.entity
import org.reekwest.http.core.entity.extract
import org.reekwest.http.core.notFound
import org.reekwest.http.core.ok
import org.reekwest.http.jetty.startJettyServer
import org.reekwest.http.routing.by
import org.reekwest.http.routing.path
import org.reekwest.http.routing.routes
import java.nio.ByteBuffer
import java.util.*


fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0] else "5000"

    var todos = mutableListOf<TodoEntry>()

    cors(log(routes(
        OPTIONS to "/{any:.*}" by { _: Request -> ok() },
        GET to "/{id:.+}" by { request: Request ->
            val todo = todos.find { it.url.contains(request.path("id")!!) }
            todo?.let { ok(entity = it.toEntity()) } ?: notFound()
        },
        GET to "/" by { _: Request -> ok().entity(todos.toJson()) },
        POST to "/" by { request: Request ->
            val todo = request.todoEntry()
            todos.add(todo)
            ok(entity = todo.toEntity())
        },
        DELETE to "/" by { _: Request ->
            todos.clear()
            ok().entity(todos.toJson())
        }
    ))).startJettyServer(port.toInt())
}

fun List<TodoEntry>.toJson(): String = jacksonObjectMapper().writeValueAsString(this)

data class TodoEntry(val title: String, val order: Int, val url: String = "http://localhost:5000/${UUID.randomUUID()}", val completed: Boolean = false)

fun HttpMessage.todoEntry() = extract(TodoEntity)

fun TodoEntry.toEntity() = TodoEntity.toEntity(this)

object TodoEntity : EntityTransformer<TodoEntry> {
    val mapper = jacksonObjectMapper()

    override fun fromEntity(entity: Entity?): TodoEntry =
        entity?.let { mapper.readValue<TodoEntry>(String(it.array())) } ?: throw RuntimeException("could not get todo from entity")

    override fun toEntity(value: TodoEntry): Entity =
        ByteBuffer.wrap(mapper.writeValueAsString(value).toByteArray())
}