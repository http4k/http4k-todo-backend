package org.http4k.todo

import org.http4k.core.HttpHandler
import org.http4k.core.Request

private val corsHeaders = listOf(
    "access-control-allow-origin" to "*",
    "access-control-allow-headers" to "content-type",
    "access-control-allow-methods" to "POST, GET, OPTIONS, PUT, PATCH,  DELETE")

fun cors(handler: HttpHandler): HttpHandler = { request: Request -> handler(request).copy(headers = corsHeaders) }


