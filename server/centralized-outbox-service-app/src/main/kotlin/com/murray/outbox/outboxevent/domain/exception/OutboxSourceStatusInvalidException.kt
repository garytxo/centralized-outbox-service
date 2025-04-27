package com.murray.outbox.outboxevent.domain.exception

import com.murray.outbox.shared.exception.BadRequestApiException

class OutboxSourceStatusInvalidException : BadRequestApiException(
    message = "Outbox source status invalid",
    apiMessageKey = "outbox.error.sourceStatus.invalidArgument"
)