package com.murray.outbox.outboxevent.domain.event

import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.shared.domain.event.DomainEvent
import com.murray.outbox.shared.domain.model.ID

class OutboxEventSentEvent(
    override val aggregateId: ID,
    override val aggregateType: String,
    override val data: Map<String, Any> = emptyMap()
): DomainEvent {

    companion object {
        fun fromOutboxEvent(outboxEvent: OutboxEvent) =
            OutboxEventSentEvent(
                aggregateId = outboxEvent.id,
                aggregateType = OutboxEvent::class.simpleName!!,
                data = mapOf(
                    "sourceId" to outboxEvent.sourceId.value,
                    "eventTypeId" to outboxEvent.eventTypeId.id,
                    "sourcePayload" to outboxEvent.sourcePayload.value,
                    "status" to outboxEvent.status.status,
                    "sendDirectly" to outboxEvent.sendDirectly
                )
            )
    }
}