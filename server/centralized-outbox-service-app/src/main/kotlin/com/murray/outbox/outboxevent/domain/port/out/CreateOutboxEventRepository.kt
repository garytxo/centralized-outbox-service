package com.murray.outbox.outboxevent.domain.port.out

import com.murray.outbox.outboxevent.domain.model.OutboxEvent

fun interface CreateOutboxEventRepository {

    fun save(outboxEvent: OutboxEvent)
}