package com.murray.outbox.outboxevent.domain.model

@JvmInline
value class OutboxEventTypeQueue private constructor(val value: String) {
    companion object {
        fun of(value: String) = OutboxEventTypeQueue(value)
    }
}