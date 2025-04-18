package com.murray.outbox.outboxevent.domain.model

@JvmInline
value class OutboxSourcePayload private constructor(val value: String) {

    fun isValid() = value.isNotBlank()
    
    companion object {
        fun asPayload(payload: String)  = OutboxSourcePayload(payload)
    }
}