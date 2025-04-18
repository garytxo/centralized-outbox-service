package com.murray.outbox.shared.exception

open class ConflictRequestApiException(apiMessageKey: String, override val message: String?) :
    ApiBaseException(apiMessageKey) {


    override fun getResponseCode() = 409
}