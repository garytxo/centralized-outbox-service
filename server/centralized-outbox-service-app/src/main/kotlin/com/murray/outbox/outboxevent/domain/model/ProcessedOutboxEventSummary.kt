package com.murray.outbox.outboxevent.domain.model

import java.util.UUID

class ProcessedOutboxEventSummary private constructor(
    val outboxEventTypeValue: OutboxEventTypeValue,
    val totalEventSuccessfullySent: Long = 0,
    val totalEventFailedSent: Long = 0,
    val processGroupId: UUID
){
    companion object {
        fun of(outboxEventTypeValue: OutboxEventTypeValue,
               totalEventSuccessfullySent:Long,
               processGroupId:UUID) =
            ProcessedOutboxEventSummary(
                outboxEventTypeValue =outboxEventTypeValue,
                totalEventSuccessfullySent= totalEventSuccessfullySent,
                totalEventFailedSent=0,
                processGroupId=processGroupId)
    }
}