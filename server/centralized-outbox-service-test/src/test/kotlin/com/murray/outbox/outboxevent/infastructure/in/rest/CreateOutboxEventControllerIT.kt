package com.murray.outbox.outboxevent.infastructure.`in`.rest

import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventRequest
import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventResponse
import com.murray.outbox.outboxevent.domain.event.OutboxEventCreatedEvent
import com.murray.outbox.outboxevent.domain.event.OutboxEventSentEvent
import com.murray.outbox.outboxevent.domain.model.OutboxEvent
import com.murray.outbox.shared.infrastructure.out.rest.ErrorRestResponse
import com.murray.outbox.test.BaseRestIntegrationTest
import com.murray.outbox.test.objectmother.OutboxEventTypeObjectMother
import com.murray.outbox.test.sqs.SQSTestQueueManager
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.untilNotNull
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import java.time.Duration
import java.util.UUID

class CreateOutboxEventControllerIT  @Autowired constructor (val sqsTemplate: SqsTemplate): BaseRestIntegrationTest() {


    companion object {
        const val NEW_EVENT_TYPE="SOME_AWESOME_EVENT"
        const val RECALCULATE_REPORT_FOR_ACCOUNT = "RECALCULATE_REPORT_FOR_ACCOUNT"
        const val CREATE_OUTBOX_EVENT_URL = "/api-outbox-service/v1/event"
    }


    @Test
    fun `save should return 201 when successfully saved event when not forced to send`() {

        //Given
        val request = CreateOutboxEventRequest(
            sourceId = UUID.randomUUID().toString(),
            eventType = RECALCULATE_REPORT_FOR_ACCOUNT,
            sourcePayload = "{'hello':'world'}",
            skip = false,
        )

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .post(CREATE_OUTBOX_EVENT_URL)

        //Then:
        val response = result.then().statusCode(HttpStatus.CREATED.value())
            .extract().response().`as`(CreateOutboxEventResponse::class.java)


        assertNotNull(response.id)

        assertThatEventExistsFor(response.id,request)
    }

    @Test
    fun `save should return 404 when event source type is not valid`() {

        //Given
        val request = CreateOutboxEventRequest(
            sourceId = UUID.randomUUID().toString(),
            eventType = "NOT_KNOWN",
            sourcePayload = "{'hello':'world'}",
            skip = false,
        )

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .post(CREATE_OUTBOX_EVENT_URL)

        //Then:
        val errorResponse = result.then().statusCode(HttpStatus.NOT_FOUND.value())
            .extract().response().`as`(ErrorRestResponse::class.java)
        assertNotNull(errorResponse)
        assertThat(errorResponse.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(errorResponse.messageKey).isEqualTo("outbox.error.outbox.eventType.notFound")
    }

    @Test
    fun `save should return 201 and domain event stored when not forced to send`() {

        //Given
        val request = CreateOutboxEventRequest(
            sourceId = UUID.randomUUID().toString(),
            eventType = RECALCULATE_REPORT_FOR_ACCOUNT,
            sourcePayload = """{"id":"540711e8-eadc-40f7-abd9-7688e10ae4e6","projectionOperation":"RECALCULATE_REPORT_FOR_ACCOUNT"}""",
            skip = false,
        )

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .post(CREATE_OUTBOX_EVENT_URL)

        //Then:
        val response = result.then().statusCode(HttpStatus.CREATED.value())
            .extract().response().`as`(CreateOutboxEventResponse::class.java)
        assertNotNull(response.id)
        assertThatDomainEventCreatedExistsFor(response.id, request)
    }

    @Test
    fun `save should return 201 and domain event stored when send directly to send`() {

        //Given
        val testQueue = SQSTestQueueManager.TestQueueName.TEST_DIRECT_SEND
        sqsTestQueueManager.createQueue(testQueue)

        val newEventType = OutboxEventTypeObjectMother.build {
            queueName = testQueue.queueName
            eventType = testQueue.queueEventType
        }
        outBoxTestDataGenerator.saveEventType(newEventType)

        val request = CreateOutboxEventRequest(
            sourceId = UUID.randomUUID().toString(),
            eventType = newEventType.eventType,
            sourcePayload = """{"id":"540711e8-eadc-40f7-abd9-7688e10ae4e6","projectionOperation":"TEST_DIRECT_SEND"}""",
            skip = true,
        )

        //When
        val result = given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .post(CREATE_OUTBOX_EVENT_URL)

        //Then:
        val response = result.then().statusCode(HttpStatus.CREATED.value())
            .extract().response().`as`(CreateOutboxEventResponse::class.java)
        assertNotNull(response.id)
        assertThatDomainEventSendExistsFor(response.id, request)

        val received = await()
            .pollInterval(Duration.ofMillis(1000))
            .atMost(Duration.ofSeconds(3))
            .ignoreExceptions()
            .untilNotNull {
                sqsTemplate.receive(testQueue.queueName,String::class.java)
            }

        assertThat(received.get().payload).isEqualTo(request.sourcePayload)

    }

    private fun assertThatDomainEventSendExistsFor(id: UUID, request: CreateOutboxEventRequest) {

        val result = outBoxTestDataGenerator.findDomainEventByAggregateId(id.toString())
        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById(request.eventType)

        assertNotNull(result)
        assertThat(result.eventType).isEqualTo(OutboxEventSentEvent::class.simpleName)
        assertThat(result.aggregateType).isEqualTo(OutboxEvent::class.simpleName)
        assertThat(result.eventData.data()).isNotNull()
        val eventData = objectMapper.readValue(result.eventData.data(), Map::class.java)
        assertThat(eventData["eventTypeId"]).isEqualTo(eventType.id.toString())
        assertThat(eventData["sourceId"]).isEqualTo(request.sourceId)
        assertThat(eventData["sourcePayload"]).isEqualTo(request.sourcePayload)
        assertThat(eventData["sendDirectly"]).isEqualTo(request.skip)


        assertThat(result.rowCreatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(result.rowCreatedOn).isNotNull()
        assertThat(result.rowUpdatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(result.rowUpdatedOn).isNotNull()
        assertThat(result.rowVersion).isEqualTo(1)
    }

    private fun assertThatEventExistsFor(id: UUID, request: CreateOutboxEventRequest) {

        val result = outBoxTestDataGenerator.findOutBoxEventById(id)
        assertThat(result.eventTypeId).isEqualTo(result.eventTypeId)
        assertThat(result.sourcePayload).isEqualTo(request.sourcePayload)
        assertThat(result.sourceUserId).isEqualTo(TEST_USER.userId)
        assertThat(result.rowCreatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(result.rowCreatedOn).isNotNull()
        assertThat(result.rowUpdatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(result.rowUpdatedOn).isNotNull()
        assertThat(result.rowVersion).isEqualTo(1)
    }

    private fun assertThatDomainEventCreatedExistsFor(id: UUID, request: CreateOutboxEventRequest) {

        val result = outBoxTestDataGenerator.findDomainEventByAggregateId(id.toString())
        val eventType = outBoxTestDataGenerator.findOutBoxEventTypeById(request.eventType)

        assertNotNull(result)
        assertThat(result.eventType).isEqualTo(OutboxEventCreatedEvent::class.simpleName)
        assertThat(result.aggregateType).isEqualTo(OutboxEvent::class.simpleName)
        assertThat(result.eventData.data()).isNotNull()
        val eventData = objectMapper.readValue(result.eventData.data(), Map::class.java)
        assertThat(eventData["eventTypeId"]).isEqualTo(eventType.id.toString())
        assertThat(eventData["sourceId"]).isEqualTo(request.sourceId)
        assertThat(eventData["skip"]).isEqualTo(request.skip)

        assertThat(result.rowCreatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(result.rowCreatedOn).isNotNull()
        assertThat(result.rowUpdatedBy).isEqualTo(TEST_USER.id.toString())
        assertThat(result.rowUpdatedOn).isNotNull()
        assertThat(result.rowVersion).isEqualTo(1)
    }
}