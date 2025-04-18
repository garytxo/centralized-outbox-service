package com.murray.outbox.infrastructure.`in`.rest

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "CreateOutboxEvent",
    description = "Create new  outbox event request",
)
class CreateOutboxEventRequest(

    @field:Schema(
        name = "eventType", required = true,
        title = "Valid event type that is managed by the outbox service",
        example = "RECALCULATE_REPORT_FOR_ACCOUNT",
        allowableValues = ["RECALCULATE_REPORT_FOR_ACCOUNT","REPOPULATE_PROJECTIONS_FOR_ACCOUNT"]
    )
    val eventType: String,

    @field:Schema(
        name = "sourceId", required = true,
        title = "Event Source unique identifier, such as entity unique identifier or the hash of the event payload",
        example = "c2d6b809-07bd-43d6-bae2-305a15a37017"
    )
    val sourceId: String,


    @field:Schema(
        name = "sourcePayload", required = true,
        title = "The source payload that is associated with the sourceId and is published message body",
        example = """{"projectionOperation":"RECALCULATE_REPORT_FOR_ACCOUNT","accountId":"c2d6b809-07bd-43d6-bae2-305a15a37017"}"""
    )
    val sourcePayload: String,

    @field:Schema(
        name = "skip",
        title = "The event should be directly published and stored to be manage by scheduled task",
        example = "false"
    )
    val skip: Boolean = false
)