package com.murray.outbox.outboxevent.application

import com.murray.outbox.shared.application.Command

data class PublishOutboxEventCommand (
    val eventType: String
):Command<PublishOutboxEventCommandResponse>