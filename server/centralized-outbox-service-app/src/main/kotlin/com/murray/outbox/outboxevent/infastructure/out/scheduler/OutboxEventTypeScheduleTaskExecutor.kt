package com.murray.outbox.outboxevent.infastructure.out.scheduler

import com.murray.outbox.outboxevent.application.PublishOutboxEventCommand
import com.murray.outbox.shared.application.CommandBus
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Component
class OutboxEventTypeScheduleTaskExecutor(private val commandBus: CommandBus) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @SchedulerLock
    fun executeTaskWithLock(scheduledTask: OutboxEventTypeScheduleTask) {
        val stopWatch = StopWatch().apply { start() }
        try {
            val response = commandBus.dispatch(PublishOutboxEventCommand(scheduledTask.eventType))
            stopWatch.stop()
            if(response.totalEvents()>0){
                logger.info("Completed Publishing Events For Event Type:${scheduledTask.eventType} took ${stopWatch.totalTimeSeconds} seconds for totalEvents:${response.totalEvents()}")
            }

        }catch(ex: Exception){
            stopWatch.stop()
            logger.error("Issue Publishing  Events For Event Type:${scheduledTask.eventType}  caused by :${ex.message}", ex)
        }

    }

}