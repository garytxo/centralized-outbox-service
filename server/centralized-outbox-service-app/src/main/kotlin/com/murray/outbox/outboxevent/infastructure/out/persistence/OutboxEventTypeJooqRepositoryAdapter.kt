package com.murray.outbox.outboxevent.infastructure.out.persistence

import com.murray.outbox.infrastructure.out.persistence.jooq.repository.OutboxEventTypeJooqRepository
import com.murray.outbox.outboxevent.domain.exception.OutboxEventTypeNotActiveException
import com.murray.outbox.outboxevent.domain.exception.OutboxEventTypeNotFoundException
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventType
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeQueue
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeValue
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.outboxevent.domain.port.out.PublishOutboxEventRepository
import com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxEventType as OutboxEventTypePojo
import org.springframework.stereotype.Repository

@Repository
class OutboxEventTypeJooqRepositoryAdapter(
    private val outboxEventTypeJooqRepository: OutboxEventTypeJooqRepository,
) : OutboxEventTypeRepository {

    override fun findBy(eventType: String) =
        outboxEventTypeJooqRepository.findByType(eventType)?.toDomain()
            ?:throw OutboxEventTypeNotFoundException(eventType)

    override fun findBy(id: OutboxEventTypeId) =
        outboxEventTypeJooqRepository.findById(id.id)?.toDomain()
            ?:throw OutboxEventTypeNotFoundException(id.id.toString())


    private fun OutboxEventTypePojo.toDomain():OutboxEventType {
        if (!this.active!!)
            throw OutboxEventTypeNotActiveException(eventType)

        return OutboxEventType.load(
            id = OutboxEventTypeId(this.id),
            type = OutboxEventTypeValue.of(this.eventType),
            queueName = OutboxEventTypeQueue.of(this.queueName)
        )
    }
}