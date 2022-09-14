package com.example.util

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    //private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    //private val dbUrl = appConfig.property("db.jdbcUrl").getString()
    //private val dbUser = appConfig.property("db.dbUser").getString()
    //private val dbPassword = appConfig.property("db.dbPassword").getString()
    private val db_port = "54321"
    private val db_name = "ktor-db"
    private val jdbcURL = "jdbc:postgresql://localhost:$db_port/$db_name"
    private val db_user = "testuser"
    private val db_password = "mpassword"
    private val config = HoconApplicationConfig(ConfigFactory.load())
    private val driverClassName = config.property("storage.driverClassName").getString()


    fun init() {
        Database.connect(hikari())
        val flyway = Flyway.configure().dataSource(jdbcURL, db_user, db_password)
            //.outOfOrder(true)
            //.ignoreMissingMigrations(true)
            .load()

        //flyway.repair()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = driverClassName
        config.jdbcUrl = jdbcURL
        config.username = db_user
        config.password = db_password
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }


    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction{ block() }//transaction{ String } for raw SQL
        }
}