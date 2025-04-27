package com.murray.outbox.shared.domain.event

import com.murray.outbox.shared.domain.model.ID
import java.time.Instant

interface DomainEvent {
    val aggregateId: ID
    val aggregateType: String
    val data: Map<String, Any>
        get() = emptyMap()
    val happenedOn: Instant
        get() = Instant.now()

}