package com.murray.outbox.outboxevent.infastructure.`in`.rest

import com.murray.outbox.infrastructure.`in`.rest.PublishOutboxEventController
import com.murray.outbox.infrastructure.`in`.rest.PublishOutboxEventResponse
import com.murray.outbox.outboxevent.application.PublishOutboxEventCommand
import com.murray.outbox.shared.application.CommandBus
import org.springframework.web.bind.annotation.RestController

@RestController
class PublishOutboxEventControllerImpl(private val commandBus: CommandBus) : PublishOutboxEventController {

    override fun publish(eventType: String): PublishOutboxEventResponse {

        val result = commandBus.dispatch(PublishOutboxEventCommand(eventType))
        return PublishOutboxEventResponse(
            eventType = result.eventType,
            totalEventSuccessfullySent = result.totalEventSuccessfullySent,
            totalEventFailedSent = result.totalEventFailedSent,
            processGroupId = result.processGroupId,
        )
    }
}