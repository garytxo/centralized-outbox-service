package com.murray.outbox.shared.exception

open class BadRequestApiException(apiMessageKey: String, override val message: String?) :
    ApiBaseException(apiMessageKey) {


    override fun getResponseCode() = 400
}