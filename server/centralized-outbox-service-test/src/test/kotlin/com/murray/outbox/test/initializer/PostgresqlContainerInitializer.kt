package com.murray.outbox.test.initializer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class PostgresqlContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private const val OUTBOX_DATABASE_NAME = "outbox_test_db"
        private const val OUTBOX_POSTGRESQL_DB_URL = "OUTBOX_POSTGRESQL_DB_URL"
        private const val OUTBOX_POSTGRESQL_USERNAME = "OUTBOX_POSTGRESQL_USERNAME"
        private const val OUTBOX_POSTGRESQL_PASSWORD = "OUTBOX_POSTGRESQL_PASSWORD"
    }

    private val postgresContainer = PostgreSQLContainer("postgres:16")
        .apply {
            withExposedPorts(5432)
            withDatabaseName(OUTBOX_DATABASE_NAME)
        }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        postgresContainer.start()
        val dbUrl = postgresContainer.jdbcUrl.substring(18)

        TestPropertyValues.of(
            mapOf(
                OUTBOX_POSTGRESQL_DB_URL to dbUrl,
                OUTBOX_POSTGRESQL_USERNAME to postgresContainer.username,
                OUTBOX_POSTGRESQL_PASSWORD to postgresContainer.password,
            ),
        ).applyTo(applicationContext.environment)
    }

}