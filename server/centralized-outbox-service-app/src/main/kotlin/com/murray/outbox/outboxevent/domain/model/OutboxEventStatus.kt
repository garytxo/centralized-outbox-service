package com.murray.outbox.outboxevent.domain.model

@JvmInline
value class OutboxEventStatus private constructor(val status: String) {

    fun isValid(): Boolean = status in ALLOWED_STATUSES

    companion object {
        private val ALLOWED_STATUSES = setOf("PENDING","PROCESSING", "PROCESSED", "FAILED")

        fun asStatus(value: String) = OutboxEventStatus(value)

        fun toProcessing() = OutboxEventStatus("PROCESSING")

        fun toProcessed() = OutboxEventStatus("PROCESSED")

        fun defaultStatus() = OutboxEventStatus("PENDING")
    }
}