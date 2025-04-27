package com.murray.outbox.outboxevent.domain.model


class OutboxEventMessageSourceUserId private constructor(val value:String) {

    companion object {
        fun of(
            value:String
        ) = OutboxEventMessageSourceUserId(value)
    }
}

