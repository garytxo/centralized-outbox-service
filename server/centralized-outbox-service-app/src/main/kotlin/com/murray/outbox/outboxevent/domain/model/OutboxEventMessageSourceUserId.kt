package com.murray.outbox.outboxevent.domain.model

import com.murray.outbox.shared.domain.model.ID
import java.util.UUID

class OutboxEventMessageSourceUserId(id: UUID? = null) : ID(generate(id).id)

