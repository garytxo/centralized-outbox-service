package com.murray.outbox.shared.infrastructure.out.rest

import com.murray.outbox.shared.exception.ApiBaseException
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

@ControllerAdvice(annotations = [RestController::class, Controller::class])
class GlobalRestExceptionHandlerControllerAdvice {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Handle all API Exception classes
     */
    @ExceptionHandler(ApiBaseException::class)
    fun apiExceptionHandler(
        apiBaseException: ApiBaseException,
        webRequest: WebRequest
    ): ResponseEntity<ErrorRestResponse> {

        logger.error("API Exception for ${apiBaseException.javaClass.simpleName} thrown for method:${webRequest.method()} path:${webRequest.url()} ")

        return ResponseEntity
            .status(apiBaseException.getResponseCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(apiBaseException.toErrorResponse())
    }

    private fun ApiBaseException.toErrorResponse(): ErrorRestResponse {
        return ErrorRestResponse(
            status = this.getResponseCode(),
            messageKey = this.apiMessageKey()
        )
    }

    private fun WebRequest.url(): String {
        return (this as ServletWebRequest).request.requestURI
    }

    private fun WebRequest.method(): String {
        return (this as ServletWebRequest).request.method
    }
}