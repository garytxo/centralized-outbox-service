package com.murray.outbox.shared.application

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.reflect.ParameterizedType

@Service
class CommandBus(
    commandHandlers: List<CommandHandler<*, *>>
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val handlers: Map<Class<*>, CommandHandler<*, *>> =
        commandHandlers.associateBy {
            (it.javaClass.superclass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*>
        }

    init {
        logger.info("Command Handlers loaded ${handlers.size}")
        handlers.forEach { (command, handler) -> logger.info("Loaded command:${command.simpleName} -> handler:${handler.javaClass.simpleName}") }
    }

    @Suppress("UNCHECKED_CAST")
    fun <RESPONSE, COMMAND : Command<RESPONSE>> dispatch(command: COMMAND): RESPONSE {
        val handler = handlers[command::class.java]
            ?: throw IllegalArgumentException("No handler for command type ${command::class.java.simpleName}")

        return (handler as CommandHandler<RESPONSE, COMMAND>).execute(command)
    }

}