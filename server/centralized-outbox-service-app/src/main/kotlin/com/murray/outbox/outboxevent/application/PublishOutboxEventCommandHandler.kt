package com.murray.outbox.outboxevent.application

import com.murray.outbox.outboxevent.domain.event.OutboxEventTypePublishedEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventMessagePublisher
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.outboxevent.domain.port.out.PublishOutboxEventRepository
import com.murray.outbox.shared.annotation.OutboxCommandHandler
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@OutboxCommandHandler
class PublishOutboxEventCommandHandler(
    private val outBoxEventTypeRepository: OutboxEventTypeRepository,
    private val publishOutBoxEventRepository: PublishOutboxEventRepository,
    private val outboxEventMessagePublisher: OutboxEventMessagePublisher,
    domainEventDispatcher: DomainEventDispatcher
) : AbstractCommandHandler<PublishOutboxEventCommandResponse,PublishOutboxEventCommand>(domainEventDispatcher) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun execute(command: PublishOutboxEventCommand): PublishOutboxEventCommandResponse {
        logger.info("create event: $command")
        val eventType = outBoxEventTypeRepository.findBy(command.eventType)
        val pendingEvents = publishOutBoxEventRepository.findAllPendingEventFor(eventType)

        pendingEvents
            .takeIf { it.isNotEmpty() }
            ?.also { logger.info("Found ${it.count()} pending events to publish for ${command.eventType}")  }
            ?.forEach { message->
                publish(message)
                    .apply {
                        saveProcessedResults(message)
                    }.apply {
                        dispatchEvent(OutboxEventTypePublishedEvent.from(message))
                    }
            }
        return PublishOutboxEventCommandResponse(
            eventType = eventType.type.value,
            totalEventSuccessfullySent = pendingEvents.count { it.success() }.toLong(),
            totalEventFailedSent = pendingEvents.count { !it.success() }.toLong(),
            processGroupId = pendingEvents.getProcessGroupId()
        )
    }

    private fun Set<OutboxEventMessage>.getProcessGroupId(): String {
        return if (this.isEmpty()) {
            ""
        } else {
            this.first().processId.id.toString()
        }
    }

    private fun publish(message: OutboxEventMessage) {
        kotlin.runCatching {
            outboxEventMessagePublisher.send(message)
        }.onFailure {
            logger.error("Error publishing event", it)
            message.completedWithError(it.stackTraceToString())
        }.onSuccess {
            message.completedWithSuccess()
        }

    }

    private fun saveProcessedResults(message: OutboxEventMessage) {

        publishOutBoxEventRepository.updateProcessedEvent(message)
    }
}