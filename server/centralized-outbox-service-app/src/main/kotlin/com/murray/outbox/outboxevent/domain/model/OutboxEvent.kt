package com.murray.outbox.outboxevent.domain.model

import com.murray.outbox.outboxevent.domain.event.OutboxEventCreatedEvent
import com.murray.outbox.shared.domain.model.AggregateRoot
import com.murray.outbox.outboxevent.domain.exception.OutboxSourceIdInvalidException
import com.murray.outbox.outboxevent.domain.exception.OutboxSourcePayloadInvalidException
import com.murray.outbox.outboxevent.domain.exception.OutboxSourceStatusInvalidException

data class OutboxEvent private constructor(
    val id: OutboxEventId,
    val eventTypeId: OutboxEventTypeId,
    val sourceId: OutboxSourceId,
    val sourcePayload: OutboxSourcePayload,
    val status: OutboxEventStatus,
    val sendDirectly: Boolean

) : AggregateRoot<OutboxEventId>() {


    init {
        validateSourceId()
        validateEventStatus()
        validatePayload()
    }

    private fun validateSourceId() {
        if (!sourceId.isValid()) {
            throw OutboxSourceIdInvalidException()
        }
    }

    private fun validateEventStatus() {
        if (!status.isValid()) {
            throw OutboxSourceStatusInvalidException()
        }
    }

    private fun validatePayload() {
        if (!sourcePayload.isValid()) {
            throw OutboxSourcePayloadInvalidException()
        }
    }

    companion object {

        fun create(
            sourceId: OutboxSourceId,
            eventTypeId: OutboxEventTypeId,
            eventPayload: OutboxSourcePayload,
            status: OutboxEventStatus,
            sendDirectly: Boolean
        ): OutboxEvent {
            return OutboxEvent(
                id = OutboxEventId(),
                sourceId = sourceId,
                eventTypeId = eventTypeId,
                sourcePayload = eventPayload,
                status = status,
                sendDirectly = sendDirectly
            ).also {
                it.recordEvent(OutboxEventCreatedEvent(
                    aggregateId = it.id,
                    aggregateType = it::class.simpleName!!,
                    data = mapOf(
                        "sourceId" to it.sourceId.value,
                        "eventTypeId" to it.eventTypeId.id,
                        "status" to it.status.status,
                        "skip" to it.sendDirectly
                    )
                ))
            }
        }

        fun load(
            id: OutboxEventId,
            sourceId: OutboxSourceId,
            eventTypeId: OutboxEventTypeId,
            eventPayload: OutboxSourcePayload,
            status: OutboxEventStatus,
            sendDirectly: Boolean
        ) = OutboxEvent(
            id = id,
            sourceId = sourceId,
            eventTypeId = eventTypeId,
            sourcePayload = eventPayload,
            status = status,
            sendDirectly = sendDirectly

        )
    }
}