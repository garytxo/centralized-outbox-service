package com.murray.outbox.outboxevent.domain.port.out

import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventType

interface PublishOutboxEventRepository {

    fun findAllPendingEventFor(outboxEventType: OutboxEventType): Set<OutboxEventMessage>

    fun updateProcessedEvent(updatedOutboxEventMessage: OutboxEventMessage)
}