package com.murray.outbox.outboxevent.domain.exception

import com.murray.outbox.shared.exception.UnexpectedApiException
class OutBoxRepositoryAdapterException(
    throwable: Throwable,
) : UnexpectedApiException(
    internalErrorMessage = "Unexpected outbox repository exception cause:${throwable.message}",
    apiMessageKey = "outbox.error.saveEvent.failed",
    cause = throwable

)
