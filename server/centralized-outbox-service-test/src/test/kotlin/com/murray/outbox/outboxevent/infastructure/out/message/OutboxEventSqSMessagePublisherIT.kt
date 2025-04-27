package com.murray.outbox.outboxevent.infastructure.out.message

import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageProcessId
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageSourceUserId
import com.murray.outbox.outboxevent.domain.model.OutboxEventStatus
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import com.murray.outbox.shared.infrastructure.out.message.OutboxEventSqSMessagePublisher
import com.murray.outbox.test.BaseNoneRestIntegrationTest
import com.murray.outbox.test.objectmother.OutboxEventObjectMother
import com.murray.outbox.test.objectmother.OutboxEventTypeObjectMother
import com.murray.outbox.test.sqs.SQSTestQueueManager
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.untilNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.UUID

class OutboxEventSqSMessagePublisherIT  @Autowired constructor(
    val outboxEventSqSMessagePublisher: OutboxEventSqSMessagePublisher,
    val sqsTemplate: SqsTemplate
): BaseNoneRestIntegrationTest() {

    lateinit var  newEventType: OutboxEventType
    @BeforeEach
    fun init() {
        sqsTestQueueManager.createQueue(SQSTestQueueManager.TestQueueName.TEST_EVENT)
        newEventType = OutboxEventTypeObjectMother.build {
            queueName = SQSTestQueueManager.TestQueueName.TEST_EVENT.queueName
            eventType = SQSTestQueueManager.TestQueueName.TEST_EVENT.queueEventType
        }
        outBoxTestDataGenerator.saveEventType(newEventType)
        newEventType = outBoxTestDataGenerator.findOutBoxEventTypeById(SQSTestQueueManager.TestQueueName.TEST_EVENT.queueEventType)

    }


    @Test
    fun `send should publish message to test queue for valid supported event type and received`(){

        //Given
        val newProcessGroupId = UUID.randomUUID()
        val outBoxEvent = OutboxEventObjectMother.buildJooqEntity {
            eventTypeId = OutboxEventTypeId(newEventType.id)
            processGroupId = newProcessGroupId
            status = OutboxEventStatus.toProcessing()

        }
        outBoxTestDataGenerator.saveOutboxEvent(outBoxEvent)

        val outboxEventMessage = OutboxEventMessage.load(
            eventTypeId = OutboxEventTypeId(outBoxEvent.eventTypeId),
            sourceId = OutboxSourceId.asSource(outBoxEvent.sourceEventId),
            processId = OutboxEventMessageProcessId(outBoxEvent.processGroupId),
            sourcePayload = OutboxSourcePayload.asPayload(outBoxEvent.sourcePayload),
            sourceUserId = OutboxEventMessageSourceUserId.of(outBoxEvent.sourceUserId?:TEST_USER.userId)

        )

        //When
        outboxEventSqSMessagePublisher.send(outboxEventMessage)

        //Then
        val received = await()
            .pollInterval(Duration.ofMillis(1000))
            .atMost(Duration.ofSeconds(3))
            .ignoreExceptions()
            .untilNotNull {
                sqsTemplate.receive(SQSTestQueueManager.TestQueueName.TEST_EVENT.queueName,String::class.java)
            }

        assertThat(received.get().payload).isEqualTo(outboxEventMessage.sourcePayload.value)


    }


}