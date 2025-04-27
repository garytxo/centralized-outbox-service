package com.murray.outbox.shared.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Transactional
@Component
annotation class OutboxCommandHandler(
    @get: AliasFor(annotation = Component::class)
    val value: String = ""
)