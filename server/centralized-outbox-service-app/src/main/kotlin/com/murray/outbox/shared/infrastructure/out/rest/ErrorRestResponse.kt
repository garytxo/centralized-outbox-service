package com.murray.outbox.shared.infrastructure.out.rest

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(
    name = "ErrorRestResponse",
    description = "Generic error response contract"
)
class ErrorRestResponse(
    @Schema(name = "status", description = "Http error response code", example = "404", required = true)
    val status: Int,
    @Schema(
        name = "messageKey",
        description = "Unique message id that relates message label",
        example = "account.notFound",
        required = true
    )
    val messageKey: String,

    )  {

    @Schema(name = "timeStamp", description = "time stamp when error occurred", required = true)
    val timestamp: Instant = Instant.now()
}