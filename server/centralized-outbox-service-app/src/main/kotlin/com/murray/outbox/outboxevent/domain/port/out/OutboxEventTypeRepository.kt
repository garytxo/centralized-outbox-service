package com.murray.outbox.outboxevent.domain.port.out

import com.murray.outbox.outboxevent.domain.model.OutboxEventType
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId

interface OutboxEventTypeRepository {

    fun findBy(eventType: String): OutboxEventType

    fun findBy(id: OutboxEventTypeId):OutboxEventType
}