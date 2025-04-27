package com.murray.outbox.outboxevent.infastructure.out.persistence

import com.murray.outbox.outboxevent.domain.exception.OutboxEventAlreadyExistsException
import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventId
import com.murray.outbox.outboxevent.domain.model.OutboxEventStatus
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import com.murray.outbox.test.BaseNoneRestIntegrationTest
import com.murray.outbox.test.objectmother.OutboxEventObjectMother
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class OutboxEventJooqRepositoryAdapterIT @Autowired constructor(
    private val outBoxEventJooqRepositoryAdapter: OutboxEventJooqRepositoryAdapter
) : BaseNoneRestIntegrationTest() {

    @Test
    fun `save should persist new outbox event in database`() {

        // Given
        val eventTypeId = outBoxTestDataGenerator.findOutBoxEventTypeById("RECALCULATE_REPORT_FOR_ACCOUNT").id
        val event = OutboxEvent.create(
            sourceId = OutboxSourceId.asSource("086c357d-ccd6-4566-8a74-0ab8d579b1cf"),
            eventTypeId = OutboxEventTypeId(eventTypeId),
            eventPayload = OutboxSourcePayload.asPayload("{'hello':'world'}"),
            status = OutboxEventStatus.asStatus("PENDING"),
            sendDirectly = false
        )

        // When
        outBoxEventJooqRepositoryAdapter.save(event)

        // Then
        val result = outBoxTestDataGenerator.findOutBoxEventById(event.id.id)
        assertEquals(result.sourceUserId, TEST_USER.userId)

        assertNotNull(result)
        assertEquals(event.id.id.toString(), result.id.toString())
        assertEquals(event.eventTypeId.id, result.eventTypeId)
        assertEquals(event.sourcePayload.value, result.sourcePayload)
        assertEquals(event.status.status, result.eventStatus)
    }


    @Test
    fun `save should throw error when event already exists`() {

        //Given
        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById("RECALCULATE_REPORT_FOR_ACCOUNT")

        val eventJooq = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(eventType.id)
        }
        outBoxTestDataGenerator.saveOutboxEvent(eventJooq)

        val event = OutboxEventObjectMother.buildDomain {
            id = OutboxEventId(eventJooq.id)
            sourceId = OutboxSourceId.asSource(eventJooq.sourceEventId)
            eventTypeId = OutboxEventTypeId(eventType.id)
        }


        //When
        assertThrows<OutboxEventAlreadyExistsException> {
            outBoxEventJooqRepositoryAdapter.save(event)
        }

    }
}