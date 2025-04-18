package com.murray.outbox.outboxevent.domain.model

import java.time.Clock
import java.time.Instant

data class OutboxEventMessage private constructor(
    val eventTypeId: OutboxEventTypeId,
    val sourceId: OutboxSourceId,
    val processId: OutboxEventMessageProcessId,
    val sourcePayload: OutboxSourcePayload,
    val sourceUserId: OutboxEventMessageSourceUserId
) {
    private var success: Boolean = false
    private var processError: String = ""
    private var processedOn: Instant? = null

    fun success():Boolean = success
    fun processError():String = processError
    fun processedOn() = processedOn

    fun completedWithSuccess(){
        success = true
        processedOn = Instant.now(Clock.systemUTC())
    }

    fun completedWithError(errorMessage: String) {
        success = false
        processedOn = Instant.now(Clock.systemUTC())
        processError = errorMessage

    }

    companion object {

        fun load(
            eventTypeId: OutboxEventTypeId,
            sourceId: OutboxSourceId,
            processId: OutboxEventMessageProcessId,
            sourcePayload: OutboxSourcePayload,
            sourceUserId: OutboxEventMessageSourceUserId
        ) = OutboxEventMessage(
            eventTypeId = eventTypeId,
            sourceId = sourceId,
            processId = processId,
            sourcePayload = sourcePayload,
            sourceUserId = sourceUserId

        )
    }
}
