package com.murray.outbox.shared.application

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.reflect.ParameterizedType

@Service
class QueryBus(
    queryHandlers: List<QueryHandler<*, *>>
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val handlers: Map<Class<*>, QueryHandler<*, *>> =
        queryHandlers.associateBy {
            (it.javaClass.superclass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[1] as Class<*>
        }


    init {
        logger.info("Query Handlers loaded ${handlers.size}")
        handlers.forEach { (command, handler) -> logger.info("Loaded query:${command.simpleName} -> handler:${handler.javaClass.simpleName}") }
    }

    @Suppress("UNCHECKED_CAST")
    fun <R, Q : Query<R>> dispatch(query: Q): R {
        val handler = handlers[query::class.java]
            ?: throw IllegalArgumentException("No handler for query type ${query::class.java.simpleName}")

        return (handler as QueryHandler<R, Q>).handle(query)
    }

}