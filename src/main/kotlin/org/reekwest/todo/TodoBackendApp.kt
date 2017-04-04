package org.reekwest.todo

import org.reekwest.http.core.Method
import org.reekwest.http.core.Request
import org.reekwest.http.core.entity.entity
import org.reekwest.http.core.ok
import org.reekwest.http.jetty.startJettyServer
import org.reekwest.http.routing.by
import org.reekwest.http.routing.routes

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0] else "5000"

    val headers = listOf("access-control-allow-origin" to "*", "access-control-allow-headers" to "content-type")
    routes(
        Method.OPTIONS to "/" by { _: Request -> ok(headers = headers) },
        Method.GET to "/" by { _: Request -> ok(headers = headers).entity("Hello World") }
    ).startJettyServer(port.toInt())
}