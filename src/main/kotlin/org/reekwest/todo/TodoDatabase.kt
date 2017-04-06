package org.reekwest.todo

import java.util.*

class TodoDatabase(private val baseUrl: String) {
    var todos = mutableMapOf<String, TodoEntry>()

    fun save(id: String?, todoEntry: TodoEntry): TodoEntry {
        val todoWithId = todoEntry.ensureId(id)
        val patched = find(todoWithId.id!!)?.patch(todoEntry) ?: todoWithId
        todos.put(patched.id!!, patched)
        return patched
    }

    fun clear(): List<TodoEntry> {
        todos.clear()
        return todos.values.toList()
    }

    fun find(id: String): TodoEntry? = todos[id]

    fun all(): List<TodoEntry> = todos.values.toList()

    fun delete(id: String): TodoEntry? = todos.remove(id)

    private fun TodoEntry.ensureId(id: String?): TodoEntry {
        val newId = id ?: "${UUID.randomUUID()}"
        return copy(id = newId, url = "$baseUrl/$newId")
    }

    private fun TodoEntry.patch(toUpdate: TodoEntry): TodoEntry =
        copy(title = toUpdate.title ?: title, completed = toUpdate.completed ?: completed, order = toUpdate.order ?: order)
}