package com.murray.outbox.test.objectmother

import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventStatus
import com.murray.outbox.outboxevent.domain.model.OutboxEventId
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import java.util.UUID
import com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxEvent as OutboxEventJooqEntity

class OutboxEventObjectMother {

    var id = OutboxEventId()

    var sourceId = OutboxSourceId.asSource(UUID.randomUUID().toString())

    var eventTypeId = OutboxEventTypeId()

    var sourcePayload = OutboxSourcePayload.asPayload("{'hello':'world'}")

    var status = OutboxEventStatus.defaultStatus()

    var createdBy = UUID.randomUUID().toString()

    var processGroupId: UUID? = null

    var sendDirectly  = false

    private fun toDomain() = OutboxEvent.load(
        id = this.id,
        eventTypeId = this.eventTypeId,
        sourceId = this.sourceId,
        eventPayload = this.sourcePayload,
        status = this.status,
        sendDirectly = this.sendDirectly
    )

    private fun toJooqEntity() = OutboxEventJooqEntity(
        id = this.id.id,
        sourceEventId = this.sourceId.value,
        eventTypeId = this.eventTypeId.id,
        sourcePayload = this.sourcePayload.value,
        eventStatus = this.status.status,
        processGroupId = this.processGroupId,
        rowCreatedBy = this.createdBy
    )

    companion object {
        fun buildDomain(init: OutboxEventObjectMother.() -> Unit)
                = OutboxEventObjectMother().apply(init).toDomain()

        fun buildJooqEntity(init: OutboxEventObjectMother.() -> Unit)
                = OutboxEventObjectMother().apply(init).toJooqEntity()
    }

}