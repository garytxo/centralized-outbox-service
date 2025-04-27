package com.murray.outbox.outboxevent.domain.model

import com.murray.outbox.shared.domain.model.AggregateRoot
data class OutboxEventType private constructor(
    val id: OutboxEventTypeId,
    val type: OutboxEventTypeValue,
    val queueName: OutboxEventTypeQueue
): AggregateRoot<OutboxEventTypeId>() {

    companion object {
        fun load(
            id: OutboxEventTypeId,
            type: OutboxEventTypeValue,
            queueName: OutboxEventTypeQueue
        )=
            OutboxEventType(id = id, type = type,queueName=queueName)
    }

}