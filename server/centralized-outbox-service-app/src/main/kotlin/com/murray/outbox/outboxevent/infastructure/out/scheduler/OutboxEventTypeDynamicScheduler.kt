package com.murray.outbox.outboxevent.infastructure.out.scheduler

import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import com.murray.outbox.infrastructure.out.persistence.jooq.repository.OutboxEventTypeJooqRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Service
class OutboxEventTypeDynamicScheduler(
    @Qualifier("taskScheduler") private val taskScheduler: TaskScheduler,
    private val outboxEventTypeJooqRepository: OutboxEventTypeJooqRepository,
    private val scheduleTaskExecutor: OutboxEventTypeScheduleTaskExecutor,
) : ApplicationListener<RefreshScopeRefreshedEvent> {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val scheduledTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()

    override fun onApplicationEvent(event: RefreshScopeRefreshedEvent) {
        logger.info("Reloading scheduled tasks")
        loadScheduledEvents()
    }

    @PostConstruct
    fun initializeScheduledEvents() {
        loadScheduledEvents()
    }


    fun loadScheduledEvents(){
        outboxEventTypeJooqRepository.findAllActiveEventsType().forEach {
            scheduleEventType(it)
        }
    }

    fun scheduleEventType(eventType: OutboxEventType) {
        val cronTrigger = CronTrigger(eventType.scheduledCron)
        val scheduledFuture = taskScheduler.schedule({ scheduled(eventType) }, cronTrigger)
        scheduledFuture?.let { scheduledTasks[eventType.eventType] = it }
        logger.info("Scheduled task: ${eventType.eventType} with cron: ${eventType.scheduledCron} scheduledLockAtMostFor:${eventType.scheduledLockAtMostFor} scheduledLockAtLeastFor:${eventType.scheduledLockAtLeastFor}" )
    }

    internal fun scheduled(eventType: OutboxEventType) {
        val scheduledTask = OutboxEventTypeScheduleTask(eventType.eventType,eventType.scheduledLockAtMostFor!!,eventType.scheduledLockAtLeastFor!!)
        scheduleTaskExecutor.executeTaskWithLock(scheduledTask)

    }
}