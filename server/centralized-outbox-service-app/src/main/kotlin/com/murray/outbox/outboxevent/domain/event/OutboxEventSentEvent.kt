package com.murray.outbox.outboxevent.domain.event

import com.murray.outbox.shared.domain.event.DomainEvent
import com.murray.outbox.shared.domain.model.ID

class OutboxEventSentEvent(
    override val aggregateId: ID,
    override val aggregateType: String,
    override val data: Map<String, Any> = emptyMap()
): DomainEvent