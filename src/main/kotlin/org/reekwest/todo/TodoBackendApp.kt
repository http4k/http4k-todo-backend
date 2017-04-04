package org.reekwest.todo

import org.reekwest.http.core.Method
import org.reekwest.http.core.Request
import org.reekwest.http.core.entity.entity
import org.reekwest.http.core.ok
import org.reekwest.http.jetty.startJettyServer
import org.reekwest.http.routing.by
import org.reekwest.http.routing.routes

fun main(args: Array<String>) {
    routes(Method.GET to "/" by { _: Request -> ok().entity("Hello World") }).startJettyServer(args[0].toInt())
}

