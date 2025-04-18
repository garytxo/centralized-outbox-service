package com.murray.outbox.shared.exception

open class UnexpectedApiException : ApiBaseException {

    constructor(apiMessageKey: String, internalErrorMessage: String, cause: Throwable) : super(
        apiMessageKey,
        internalErrorMessage,
        cause
    )

    constructor(apiMessageKey: String, cause: Throwable) : super(apiMessageKey, cause)


    override fun getResponseCode() = 500
}