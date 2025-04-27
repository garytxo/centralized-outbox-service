package com.murray.outbox.outboxevent.application

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import com.murray.outbox.outboxevent.domain.event.OutboxEventSentEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageProcessId
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageSourceUserId
import com.murray.outbox.outboxevent.domain.model.OutboxEventStatus
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import com.murray.outbox.outboxevent.domain.port.out.CreateOutboxEventRepository
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventMessagePublisher
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.shared.annotation.OutboxCommandHandler
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

@OutboxCommandHandler
class CreateOutboxEventCommandHandler(
    private val createOutBoxEventRepository: CreateOutboxEventRepository,
    private val auditAwareService: AuditAwareService,
    private val outBoxEventTypeRepository: OutboxEventTypeRepository,
    private val outboxEventMessagePublisher: OutboxEventMessagePublisher,
    domainEventDispatcher: DomainEventDispatcher): AbstractCommandHandler<UUID,CreateOutboxEventCommand>(domainEventDispatcher) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    override fun execute(command: CreateOutboxEventCommand): UUID {
        logger.info("create event: $command")

        val newEvent = command.toDomain()

        if (!newEvent.sendDirectly) {
            createOutBoxEventRepository.save(newEvent)
            this.dispatchAggregateEvent(newEvent)
        } else {
            logger.warn("Event sending forced. Skipping database save and dispatching OutboxEventSendEvent")
            outboxEventMessagePublisher.send(newEvent.toOutboxEventMessage())
            this.dispatchEvent(OutboxEventSentEvent.fromOutboxEvent(newEvent))
        }

        return newEvent.id.id
    }

    private fun OutboxEvent.toOutboxEventMessage() = OutboxEventMessage.load(
        eventTypeId = this.eventTypeId,
        sourceId = this.sourceId,
        processId = OutboxEventMessageProcessId(),
        sourcePayload = this.sourcePayload,
        sourceUserId = OutboxEventMessageSourceUserId.of(auditAwareService.getCurrentExternalUserId())
    )

    private fun CreateOutboxEventCommand.toDomain() = OutboxEvent.create(
        sourceId = OutboxSourceId.asSource(this.eventId),
        eventTypeId = this.toEventType().id,
        eventPayload = OutboxSourcePayload.asPayload(this.eventPayload),
        status = OutboxEventStatus.defaultStatus(),
        sendDirectly = this.sendDirectly
    )

    private fun CreateOutboxEventCommand.toEventType() =
        outBoxEventTypeRepository.findBy(this.eventType);
}