package com.murray.outbox.infrastructure.`in`.rest

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "PublishOutboxEventResponse",
    description = "Publish out event response"
)
class PublishOutboxEventResponse(

    @field:Schema(
        name = "eventType", required = true,
        title = "Outbox Event Type ",
        example = "RECALCULATE_REPORT_FOR_ACCOUNT",
    )
    val eventType: String,

    @field:Schema(
        name = "totalEventSuccessfullySent", required = true,
        title = "Total number of event published successfully sent",
        example = "1",
    )
    val totalEventSuccessfullySent: Long = 0,


    @field:Schema(
        name = "totalEventFailedSent", required = true,
        title = "Total number of event published unsuccessfully sent",
        example = "1",
    )
    val totalEventFailedSent: Long = 0,

    @field:Schema(
        name = "processGroupId", required = true,
        title = "Unique out-box process group id for batch messages sent",
        example = "d87fb616-c8e7-4825-a086-df8cdf1450e9",
    )
    val processGroupId: String


)