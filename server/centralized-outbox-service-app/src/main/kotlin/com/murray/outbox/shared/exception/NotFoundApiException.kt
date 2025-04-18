package com.murray.outbox.shared.exception

open class NotFoundApiException(apiMessageKey: String, override val message: String?) :
    ApiBaseException(apiMessageKey) {

    override fun getResponseCode() = 404
}