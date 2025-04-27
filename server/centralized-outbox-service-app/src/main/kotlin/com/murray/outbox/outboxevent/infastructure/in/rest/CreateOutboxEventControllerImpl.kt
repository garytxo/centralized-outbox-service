package com.murray.outbox.outboxevent.infastructure.`in`.rest

import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventController
import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventRequest
import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventResponse
import com.murray.outbox.outboxevent.application.CreateOutboxEventCommand
import com.murray.outbox.shared.application.CommandBus
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateOutboxEventControllerImpl(
    private val commandBus: CommandBus
) : CreateOutboxEventController{

    override fun save(createOutboxEventRequest: CreateOutboxEventRequest): CreateOutboxEventResponse {
        val command = createOutboxEventRequest.toCommand()
        val newId = commandBus.dispatch(command)
        return CreateOutboxEventResponse(newId)
    }

    private fun CreateOutboxEventRequest.toCommand() = CreateOutboxEventCommand(
        eventId = this.sourceId,
        eventType = this.eventType,
        eventPayload = this.sourcePayload,
        sendDirectly = this.skip,
    )
}