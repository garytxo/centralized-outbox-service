package com.murray.outbox.shared.domain.event

import com.murray.outbox.shared.domain.model.AggregateRoot

interface DomainEventDispatcher {

    fun dispatchAggregate(aggregateRoot: AggregateRoot<*>)

    fun dispatchEvent(evt: DomainEvent)
}