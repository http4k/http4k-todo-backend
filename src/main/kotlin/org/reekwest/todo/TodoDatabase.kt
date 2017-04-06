package org.reekwest.todo

import java.util.*

class TodoDatabase(private val baseUrl: String) {
    var todos = mutableListOf<TodoEntry>()

    fun save(todoEntry: TodoEntry): TodoEntry {
        val todoWithId = todoEntry.ensureId()
        todos.add(todoWithId)
        return todoWithId
    }

    fun clear(): List<TodoEntry> {
        todos.clear()
        return todos
    }

    fun find(id: String): TodoEntry? = todos.find { (it.url != null) && it.url.contains(id) }

    fun all(): List<TodoEntry> = todos

    private fun TodoEntry.ensureId(): TodoEntry = if (url == null) copy(url = "$baseUrl/${UUID.randomUUID()}") else this
}