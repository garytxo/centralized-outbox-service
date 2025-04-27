package com.murray.outbox.outboxevent.application

data class PublishOutboxEventCommandResponse(
    val eventType: String,
    val totalEventSuccessfullySent: Long = 0,
    val totalEventFailedSent: Long = 0,
    val processGroupId: String
){
    fun totalEvents() = totalEventSuccessfullySent +  totalEventFailedSent
}