package com.murray.outbox.infrastructure.out.persistence.jooq.repository.dto

import java.util.UUID

data class BatchOutboxMessage(
    val eventTypeId: UUID,
    val sourceId: String,
    val processGroupId: UUID,
    val sourcePayload:String,
    val sourceUserId:String
)