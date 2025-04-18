package com.murray.outbox.outboxevent.application

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageProcessId
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageSourceUserId
import com.murray.outbox.outboxevent.domain.model.OutboxEventStatus
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import com.murray.outbox.outboxevent.domain.port.out.CreateOutboxEventRepository
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.shared.application.CommandHandler
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class CreateOutboxEventCommandHandler(
    private val createOutBoxEventRepository: CreateOutboxEventRepository,
    private val auditAwareService: AuditAwareService,
    private val outBoxEventTypeRepository: OutboxEventTypeRepository,
    private val domainEventDispatcher: DomainEventDispatcher): CommandHandler<UUID,CreateOutboxEventCommand> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    override fun execute(command: CreateOutboxEventCommand): UUID {
        logger.info("create event: $command")

        val newEvent = command.toDomain()

        if (!newEvent.sendDirectly) {
            createOutBoxEventRepository.save(newEvent)
            //this.dispatch(newEvent)
        } else {
            logger.warn("Event sending forced. Skipping database save and dispatching OutboxEventSendEvent")
           // domainEventDispatcher.send(newEvent.toOutboxEventMessage())
            //this.dispatch(OutboxEventSentEvent(newEvent))
        }

        return newEvent.id.id
    }

    private fun OutboxEvent.toOutboxEventMessage() = OutboxEventMessage.load(
        eventTypeId = this.eventTypeId,
        sourceId = this.sourceId,
        processId = OutboxEventMessageProcessId(),
        sourcePayload = this.sourcePayload,
        sourceUserId = OutboxEventMessageSourceUserId(UUID.fromString(auditAwareService.getCurrentExternalUserId()))
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