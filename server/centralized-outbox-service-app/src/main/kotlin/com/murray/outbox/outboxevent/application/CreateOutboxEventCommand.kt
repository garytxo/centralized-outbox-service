package com.murray.outbox.outboxevent.application

import com.murray.outbox.shared.application.Command
import java.util.UUID

data class CreateOutboxEventCommand(
    val eventId: String,
    val eventType: String,
    val eventPayload: String,
    val sendDirectly: Boolean = false
) : Command<UUID>