package com.murray.outbox.shared.exception

open class UnauthorisedApiException(apiMessageKey: String) : ApiBaseException(apiMessageKey) {

    override fun getResponseCode() = 401
}