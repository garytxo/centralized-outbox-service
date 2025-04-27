package com.murray.outbox.test.objectmother

import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import java.util.UUID

class OutboxEventTypeObjectMother {
    var id = UUID.randomUUID()
    var eventType :String = "RECALCULATE_REPORT_FOR_ACCOUNT"
    var active :Boolean = true
    var description = "Run every 3 mins"
    var queueName = "test-queue-sqs"
    var scheduledCron: String = "0 */3 * * * *"
    var scheduledLockAtMostFor = "PT3M"
    var scheduledLockAtLeastFor = "PT1M"
    var userId = UUID.randomUUID()
    private fun build(): OutboxEventType {
        return OutboxEventType(
            id = id,
            eventType = eventType,
            active = active,
            description = description,
            queueName = queueName,
            scheduledCron = scheduledCron,
            scheduledLockAtMostFor = scheduledLockAtMostFor,
            scheduledLockAtLeastFor = scheduledLockAtLeastFor,
            rowCreatedBy = userId.toString(),
            rowUpdatedBy = userId.toString(),
        )
    }

    companion object {
        fun build(init: OutboxEventTypeObjectMother.() -> Unit)
                = OutboxEventTypeObjectMother().apply(init).build()
    }
}