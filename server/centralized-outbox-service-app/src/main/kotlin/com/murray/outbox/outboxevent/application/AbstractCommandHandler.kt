package com.murray.outbox.outboxevent.application

import com.murray.outbox.shared.application.Command
import com.murray.outbox.shared.application.CommandHandler
import com.murray.outbox.shared.domain.event.DomainEvent
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import com.murray.outbox.shared.domain.model.AggregateRoot

abstract class AbstractCommandHandler<RESPONSE, COMMAND : Command<RESPONSE>>(private val eventDispatcher: DomainEventDispatcher) : CommandHandler<RESPONSE, COMMAND>{

    fun dispatchAggregateEvent(aggregateRoot: AggregateRoot<*>) {
        eventDispatcher.dispatchAggregate(aggregateRoot)
    }

    fun dispatchEvent(event: DomainEvent) {
        eventDispatcher.dispatchEvent(event)
    }
}