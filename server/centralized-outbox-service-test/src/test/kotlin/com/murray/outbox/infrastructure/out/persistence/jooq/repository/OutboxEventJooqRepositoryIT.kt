package com.murray.outbox.infrastructure.out.persistence.jooq.repository

import com.murray.outbox.test.BaseIntegrationTest
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.test.data.OutBoxTestDataGenerator
import com.murray.outbox.test.objectmother.OutboxEventObjectMother
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.assertj.core.api.Assertions.assertThat

class OutboxEventJooqRepositoryIT  @Autowired private constructor(
    private val outboxEventJooqRepository: OutboxEventJooqRepository,
    private val outBoxTestDataGenerator:OutBoxTestDataGenerator
): BaseIntegrationTest()  {

    companion object {
        const val  RECALCULATE_REPORT_FOR_ACCOUNT = "RECALCULATE_REPORT_FOR_ACCOUNT"
    }

    @Test
    fun `save should insert new outbox event`() {

        //Given
        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById(RECALCULATE_REPORT_FOR_ACCOUNT)

        val event = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(eventType.id)
        }

        //When
        val result = outboxEventJooqRepository.save(event)

        //Then
        assertThat(result).isEqualTo(1)

        val foundEvent = outBoxTestDataGenerator.findOutBoxEventById(event.id)
        assertThat(foundEvent.sourceEventId).isEqualTo(event.sourceEventId)
        assertThat(foundEvent.eventTypeId).isEqualTo(eventType.id)
        assertThat(foundEvent.sourcePayload).isEqualTo(event.sourcePayload)
        assertThat(foundEvent.eventTypeId).isEqualTo(eventType.id)
        assertThat(foundEvent.eventStatus).isEqualTo(event.eventStatus)


    }

    @Test
    fun `save should throw error when event already exists`() {

        //Given
        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById(RECALCULATE_REPORT_FOR_ACCOUNT)

        val event = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(eventType.id)
        }
        outBoxTestDataGenerator.saveOutboxEvent(event)

        //When
        assertThrows<DuplicateKeyException> {
            outboxEventJooqRepository.save(event)
        }

    }

    @Test
    fun `findPendingEventAndUpdateStatusFor should return one event with id, and payload and group_id `(){

        //Given
        outBoxTestDataGenerator.truncateOutboxEvent()
        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById(RECALCULATE_REPORT_FOR_ACCOUNT)
        outBoxTestDataGenerator.saveEventType(eventType)

        val event = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(eventType.id)
        }
        outBoxTestDataGenerator.saveOutboxEvent(event)


        //When
        val results = outboxEventJooqRepository.findPendingEventAndUpdateStatusFor(event.eventTypeId)

        assertThat(results).hasSize(1)
        assertThat(results.first().eventTypeId).isEqualTo(eventType.id)
        assertThat(results.first().sourceId).isEqualTo(event.sourceEventId)
        assertThat(results.first().sourcePayload).isEqualTo(event.sourcePayload)
        assertThat(results.first().processGroupId).isNotNull()

    }

    //TODO:Add some more complex test for multiple event_ids

    @Test
    fun `findPendingEventAndUpdateStatusFor should return aggregated events by source_id, and payload and the same processGroupId `() {

        //Given
        outBoxTestDataGenerator.truncateOutboxEvent()

        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById(RECALCULATE_REPORT_FOR_ACCOUNT)
        val numberOfEvents = 10
        val eventJooq = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(eventType.id)
        }
        val eventsCreated = outBoxTestDataGenerator.generateEventsWithSameSourceIdAndEventTypeFor(eventJooq,numberOfEvents)

        val numberOfEvents2 = 20
        val eventJooq2 = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(eventType.id)
        }
        val eventsCreated2 = outBoxTestDataGenerator.generateEventsWithSameSourceIdAndEventTypeFor(eventJooq2,numberOfEvents2)


        //When
        val results = outboxEventJooqRepository.findPendingEventAndUpdateStatusFor(eventType.id)

        //THEN
        assertThat(eventsCreated).isEqualTo(numberOfEvents)
        assertThat(eventsCreated2).isEqualTo(numberOfEvents2)

        assertThat(results).hasSize(2)

        val processGroupId = results.first().processGroupId

        results.forEach { result->
            assertThat(result.eventTypeId).isEqualTo(eventType.id)
            assertThat(result.sourceId).isIn(eventJooq.sourceEventId,eventJooq2.sourceEventId)
            assertThat(result.sourcePayload).isEqualTo(eventJooq.sourcePayload)
            assertThat(result.processGroupId).isEqualTo(processGroupId)

        }

    }

}