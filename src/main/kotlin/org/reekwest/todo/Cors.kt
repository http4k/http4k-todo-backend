package org.reekwest.todo

import org.reekwest.http.core.HttpHandler
import org.reekwest.http.core.Request

private val corsHeaders = listOf(
    "access-control-allow-origin" to "*",
    "access-control-allow-headers" to "content-type",
    "access-control-allow-methods" to "POST, GET, OPTIONS, PUT, DELETE")

fun cors(handler: HttpHandler): HttpHandler = { request: Request -> handler(request).copy(headers = corsHeaders) }


