package org.reekwest.todo

import org.reekwest.http.core.HttpHandler
import org.reekwest.http.core.Request
import org.reekwest.http.core.Response
import org.reekwest.http.core.entity.StringEntity
import org.reekwest.http.core.entity.extract

fun log(handler: HttpHandler): HttpHandler = { request: Request ->
    println(request.requestString())
    val response = handler(request)
    println(response.responseString())
    response
}

private fun Request.requestString(): String = "$method\n$uri\n${extract(StringEntity)}"
private fun Response.responseString(): String = "$status\n${extract(StringEntity)}\n\n"

