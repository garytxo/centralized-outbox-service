package com.murray.outbox.outboxevent.domain.exception
import com.murray.outbox.shared.exception.NotFoundApiException

class OutboxEventTypeNotActiveException(eventType:String): NotFoundApiException(
    message = "Outbox event type not active for: $eventType",
    apiMessageKey = "outbox.error.outbox.eventType.notActive"
)
