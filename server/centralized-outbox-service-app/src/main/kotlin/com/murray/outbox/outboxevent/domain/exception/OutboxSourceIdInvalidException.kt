package com.murray.outbox.outboxevent.domain.exception

import com.murray.outbox.shared.exception.BadRequestApiException

class OutboxSourceIdInvalidException : BadRequestApiException(
    message = "Outbox source id  invalid",
    apiMessageKey = "outbox.error.sourceId.invalidArgument"
)
