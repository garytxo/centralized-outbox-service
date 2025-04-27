package com.murray.outbox.infrastructure.out.persistence.jooq.listener

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.SQLDialect
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import java.sql.SQLException
import java.util.Objects

class SpringExceptionTranslationExecuteListener : ExecuteListener {

    override fun exception(ctx: ExecuteContext) {
        val dialect = ctx.configuration().dialect()
        val translator: SQLExceptionTranslator = getSqlExceptionTranslator(dialect)
        val sqlSqlException = ctx.sqlException()?: SQLException("Was Null")
        val dataAccessException = translator.translate(
            "Data access using JOOQ", ctx.sql(),
            sqlSqlException
        )
        val translation = Objects.requireNonNullElseGet(dataAccessException) {
            UncategorizedSQLException("translation of exception", ctx.sql(), sqlSqlException
            )
        }
        ctx.exception(translation)
    }

    private fun getSqlExceptionTranslator(dsl: SQLDialect): SQLExceptionTranslator {
        return SQLErrorCodeSQLExceptionTranslator(dsl.name)
    }
}