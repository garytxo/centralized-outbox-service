package com.murray.outbox.infrastructure.`in`.rest

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(
    name = "CreateOutboxEventResponse",
    description = "Create new outbox event response"
)
class CreateOutboxEventResponse(

    @field:Schema(
        name = "id", required = true,
        title = "Outbox Event Unique Identifier",
        example = "388a9b2d-0d25-478e-8378-ff46f299b5c3"
    )
    val id: UUID

)