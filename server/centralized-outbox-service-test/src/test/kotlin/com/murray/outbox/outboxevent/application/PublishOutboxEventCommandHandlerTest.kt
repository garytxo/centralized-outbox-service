package com.murray.outbox.outboxevent.application

import com.murray.outbox.outboxevent.domain.exception.OutboxEventTypeNotFoundException
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageProcessId
import com.murray.outbox.outboxevent.domain.model.OutboxEventMessageSourceUserId
import com.murray.outbox.outboxevent.domain.model.OutboxEventType
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeQueue
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeValue
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.outboxevent.domain.model.OutboxSourcePayload
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventMessagePublisher
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.outboxevent.domain.port.out.PublishOutboxEventRepository
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class PublishOutboxEventCommandHandlerTest {

    private val eventDispatcher = mockk<DomainEventDispatcher>()
    private val outBoxEventTypeRepository = mockk<OutboxEventTypeRepository>()
    private val publishOutBoxEventRepository = mockk<PublishOutboxEventRepository>()
    private val outboxEventMessagePublisher = mockk<OutboxEventMessagePublisher>()
    private val sut = PublishOutboxEventCommandHandler(outBoxEventTypeRepository,publishOutBoxEventRepository, outboxEventMessagePublisher,eventDispatcher)

    @Test
    fun `execute should throw error when event type does exist`(){

        //Given
        val eventTypeName = "NO_EXIST"


        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } throws OutboxEventTypeNotFoundException(eventTypeName)

        //When
        val command = PublishOutboxEventCommand(eventTypeName)
        assertThrows<OutboxEventTypeNotFoundException> { sut.execute(command) }


        //Then
        verify(exactly = 0)  { eventDispatcher.dispatchEvent(any()) }
        verify(exactly = 0)  { eventDispatcher.dispatchEvent(any()) }
        verify(exactly = 1){ outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify(exactly = 0)  { publishOutBoxEventRepository.findAllPendingEventFor(any()) }
        verify(exactly = 0)  { publishOutBoxEventRepository.updateProcessedEvent(any()) }

    }

    @Test
    fun `execute should publish event message when the event type is valid`(){

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val activeEventType = OutboxEventType.load(
            id = OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())
        )
        val outboxEventMessage  = OutboxEventMessage.load(
            eventTypeId = OutboxEventTypeId(),
            sourceId = OutboxSourceId.asSource(UUID.randomUUID().toString()),
            processId = OutboxEventMessageProcessId(),
            sourcePayload = OutboxSourcePayload.asPayload(""),
            sourceUserId = OutboxEventMessageSourceUserId.of("TEST")

        )

        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns activeEventType
        every { publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) } returns setOf(outboxEventMessage)
        every { outboxEventMessagePublisher.send(outboxEventMessage) } just runs
        every { publishOutBoxEventRepository.updateProcessedEvent(ofType(OutboxEventMessage::class)) } just runs
        every { eventDispatcher.dispatchEvent(any()) } just runs

        //When
        val command = PublishOutboxEventCommand(eventTypeName)
        val response = sut.execute(command)

        //Then
        assertNotNull(response)
        assertThat(response).isNotNull()
        assertThat(response.eventType).isEqualTo(activeEventType.type.value)
        assertThat(response.totalEventSuccessfullySent).isEqualTo(1)
        assertThat(response.totalEventFailedSent).isEqualTo(0)
        assertThat(response.processGroupId).isEqualTo(outboxEventMessage.processId.id.toString())

        verify (exactly = 1){ outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify (exactly = 1){ publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) }
        verify(exactly = 1){outboxEventMessagePublisher.send(outboxEventMessage) }
        verify(exactly = 1)  { publishOutBoxEventRepository.updateProcessedEvent(outboxEventMessage) }
        verify(exactly = 1){ eventDispatcher.dispatchEvent(any()) }

    }

    @Test
    fun `execute should return summary with one error because an exception is thrown when publishing message`(){

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val activeEventType = OutboxEventType.load(
            id = OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())

        )
        val outboxEventMessage  = OutboxEventMessage.load(
            eventTypeId = OutboxEventTypeId(),
            sourceId = OutboxSourceId.asSource(UUID.randomUUID().toString()),
            processId = OutboxEventMessageProcessId(UUID.randomUUID()),
            sourcePayload = OutboxSourcePayload.asPayload(""),
            sourceUserId = OutboxEventMessageSourceUserId.of("TEST")
        )

        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns activeEventType
        every { publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) } returns setOf(outboxEventMessage)
        every { outboxEventMessagePublisher.send(outboxEventMessage) } throws IllegalArgumentException()
        every { publishOutBoxEventRepository.updateProcessedEvent(ofType(OutboxEventMessage::class)) } just runs
        every { eventDispatcher.dispatchEvent(any()) } just runs

        //WHEN
        val command = PublishOutboxEventCommand(eventTypeName)
        val response = sut.execute(command)

        //THEN
        assertNotNull(response)
        assertThat(response.eventType).isEqualTo(activeEventType.type.value)
        assertThat(response.totalEventSuccessfullySent).isEqualTo(0L)
        assertThat(response.totalEventFailedSent).isEqualTo(1)
        assertThat(response.processGroupId).isEqualTo(outboxEventMessage.processId.id.toString())

        verify (exactly = 1){ outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify (exactly = 1){ publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) }
        verify(exactly = 1){outboxEventMessagePublisher.send(outboxEventMessage) }
        verify(exactly = 1){ publishOutBoxEventRepository.updateProcessedEvent(outboxEventMessage) }
        verify(exactly = 1){ eventDispatcher.dispatchEvent(any()) }
        verify(exactly = 0){ eventDispatcher.dispatchAggregate(any()) }
    }

    @Test
    fun `execute should return summary with nothing sent nor errors when no events were found`(){

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val activeEventType = OutboxEventType.load(
            id = OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())
        )

        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns activeEventType
        every { publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) } returns emptySet()
        every { outboxEventMessagePublisher.send(any()) } just runs
        every { eventDispatcher.dispatchEvent(any()) } just runs

        //WHEN
        val command = PublishOutboxEventCommand(eventTypeName)
        val response = sut.execute(command)

        //THEN
        assertNotNull(response)
        assertThat(response.eventType).isEqualTo(activeEventType.type.value)
        assertThat(response.totalEventSuccessfullySent).isEqualTo(0L)
        assertThat(response.totalEventFailedSent).isEqualTo(0)
        assertThat(response.processGroupId).isEqualTo("")

        verify (exactly = 1){ outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify (exactly = 1){ publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) }
        verify(exactly = 0){outboxEventMessagePublisher.send(any()) }
        verify(exactly = 0){publishOutBoxEventRepository.updateProcessedEvent(any()) }
        verify(exactly = 0){ eventDispatcher.dispatchEvent(any()) }
        verify(exactly = 0){ eventDispatcher.dispatchAggregate(any()) }
    }

    @Test
    fun `execute should publish 2 unique message events `(){

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val activeEventType = OutboxEventType.load(
            id = OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())
        )

        val outboxEventMessage1  = OutboxEventMessage.load(
            eventTypeId = OutboxEventTypeId(),
            sourceId = OutboxSourceId.asSource(UUID.randomUUID().toString()),
            processId = OutboxEventMessageProcessId(UUID.randomUUID()),
            sourcePayload = OutboxSourcePayload.asPayload(""),
            sourceUserId = OutboxEventMessageSourceUserId.of("TEST1")
        )

        val outboxEventMessage2  = OutboxEventMessage.load(
            eventTypeId = OutboxEventTypeId(),
            sourceId = OutboxSourceId.asSource(UUID.randomUUID().toString()),
            processId = OutboxEventMessageProcessId(UUID.randomUUID()),
            sourcePayload = OutboxSourcePayload.asPayload(""),
            sourceUserId = OutboxEventMessageSourceUserId.of("TEST2")
        )

        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns activeEventType
        every { publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) } returns setOf(outboxEventMessage1, outboxEventMessage2)
        every { outboxEventMessagePublisher.send(any()) } just runs
        every { publishOutBoxEventRepository.updateProcessedEvent(ofType(OutboxEventMessage::class)) } just runs
        every { eventDispatcher.dispatchEvent(any()) } just runs

        //WHEN
        val command = PublishOutboxEventCommand(eventTypeName)
        val response = sut.execute(command)

        //THEN
        assertNotNull(response)
        assertThat(response.eventType).isEqualTo(activeEventType.type.value)
        assertThat(response.totalEventSuccessfullySent).isEqualTo(2L)
        assertThat(response.totalEventFailedSent).isEqualTo(0)
        assertThat(response.processGroupId).isEqualTo(outboxEventMessage1.processId.id.toString())

        verify (exactly = 1){ outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify (exactly = 1){ publishOutBoxEventRepository.findAllPendingEventFor(activeEventType) }
        verify(exactly = 2){outboxEventMessagePublisher.send(any()) }
        verify(exactly = 2)  { publishOutBoxEventRepository.updateProcessedEvent(any()) }
        verify(exactly = 2){ eventDispatcher.dispatchEvent(any()) }
    }


}