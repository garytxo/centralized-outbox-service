package com.murray.outbox.outboxevent.domain.event

import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.shared.domain.event.DomainEvent
import com.murray.outbox.shared.domain.model.ID

class OutboxEventTypePublishedEvent(override val aggregateId: ID,
                                    override val aggregateType: String,
                                    override val data: Map<String, Any> = emptyMap()
): DomainEvent
{
    companion object {

        fun from(outboxEventMessage: OutboxEventMessage)= OutboxEventTypePublishedEvent(
            aggregateId = outboxEventMessage.processId,
            aggregateType =OutboxEventMessage::class.simpleName!!,
            data = mapOf(
                "sourceEventId" to outboxEventMessage.sourceId.value,
                "eventTypeId" to outboxEventMessage.eventTypeId.id.toString(),
                "processGroupId" to outboxEventMessage.processId.id.toString(),
                "publishedSuccessfully" to outboxEventMessage.success()
            )
        )
    }
}