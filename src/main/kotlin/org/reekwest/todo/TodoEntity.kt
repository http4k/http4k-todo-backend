package org.reekwest.todo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.reekwest.http.core.HttpMessage
import org.reekwest.http.core.entity.Entity
import org.reekwest.http.core.entity.EntityTransformer
import org.reekwest.http.core.entity.extract
import java.nio.ByteBuffer

object TodoEntity : EntityTransformer<TodoEntry> {
    val mapper = jacksonObjectMapper()

    override fun fromEntity(entity: Entity?): TodoEntry =
        entity?.let { mapper.readValue<TodoEntry>(String(it.array())) } ?: throw RuntimeException("could not get todo from entity")

    override fun toEntity(value: TodoEntry): Entity =
        ByteBuffer.wrap(mapper.writeValueAsString(value).toByteArray())
}

fun HttpMessage.todoEntry() = extract(TodoEntity)
fun TodoEntry.toEntity() = TodoEntity.toEntity(this)
fun List<TodoEntry>.toJson(): String = jacksonObjectMapper().writeValueAsString(this)