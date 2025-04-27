package com.murray.outbox.outboxevent.infastructure.`in`.rest

import com.murray.outbox.infrastructure.`in`.rest.PublishOutboxEventResponse
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import com.murray.outbox.infrastructure.out.persistence.jooq.repository.OutboxEventJooqRepository.Companion.OutboxStatus.ERROR
import com.murray.outbox.infrastructure.out.persistence.jooq.repository.OutboxEventJooqRepository.Companion.OutboxStatus.PROCESSED
import com.murray.outbox.outboxevent.domain.event.OutboxEventTypePublishedEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventMessagePublisher
import com.murray.outbox.test.BaseRestIntegrationTest
import com.murray.outbox.test.objectmother.OutboxEventObjectMother
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class PublishOutboxEventControllerIT : BaseRestIntegrationTest() {


    @MockkBean(relaxed = true)
    lateinit var outboxEventMessagePublisher : OutboxEventMessagePublisher

    companion object {
        const val RECALCULATE_REPORT_FOR_ACCOUNT = "RECALCULATE_REPORT_FOR_ACCOUNT"
        const val PUBLISH_EVENTS_URL = "/api-outbox-service/v1/event-type/{eventType}/publish"
    }

    lateinit var  activeEventType: OutboxEventType

    @BeforeEach
    fun initialize() {
        activeEventType = outBoxTestDataGenerator.findOutBoxEventTypeById(RECALCULATE_REPORT_FOR_ACCOUNT)

    }

    @Test
    fun `publish should return 200 when successfully publish event`() {

        //Given
        val eventJooq = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(activeEventType.id)
        }
        outBoxTestDataGenerator.saveOutboxEvent(eventJooq)


        every { outboxEventMessagePublisher.send(any()) } returns Unit

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .put(PUBLISH_EVENTS_URL, RECALCULATE_REPORT_FOR_ACCOUNT)

        //Then:
        val response = result.then()
            .statusCode(HttpStatus.OK.value())
            .extract().response().`as`(PublishOutboxEventResponse::class.java)

        assertThat(response.eventType).isEqualTo(RECALCULATE_REPORT_FOR_ACCOUNT)
        assertThat(response.totalEventSuccessfullySent).isGreaterThanOrEqualTo(1)
        assertThat(response.totalEventFailedSent).isGreaterThanOrEqualTo(0)
        assertThat(response.processGroupId).isNotNull()

        val outboxEventUpdated = outBoxTestDataGenerator.findOutBoxEventById(eventJooq.id)
        assertThat(outboxEventUpdated.eventStatus).isEqualTo(PROCESSED)
        assertThat(outboxEventUpdated.processGroupId).isEqualTo(UUID.fromString(response.processGroupId))
        assertThat(outboxEventUpdated.processedOn).isNotNull()
        assertThat(outboxEventUpdated.processedError).isNull()

        //Check domain event
        assertDomainEventResultFor(activeEventType.id.toString(),response,eventJooq.sourceEventId,true)
    }



    @Test
    fun `publish should return 200 when successfully publish 10 events with the same source id`() {

        //Given
        val numberOfEvents = 10
        val eventJooq = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(activeEventType.id)
        }
        val eventsCreated = outBoxTestDataGenerator.generateEventsWithSameSourceIdAndEventTypeFor(eventJooq,numberOfEvents)


        every { outboxEventMessagePublisher.send(any()) } returns Unit

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .put(PUBLISH_EVENTS_URL, RECALCULATE_REPORT_FOR_ACCOUNT)

        //Then:
        val response = result.then()
            .statusCode(HttpStatus.OK.value())
            .extract().response().`as`(PublishOutboxEventResponse::class.java)


        assertThat(eventsCreated).isEqualTo(numberOfEvents)

        assertThat(response.eventType).isEqualTo(RECALCULATE_REPORT_FOR_ACCOUNT)
        assertThat(response.totalEventSuccessfullySent).isGreaterThanOrEqualTo(1)
        assertThat(response.totalEventFailedSent).isGreaterThanOrEqualTo(0)
        assertThat(response.processGroupId).isNotNull()

        val outboxEventUpdated = outBoxTestDataGenerator.findOutBoxEventById(eventJooq.id)
        assertThat(outboxEventUpdated.eventStatus).isEqualTo(PROCESSED)
        assertThat(outboxEventUpdated.processGroupId).isEqualTo(UUID.fromString(response.processGroupId))
        assertThat(outboxEventUpdated.processedOn).isNotNull()
        assertThat(outboxEventUpdated.processedError).isNull()

        //Check domain event
        assertDomainEventResultFor(activeEventType.id.toString(),response,eventJooq.sourceEventId,true)
    }

    @Test
    fun `publish should return 200 when successfully publish multiple events different source id`() {

        //Given
        val numberOfEvents = 10
        val eventJooq = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(activeEventType.id)
        }
        val eventsCreated = outBoxTestDataGenerator.generateEventsWithSameSourceIdAndEventTypeFor(eventJooq,numberOfEvents)

        val numberOfEvents2 = 20
        val eventJooq2 = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(activeEventType.id)
        }
        val eventsCreated2 = outBoxTestDataGenerator.generateEventsWithSameSourceIdAndEventTypeFor(eventJooq2,numberOfEvents2)



        every { outboxEventMessagePublisher.send(any()) } returns Unit

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .put(PUBLISH_EVENTS_URL, RECALCULATE_REPORT_FOR_ACCOUNT)

        //Then:
        val response = result.then()
            .statusCode(HttpStatus.OK.value())
            .extract().response().`as`(PublishOutboxEventResponse::class.java)


        assertThat(eventsCreated).isEqualTo(numberOfEvents)
        assertThat(eventsCreated2).isEqualTo(numberOfEvents2)

        assertThat(response.eventType).isEqualTo(RECALCULATE_REPORT_FOR_ACCOUNT)
        assertThat(response.totalEventSuccessfullySent).isGreaterThanOrEqualTo(1)
        assertThat(response.totalEventFailedSent).isGreaterThanOrEqualTo(0)
        assertThat(response.processGroupId).isNotNull()

        val outboxEventUpdated = outBoxTestDataGenerator.findOutBoxEventById(eventJooq.id)
        assertThat(outboxEventUpdated.eventStatus).isEqualTo(PROCESSED)
        assertThat(outboxEventUpdated.processGroupId).isEqualTo(UUID.fromString(response.processGroupId))
        assertThat(outboxEventUpdated.processedOn).isNotNull()
        assertThat(outboxEventUpdated.processedError).isNull()

        //Check domain event
        val domainEvents = outBoxTestDataGenerator.findDomainEventsByAggregateId(response.processGroupId)

        assertThat(domainEvents.count()).isEqualTo(2)
        for(domainEvent in domainEvents) {
            assertThat(domainEvent.eventType).isEqualTo(OutboxEventTypePublishedEvent::class.simpleName)
            assertThat(domainEvent.aggregateType).isEqualTo(OutboxEventMessage::class.simpleName)
            assertThat(domainEvent.eventData.data()).isNotNull()
            val eventData = objectMapper.readValue(domainEvent.eventData.data(), Map::class.java)
            assertThat(eventData["eventTypeId"]).isEqualTo(activeEventType.id.toString())

            assertThat(eventData["sourceEventId"]).isIn(eventJooq.sourceEventId,eventJooq2.sourceEventId)
            assertThat(eventData["processGroupId"]).isEqualTo(response.processGroupId)
            assertThat(eventData["publishedSuccessfully"]).isEqualTo(true)

            assertThat(domainEvent.rowCreatedBy).isEqualTo(TEST_USER.id.toString())
            assertThat(domainEvent.rowCreatedOn).isNotNull()
            assertThat(domainEvent.rowUpdatedBy).isEqualTo(TEST_USER.id.toString())
            assertThat(domainEvent.rowUpdatedOn).isNotNull()
            assertThat(domainEvent.rowVersion).isEqualTo(1)
        }

        @Test
        fun `publish should return 200 when failed to publish event`() {

            //Given
            val eventJooq = OutboxEventObjectMother.buildJooqEntity {
                eventTypeId = OutboxEventTypeId(activeEventType.id)
            }
            outBoxTestDataGenerator.saveOutboxEvent(eventJooq)


            every { outboxEventMessagePublisher.send(any()) } throws IllegalArgumentException("Something went wrong")

            //When
            val result = given()
                .contentType(ContentType.JSON)
                .put(PUBLISH_EVENTS_URL, RECALCULATE_REPORT_FOR_ACCOUNT)

            //Then:
            val response = result.then()
                .statusCode(HttpStatus.OK.value())
                .extract().response().`as`(PublishOutboxEventResponse::class.java)

            assertThat(response.eventType).isEqualTo(RECALCULATE_REPORT_FOR_ACCOUNT)
            assertThat(response.totalEventSuccessfullySent).isGreaterThanOrEqualTo(0)
            assertThat(response.totalEventFailedSent).isGreaterThanOrEqualTo(1)
            assertThat(response.processGroupId).isNotNull()

            val outboxEventUpdated = outBoxTestDataGenerator.findOutBoxEventById(eventJooq.id)
            assertThat(outboxEventUpdated.eventStatus).isEqualTo(ERROR)
            assertThat(outboxEventUpdated.processGroupId).isEqualTo(UUID.fromString(response.processGroupId))
            assertThat(outboxEventUpdated.processedOn).isNotNull()
            assertThat(outboxEventUpdated.processedError).isNotNull()

            //Check domain event
            assertDomainEventResultFor(activeEventType.id.toString(),response,eventJooq.sourceEventId,false)
        }


    }





    private fun assertDomainEventResultFor(eventTypeId:String,response: PublishOutboxEventResponse, sourceEventId:String,successfullyPublished: Boolean) {
        val domainEvent = outBoxTestDataGenerator.findDomainEventByAggregateId(response.processGroupId)

        assertNotNull(domainEvent)
        assertThat(domainEvent.eventType).isEqualTo(OutboxEventTypePublishedEvent::class.simpleName)
        assertThat(domainEvent.aggregateType).isEqualTo(OutboxEventMessage::class.simpleName)
        assertThat(domainEvent.eventData.data()).isNotNull()

        val eventData = objectMapper.readValue(domainEvent.eventData.data(), Map::class.java)
        println(eventData)
        assertThat(eventData["eventTypeId"]).isEqualTo(eventTypeId)
        assertThat(eventData["sourceEventId"]).isEqualTo(sourceEventId)
        assertThat(eventData["processGroupId"]).isEqualTo(response.processGroupId)
        assertThat(eventData["publishedSuccessfully"]).isEqualTo(successfullyPublished)

        assertThat(domainEvent.rowCreatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(domainEvent.rowCreatedOn).isNotNull()
        assertThat(domainEvent.rowUpdatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(domainEvent.rowUpdatedOn).isNotNull()
        assertThat(domainEvent.rowVersion).isEqualTo(1)

    }

}