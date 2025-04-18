package com.murray.outbox.outboxevent.infastructure.out.persistence

import com.murray.outbox.infrastructure.out.persistence.jooq.repository.OutboxEventJooqRepository
import com.murray.outbox.outboxevent.domain.exception.OutBoxRepositoryAdapterException
import com.murray.outbox.outboxevent.domain.exception.OutboxEventAlreadyExistsException
import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.outboxevent.domain.port.out.CreateOutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import  com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxEvent as OutboxEventJooqEntity


@Repository
class OutboxEventJooqRepositoryAdapter(
    private val outboxEventJooqRepository: OutboxEventJooqRepository
) : CreateOutboxEventRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun save(outboxEvent: OutboxEvent) {
        logger.info("Saving new outbox event: {}", outboxEvent)
        try {
            outboxEventJooqRepository.save(outboxEvent.toOutboxEventJooqEntity())
            logger.info("Outbox event saved successfully: {}", outboxEvent.id)

        } catch (duplicateKeyException: DuplicateKeyException){
            throw OutboxEventAlreadyExistsException(null)
        }catch(e: Exception) {
            logger.error("Error saving outbox event: ${e.message}", e)
            throw OutBoxRepositoryAdapterException(e)
        }
    }

    private fun OutboxEvent.toOutboxEventJooqEntity() =
        OutboxEventJooqEntity(
            id = this.id.id,
            sourceEventId = this.sourceId.value,
            eventTypeId = this.eventTypeId.id,
            sourcePayload = this.sourcePayload.value,
            eventStatus = this.status.status,
            rowCreatedBy = "" // THIS WILL BE OVERIDEN BY the JooqAuditableListener
        )
}