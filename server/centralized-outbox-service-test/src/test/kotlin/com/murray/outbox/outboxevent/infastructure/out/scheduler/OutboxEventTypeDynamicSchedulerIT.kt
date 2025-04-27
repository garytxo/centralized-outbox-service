package com.murray.outbox.outboxevent.infastructure.out.scheduler

import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import com.murray.outbox.test.BaseNoneRestIntegrationTest
import com.murray.outbox.test.objectmother.OutboxEventTypeObjectMother
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class OutboxEventTypeDynamicSchedulerIT  @Autowired private constructor(
    private val outboxEventTypeDynamicScheduler: OutboxEventTypeDynamicScheduler
): BaseNoneRestIntegrationTest() {

    @Test
    fun `should schedule and execute a dynamic task`() {

        //Given
        val activeEventType = OutboxEventTypeObjectMother.build {
            eventType = "TEST_EVENT_TYPE1"
            scheduledCron = "*/5 * * * * *" // Every 5 seconds
            scheduledLockAtMostFor ="PT30S"
            scheduledLockAtLeastFor = "PT5S"
        }
        saveEventType(activeEventType)

        // When: Loading tasks dynamically
        outboxEventTypeDynamicScheduler.initializeScheduledEvents()

        //Then
        await()
            .atMost(   Duration.ofSeconds(10))
            .pollInterval(Duration.ofSeconds(1))
            .until {
                findShedlockEventFor(activeEventType.eventType)>0
            }


    }

    @Test
    fun `should prevent concurrent execution using shedlock`() {

        //Given
        val activeEventType = OutboxEventTypeObjectMother.build {
            eventType = "LOCKED_TASK_EVENT"
            scheduledCron = "*/5 * * * * *" // Every 5 seconds
            scheduledLockAtMostFor ="PT10S"
            scheduledLockAtLeastFor = "PT5S"
        }

        saveEventType(activeEventType)

        // When: Loading tasks dynamically
        outboxEventTypeDynamicScheduler.initializeScheduledEvents()

        // When: Executing the task twice within the lock period
        outboxEventTypeDynamicScheduler.scheduleEventType(activeEventType)
        outboxEventTypeDynamicScheduler.scheduleEventType(activeEventType)

        // Then: Only one execution should be recorded in ShedLock
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofSeconds(1))
            .until {
                findShedlockEventFor(activeEventType.eventType) == 1
            }


    }

    private fun findShedlockEventFor(eventType: String):Int{

        return outBoxTestDataGenerator.findShedlockCountFor(eventType)
    }



    private fun saveEventType(eventType: OutboxEventType) {
        outBoxTestDataGenerator.saveEventType(eventType)
    }

}