package com.murray.outbox.outboxevent.infastructure.out.scheduler

data class OutboxEventTypeScheduleTask(
    val eventType: String,
    val lockAtMostFor:String,
    val lockAtLeastFor: String
)