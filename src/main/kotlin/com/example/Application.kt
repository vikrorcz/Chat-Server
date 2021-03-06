package com.example

import io.ktor.server.application.*
import com.example.plugins.*
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureRouting()
    configureSerialization()
    DatabaseFactory.init()
    configureSecurity()
}
