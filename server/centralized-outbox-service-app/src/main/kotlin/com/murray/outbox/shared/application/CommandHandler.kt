package com.murray.outbox.shared.application

fun interface CommandHandler <RESPONSE, COMMAND : Command<RESPONSE>> {

    fun execute(command: COMMAND): RESPONSE
}