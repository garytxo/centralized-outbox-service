package com.murray.outbox.shared.exception

abstract class ApiBaseException : RuntimeException {

    private val apiMessageKey: String

    constructor(apiMessageKey: String) {
        this.apiMessageKey = apiMessageKey
    }

    constructor(apiMessageKey: String, internalErrorMessage: String) : super(internalErrorMessage) {
        this.apiMessageKey = apiMessageKey
    }

    constructor(apiMessageKey: String, internalErrorMessage: String, cause: Throwable) : super(
        internalErrorMessage,
        cause
    ) {
        this.apiMessageKey = apiMessageKey
    }

    constructor(apiMessageKey: String, cause: Throwable) : super(cause) {
        this.apiMessageKey = apiMessageKey
    }

    fun apiMessageKey() = apiMessageKey


    abstract fun getResponseCode(): Int

}