package com.murray.outbox.outboxevent.domain.model

@JvmInline
value class OutboxEventTypeValue private constructor(val value: String) {
    companion object {
        fun of(value: String) = OutboxEventTypeValue(value)
    }
}