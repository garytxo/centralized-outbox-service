package com.murray.outbox.shared.infrastructure.out.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent
import com.murray.outbox.infrastructure.out.persistence.jooq.repository.DomainEventJooqRepository
import com.murray.outbox.shared.domain.event.DomainEvent
import org.jooq.JSONB
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.util.UUID

/**
 * Simplified solution that handles the domain events which can be extended to persist the
 * Example of how to use internal spring event listener to persist domain events
 */
@Component
class SpringInMemoryDomainEventListener(
    private val objectMapper: ObjectMapper,
    private val domainEventJooqRepository: DomainEventJooqRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun handleDomainEvent(event: DomainEvent) {
        logger.info("Handling Event: ${event.asString()}")
        domainEventJooqRepository.store(event.toEventEntity())

    }

    private fun DomainEvent.toEventEntity() = OutboxDomainEvent(
        id = UUID.randomUUID(),
        eventType = this.javaClass.simpleName.toString(),
        aggregateId = this.aggregateId.id.toString(),
        aggregateType = this.aggregateType,
        eventData = JSONB.jsonb(objectMapper.writeValueAsString(this.data)),
        happenedOn = this.happenedOn.atOffset(ZoneOffset.UTC).toLocalDateTime(),
        rowCreatedBy = "",
        rowUpdatedBy = "",

        )

    private fun DomainEvent.asString() = """
        Event:${this.javaClass.simpleName} aggregateType:${this.aggregateType} aggregateId:${this.aggregateId} happenedOn:${this.happenedOn} data:${this.data}
    """.trimIndent()
}