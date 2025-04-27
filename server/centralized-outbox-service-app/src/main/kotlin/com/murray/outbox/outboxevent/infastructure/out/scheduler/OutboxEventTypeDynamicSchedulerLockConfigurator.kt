package com.murray.outbox.outboxevent.infastructure.out.scheduler

import net.javacrumbs.shedlock.core.LockConfiguration
import net.javacrumbs.shedlock.spring.ExtendedLockConfigurationExtractor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.Optional

/**
 * Each outbox event type should be registered in the  shedlock database table which ensure that
 * scheduled tasks run only once at the same time over multiple instances.
 * This Lock Configure ensure that we can dynamically register each OutboxEventType in shedlock
 * by capturing OutboxEventTypeScheduleTask and mapping its values to the LockConfiguration class.
 *
 */
@Primary
@Component
class OutboxEventTypeDynamicSchedulerLockConfigurator : ExtendedLockConfigurationExtractor {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun getLockConfiguration(
        `object`: Any,
        method: Method,
        parameterValues: Array<out Any>
    ): Optional<LockConfiguration> {

        logger.debug("Configuring Dynamic Outbox Event Scheduled tasks")
        return if(`object` is OutboxEventTypeScheduleTaskExecutor && parameterValues.first() is OutboxEventTypeScheduleTask){
            val scheduledTask = parameterValues.first() as OutboxEventTypeScheduleTask
            logger.debug(
                "Found OutboxEventTypeScheduleTask configuration for outboxEventTypeScheduleTask:{}",
                scheduledTask
            )
            Optional.of(
                LockConfiguration(
                Instant.now(Clock.systemUTC()),
                scheduledTask.eventType,
                Duration.parse(scheduledTask.lockAtMostFor),
                Duration.parse(scheduledTask.lockAtLeastFor))
            ) }else {
            logger.warn("No match found ")
            Optional.empty()
        }

    }

    override fun getLockConfiguration(task: Runnable): Optional<LockConfiguration> {
        return Optional.empty()
    }
}