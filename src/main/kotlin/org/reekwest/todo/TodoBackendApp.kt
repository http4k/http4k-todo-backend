package org.reekwest.todo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.reekwest.http.core.HttpMessage
import org.reekwest.http.core.Method
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

    val headers = listOf("access-control-allow-origin" to "*", "access-control-allow-headers" to "content-type")
    routes(
        Method.OPTIONS to "/" by { _: Request -> ok(headers = headers) },
        Method.GET to "/" by { _: Request -> ok(headers = headers).entity("Hello World") },
        Method.POST to "/" by { request: Request -> ok(headers = headers, entity = request.todoEntry().toEntity()) }
    ).startJettyServer(port.toInt())
}

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