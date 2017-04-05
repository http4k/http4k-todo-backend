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
import org.reekwest.http.core.ok
import org.reekwest.http.jetty.startJettyServer
import org.reekwest.http.routing.by
import org.reekwest.http.routing.routes
import java.nio.ByteBuffer

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0] else "5000"

    val todos = mutableListOf<TodoEntry>()

    cors(routes(
        OPTIONS to "/" by { _: Request -> ok() },
        GET to "/" by { _: Request -> ok().entity(todos.toJson()) },
        POST to "/" by { request: Request ->
            val todo = request.todoEntry()
            ok(entity = todo.toEntity())
        },
        DELETE to "/" by { _: Request ->
            ok().entity(todos.toJson())
        }
    )).startJettyServer(port.toInt())
}

fun List<TodoEntry>.toJson(): String = jacksonObjectMapper().writeValueAsString(this)

data class TodoEntry(val title: String, val order: Int)

fun HttpMessage.todoEntry() = extract(TodoEntity)

fun TodoEntry.toEntity() = TodoEntity.toEntity(this)

object TodoEntity : EntityTransformer<TodoEntry> {
    val mapper = jacksonObjectMapper()

    override fun fromEntity(entity: Entity?): TodoEntry =
        entity?.let { mapper.readValue<TodoEntry>(String(it.array())) } ?: throw RuntimeException("could not get todo from entity")

    override fun toEntity(value: TodoEntry): Entity =
        ByteBuffer.wrap(mapper.writeValueAsString(value).toByteArray())
}