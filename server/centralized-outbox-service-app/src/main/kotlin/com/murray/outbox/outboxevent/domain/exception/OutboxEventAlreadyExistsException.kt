package com.murray.outbox.outboxevent.domain.exception

import com.murray.outbox.shared.exception.ConflictRequestApiException

class OutboxEventAlreadyExistsException(
    override val message: String?,
): ConflictRequestApiException(
        message = message,
        apiMessageKey = "outbox.error.outbox.alreadyExist",
)