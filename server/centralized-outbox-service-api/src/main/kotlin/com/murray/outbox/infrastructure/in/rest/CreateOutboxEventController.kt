package com.murray.outbox.infrastructure.`in`.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

@Tag(
    name = "CreateOutboxEventController",
    description = "Only stores an outbox event if the createOutboxRequest.eventType is managed by the service."
)
fun interface CreateOutboxEventController {

    @PostMapping(path = ["v1/event"])
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Create New Outbox Event")

    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "Success",
            content = [
                (Content(
                    array = (ArraySchema(schema = Schema(implementation = CreateOutboxEventResponse::class)))
                ))]
        )
    )
    fun save(
        @SwaggerRequestBody(
            description = "Create OutBox Event Request Body", required = true,
            content = [
                (Content( schema = Schema(implementation = CreateOutboxEventRequest::class)))
            ])
        @RequestBody createOutboxEventRequest: CreateOutboxEventRequest
    ): CreateOutboxEventResponse

}