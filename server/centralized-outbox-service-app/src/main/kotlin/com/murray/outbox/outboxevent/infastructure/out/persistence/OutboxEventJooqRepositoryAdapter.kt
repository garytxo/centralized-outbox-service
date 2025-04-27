package com.murray.outbox.outboxevent.infastructure.out.persistence

import com.murray.outbox.infrastructure.out.persistence.jooq.repository.OutboxEventJooqRepository
import com.murray.outbox.infrastructure.out.persistence.jooq.repository.dto.BatchOutboxMessage
import com.murray.outbox.outboxevent.domain.exception.OutBoxRepositoryAdapterException
import com.murray.outbox.outboxevent.domain.exception.OutboxEventAlreadyExistsException
import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageProcessId
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageSourceUserId
import com.murray.outbox.outboxevent.domain.model.OutboxEventType
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import com.murray.outbox.outboxevent.domain.port.out.CreateOutboxEventRepository
import com.murray.outbox.outboxevent.domain.port.out.PublishOutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import  com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxEvent as OutboxEventJooqEntity


@Repository
class OutboxEventJooqRepositoryAdapter(
    private val outboxEventJooqRepository: OutboxEventJooqRepository
) : CreateOutboxEventRepository, PublishOutboxEventRepository {

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

    override fun findAllPendingEventFor(outboxEventType: OutboxEventType): Set<OutboxEventMessage> {
        return outboxEventJooqRepository.findPendingEventAndUpdateStatusFor(outboxEventType.id.id)
            .map {it.toOutboxEventMessage() }.toSet()
    }

    private fun BatchOutboxMessage.toOutboxEventMessage() = OutboxEventMessage.load(
        eventTypeId = OutboxEventTypeId(this.eventTypeId),
        sourceId = OutboxSourceId.asSource(this.sourceId),
        processId = OutboxEventMessageProcessId(this.processGroupId),
        sourcePayload = OutboxSourcePayload.asPayload(this.sourcePayload),
        sourceUserId = OutboxEventMessageSourceUserId.of(this.sourceUserId),
    )

    override fun updateProcessedEvent(updatedOutboxEventMessage: OutboxEventMessage) {
        logger.info("Updating outboxEventMessage:$updatedOutboxEventMessage after processing" )
        if(updatedOutboxEventMessage.success()){
            outboxEventJooqRepository.saveWithSuccess(updatedOutboxEventMessage.toBatchOutboxMessage())
        }else {
            outboxEventJooqRepository.saveWithError(updatedOutboxEventMessage.toBatchOutboxMessage(),updatedOutboxEventMessage.processError())
        }
    }
    private fun OutboxEventMessage.toBatchOutboxMessage() = BatchOutboxMessage(
        eventTypeId = this.eventTypeId.id,
        sourceId = this.sourceId.value,
        processGroupId = this.processId.id,
        sourcePayload = this.sourcePayload.value,
        sourceUserId = this.sourceUserId.value,
    )
}