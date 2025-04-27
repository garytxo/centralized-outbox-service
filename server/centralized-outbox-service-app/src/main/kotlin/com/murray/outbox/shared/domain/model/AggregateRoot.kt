package com.murray.outbox.shared.domain.model

import com.murray.outbox.shared.domain.event.DomainEvent
import java.time.Instant
import java.util.Collections

abstract class AggregateRoot<id : ID> {
    val createdOn = Instant.now()
    val domainEvents = mutableListOf<DomainEvent>()

    fun recordEvents(domainEvents: MutableList<DomainEvent>) {
        domainEvents.forEach(domainEvents::add)
    }

    fun recordEvent(domainEvent: DomainEvent) {
        domainEvents.add(domainEvent)
    }

    fun pullEvents(): List<DomainEvent> {
        val domainEventsRecorded = domainEvents.toMutableList()
        this.domainEvents.clear()
        return Collections.unmodifiableList(domainEventsRecorded)
    }



    fun id() = ID

}