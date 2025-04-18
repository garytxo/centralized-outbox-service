package com.murray.outbox.shared.application

fun interface QueryHandler<RESPONSE, QUERY : Query<RESPONSE>> {

    fun handle(query: QUERY): RESPONSE
}