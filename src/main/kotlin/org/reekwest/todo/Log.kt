package org.reekwest.todo

import org.reekwest.http.core.HttpHandler
import org.reekwest.http.core.Request

fun log(handler: HttpHandler): HttpHandler = { request: Request ->
    println(request)
    val response = handler(request)
    println(response)
    println("==========================")
    response
}
