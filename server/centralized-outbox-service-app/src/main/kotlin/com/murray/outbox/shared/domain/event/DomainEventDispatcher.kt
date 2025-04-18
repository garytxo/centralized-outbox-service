package com.murray.outbox.shared.domain.event

import com.murray.outbox.shared.domain.model.AggregateRoot

interface DomainEventDispatcher {

    fun dispatch(aggregateRoot: AggregateRoot<*>)

    fun dispatch(evt: DomainEvent)
}