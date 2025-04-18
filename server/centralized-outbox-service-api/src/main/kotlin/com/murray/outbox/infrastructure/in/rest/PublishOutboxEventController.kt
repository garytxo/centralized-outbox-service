package com.murray.outbox.infrastructure.`in`.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Tag(
    name = "PublishOutboxEventController",
    description = "Publish outbox events by event type"
)
fun interface PublishOutboxEventController {

    @PutMapping(path = ["v1/event-type/{eventType}/publish"])
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Publish Outbox Event by event type")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Success",
            content = [(Content(
                    array = (ArraySchema(schema = Schema(implementation = PublishOutboxEventResponse::class)))
                ))
            ]
        )
    )
    fun publish(
        @Parameter(description = "Valid outbox event type")
        @PathVariable("eventType") eventType: String,
        ): PublishOutboxEventResponse

}