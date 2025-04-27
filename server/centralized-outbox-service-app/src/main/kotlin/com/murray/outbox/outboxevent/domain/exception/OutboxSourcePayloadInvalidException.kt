package com.murray.outbox.outboxevent.domain.exception

import com.murray.outbox.shared.exception.BadRequestApiException

class OutboxSourcePayloadInvalidException: BadRequestApiException(
    message = "Invalid source payload",
    apiMessageKey = "outbox.error.sourcePayload.invalidArgument"
)
