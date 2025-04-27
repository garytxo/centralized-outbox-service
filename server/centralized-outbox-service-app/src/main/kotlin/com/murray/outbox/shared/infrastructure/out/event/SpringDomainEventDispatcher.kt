package com.murray.outbox.shared.infrastructure.out.event

import com.murray.outbox.shared.domain.event.DomainEvent
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import com.murray.outbox.shared.domain.model.AggregateRoot
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class SpringDomainEventDispatcher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : DomainEventDispatcher{

    private val logger = LoggerFactory.getLogger(this.javaClass)


    override fun dispatchAggregate(aggregateRoot: AggregateRoot<*>) {
        aggregateRoot.pullEvents().forEach {
            logger.info("Dispatching aggregate $aggregateRoot event:$it")
            applicationEventPublisher.publishEvent(it)
        }
    }

    override fun dispatchEvent(evt: DomainEvent) {
        logger.info("Dispatching event:$evt")
        applicationEventPublisher.publishEvent(evt)
    }
}