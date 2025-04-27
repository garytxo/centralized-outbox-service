package com.murray.outbox.outboxevent.application

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import com.murray.outbox.outboxevent.domain.exception.OutboxEventTypeNotFoundException
import com.murray.outbox.outboxevent.domain.exception.OutboxSourceIdInvalidException
import com.murray.outbox.outboxevent.domain.model.OutboxEventType
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeQueue
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeValue
import com.murray.outbox.outboxevent.domain.port.out.CreateOutboxEventRepository
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventMessagePublisher
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.shared.domain.event.DomainEventDispatcher
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull

class CreateOutboxEventCommandHandlerTest {
    private val createOutBoxEventRepository = mockk<CreateOutboxEventRepository>(relaxed = true)
    private val auditAwareService = mockk<AuditAwareService>()
    private val eventDispatcher = mockk<DomainEventDispatcher>()
    private val outBoxEventTypeRepository = mockk<OutboxEventTypeRepository>()
    private val outboxEventMessagePublisher = mockk<OutboxEventMessagePublisher>()
    private val sut = CreateOutboxEventCommandHandler(createOutBoxEventRepository,auditAwareService,outBoxEventTypeRepository,outboxEventMessagePublisher,eventDispatcher)

    @Test
    fun `execute should save new outbox event`() {

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val eventType = OutboxEventType.load(
            id =  OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())
        )
        val command = CreateOutboxEventCommand(
            eventId = "event-id",
            eventType = eventTypeName,
            eventPayload = "event-payload",
            sendDirectly = false,
        )
        every { createOutBoxEventRepository.save(any()) } returns Unit
        every { eventDispatcher.dispatchAggregate(any()) } returns Unit
        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns eventType

        //When
        val result =sut.execute(command)


        //Then
        assertNotNull(result)

        verify { createOutBoxEventRepository.save(any()) }
        verify {  eventDispatcher.dispatchAggregate(any()) }
        verify { outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify(exactly = 0) { outboxEventMessagePublisher.send(any()) }


    }

    @Test
    fun `execute should error when source id is invalid`() {

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val eventType = OutboxEventType.load(
            id =  OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())
        )
        val command = CreateOutboxEventCommand(
            eventId = "",
            eventType = "RECALCULATE_REPORT_FOR_ACCOUNT",
            eventPayload = "event-payload",
            sendDirectly = false,
        )

        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns eventType


        //When
        assertThrows<OutboxSourceIdInvalidException> { sut.execute(command) }


        //Then
        verify(exactly = 0) { createOutBoxEventRepository.save(any()) }
        verify(exactly = 0)  { eventDispatcher.dispatchAggregate(any()) }
        verify(exactly = 1) { outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify(exactly = 0) { outboxEventMessagePublisher.send(any()) }

    }

    @Test
    fun `execute should throw exception when force is false and event type not found`() {

        //Given
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT_NOT_FOUND"
        val command = CreateOutboxEventCommand(
            eventId = "event-id",
            eventType = eventTypeName,
            eventPayload = "event-payload",
            sendDirectly = false,
        )
        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } throws  OutboxEventTypeNotFoundException("test")

        //When
        assertThrows<OutboxEventTypeNotFoundException> {
            sut.execute(command)
        }

        verify(exactly = 0) { createOutBoxEventRepository.save(any()) }
        verify(exactly = 0)  { eventDispatcher.dispatchAggregate(any()) }
        verify(exactly = 1) { outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify(exactly = 0) { outboxEventMessagePublisher.send(any()) }


    }

    @Test
    fun `execute should not call save when force is true`() {

        // Given
        val currentUserId = "TEST"
        val eventTypeName = "RECALCULATE_REPORT_FOR_ACCOUNT"
        val eventType = OutboxEventType.load(
            id =  OutboxEventTypeId(),
            type = OutboxEventTypeValue.of(eventTypeName),
            queueName = OutboxEventTypeQueue.of(eventTypeName.lowercase())
        )
        val command = CreateOutboxEventCommand(
            eventId = "event-id",
            eventType = eventTypeName,
            eventPayload = "event-payload",
            sendDirectly = true,
        )

        every { outBoxEventTypeRepository.findBy(eq(eventTypeName)) } returns eventType
        every { eventDispatcher.dispatchEvent(any()) } returns Unit
        every {outboxEventMessagePublisher.send(any())  } just Runs
        every { auditAwareService.getCurrentExternalUserId() } returns currentUserId

        // When
        val result = sut.execute(command)

        // Then
        assertNotNull(result)
        verify(exactly = 0) { createOutBoxEventRepository.save(any()) }
        verify(exactly = 1) { eventDispatcher.dispatchEvent(any()) }
        verify(exactly = 1) { outBoxEventTypeRepository.findBy(eq(eventTypeName)) }
        verify(exactly = 1) { outboxEventMessagePublisher.send(any()) }
        verify(exactly = 1) { auditAwareService.getCurrentExternalUserId() }
    }

}