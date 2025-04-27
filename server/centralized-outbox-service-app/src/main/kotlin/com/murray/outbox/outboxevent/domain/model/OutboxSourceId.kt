package com.murray.outbox.outboxevent.domain.model

@JvmInline
value class OutboxSourceId private constructor(val value: String) {

    fun isValid() = value.isNotBlank()

    companion object {
        fun asSource(value: String) = OutboxSourceId(value)
    }
}