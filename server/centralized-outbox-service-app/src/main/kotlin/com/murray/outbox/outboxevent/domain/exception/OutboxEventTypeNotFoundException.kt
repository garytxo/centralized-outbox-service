package com.murray.outbox.outboxevent.domain.exception

import com.murray.outbox.shared.exception.NotFoundApiException

class OutboxEventTypeNotFoundException(eventType:String): NotFoundApiException(
    message = "Outbox event type not found for: $eventType",
    apiMessageKey = "outbox.error.outbox.eventType.notFound"
)