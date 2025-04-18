package com.murray.outbox.outboxevent.infastructure.`in`.rest

import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventRequest
import com.murray.outbox.infrastructure.`in`.rest.CreateOutboxEventResponse
import com.murray.outbox.shared.infrastructure.out.persistence.LoggedUser
import com.murray.outbox.test.BaseRestIntegrationTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class CreateOutboxEventControllerIT : BaseRestIntegrationTest() {


    companion object {
        val TEST_USER = LoggedUser.generateTestUser()
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
}