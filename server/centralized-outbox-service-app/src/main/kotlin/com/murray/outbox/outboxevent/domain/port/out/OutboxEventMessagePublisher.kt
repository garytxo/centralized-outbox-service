package com.murray.outbox.outboxevent.domain.port.out

import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage


fun interface OutboxEventMessagePublisher {

    fun send(outboxEventMessage: OutboxEventMessage)
}