package com.murray.outbox.infrastructure.out.persistence.jooq.repository

import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import com.murray.outbox.test.BaseNoneRestIntegrationTest
import com.murray.outbox.test.objectmother.OutboxEventTypeObjectMother
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OutboxEventTypeJooqRepositoryIT  @Autowired constructor(
    private val outboxEventTypeJooqRepository: OutboxEventTypeJooqRepository) : BaseNoneRestIntegrationTest()  {

    @Test
    fun `findByType should return eventType when exists`() {

        //Given
        val activeEventType = OutboxEventTypeObjectMother.build {}
        saveEventType(activeEventType)


        //When
        val result = outboxEventTypeJooqRepository.findByType(activeEventType.eventType)

        //Then
        assertNotNull(result)
    }

    @Test
    fun `findByType should return null when event type does not exists`() {
        //Given
        val eventType = "UNKNOWN"

        //When
        val result = outboxEventTypeJooqRepository.findByType(eventType)

        //Then
        assertNull(result)
    }

    @Test
    fun `findAllActiveEventsType should all eventTypes that are enabled by default`() {

        //Given
        val activeEventType = OutboxEventTypeObjectMother.build {
            eventType = "NEW_EVENT_ENABLE_TYPE"
        }
        saveEventType(activeEventType)

        //When
        val result = outboxEventTypeJooqRepository.findAllActiveEventsType()

        //Then
        assertThat(result.size).isGreaterThan(0)
    }

    private fun saveEventType(eventType: OutboxEventType) {
        outBoxTestDataGenerator.saveEventType(eventType)
    }
}