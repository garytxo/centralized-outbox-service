package com.murray.outbox.infrastructure.out.persistence.jooq.repository

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.daos.OutboxEventDao
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEvent
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.references.OUTBOX_EVENT
import com.murray.outbox.infrastructure.out.persistence.jooq.repository.dto.BatchOutboxMessage
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Repository
class OutboxEventJooqRepository (dslContext: DSLContext,
                                 auditAwareService: AuditAwareService,
                                 outboxEventDao: OutboxEventDao
): AbstractBaseJooqRepository<OutboxEventDao>(dslContext, auditAwareService, outboxEventDao){

    private val logger = LoggerFactory.getLogger(javaClass)

    fun save(outboxEvent: OutboxEvent):Int{
        val record = dslContext().newRecord(OUTBOX_EVENT, outboxEvent)
        record.sourceUserId = auditAwareService().getCurrentExternalUserId()
        return record.store()
    }

    fun findPendingEventAndUpdateStatusFor(eventTypeId: UUID): Set<BatchOutboxMessage> {
        val processId = UUID.randomUUID()
        val updated = updateOutboxEventForProcessingWithProcessId(eventTypeId, processId)
        logger.debug("Updated {} outbox events of type {} for processing with id{}", updated, eventTypeId, processId)
        return getProcessingEventFor(eventTypeId, processId)
    }

    private fun getProcessingEventFor(eventTypeId: UUID, processId: UUID) =
        dslContext().select(
            OUTBOX_EVENT.EVENT_TYPE_ID,OUTBOX_EVENT.SOURCE_EVENT_ID, OUTBOX_EVENT.PROCESS_GROUP_ID, DSL.max(OUTBOX_EVENT.SOURCE_PAYLOAD),
            DSL.max(OUTBOX_EVENT.SOURCE_USER_ID))
            .from(OUTBOX_EVENT)
            .where(OUTBOX_EVENT.EVENT_STATUS.eq(OutboxStatus.PROCESSING))
            .and(OUTBOX_EVENT.EVENT_TYPE_ID.eq(eventTypeId))
            .and(OUTBOX_EVENT.PROCESS_GROUP_ID.eq(processId))
            .groupBy(OUTBOX_EVENT.EVENT_TYPE_ID,OUTBOX_EVENT.SOURCE_EVENT_ID,OUTBOX_EVENT.PROCESS_GROUP_ID)
            .fetchSet{ record->
                BatchOutboxMessage(
                    record.value1()!!,
                    record.value2()!!,
                    record.value3()!!,
                    record.value4()!!,
                    record.value5()?: DEFAULT_USER,
                )
            }

    private fun updateOutboxEventForProcessingWithProcessId(eventTypeId: UUID, processId: UUID) =
        dslContext().update(OUTBOX_EVENT)
            .set(OUTBOX_EVENT.EVENT_STATUS,OutboxStatus.PROCESSING)
            .set(OUTBOX_EVENT.PROCESS_GROUP_ID,processId)
            .set(OUTBOX_EVENT.ROW_UPDATED_ON, LocalDateTime.now(Clock.systemUTC()))
            .where(OUTBOX_EVENT.EVENT_TYPE_ID.eq(eventTypeId)
                .and(OUTBOX_EVENT.EVENT_STATUS.eq(OutboxStatus.PENDING))
                .and(OUTBOX_EVENT.PROCESS_GROUP_ID.isNull)
            ).execute()


    fun saveWithError(batchOutboxMessage:BatchOutboxMessage, processErrorMessage:String){
        dslContext().update(OUTBOX_EVENT)
            .set(OUTBOX_EVENT.EVENT_STATUS,OutboxStatus.ERROR)
            .set(OUTBOX_EVENT.PROCESSED_ERROR, processErrorMessage)
            .set(OUTBOX_EVENT.PROCESSED_ON, LocalDateTime.now(Clock.systemUTC()))
            .set(OUTBOX_EVENT.ROW_UPDATED_ON, LocalDateTime.now(Clock.systemUTC()))
            .where(OUTBOX_EVENT.SOURCE_EVENT_ID.eq(batchOutboxMessage.sourceId)
                .and(OUTBOX_EVENT.PROCESS_GROUP_ID.eq(batchOutboxMessage.processGroupId))
            ).execute()
    }

    fun saveWithSuccess(batchOutboxMessage:BatchOutboxMessage){
        dslContext().update(OUTBOX_EVENT)
            .set(OUTBOX_EVENT.EVENT_STATUS,OutboxStatus.PROCESSED)
            .set(OUTBOX_EVENT.PROCESSED_ON, LocalDateTime.now(Clock.systemUTC()))
            .set(OUTBOX_EVENT.ROW_UPDATED_ON, LocalDateTime.now(Clock.systemUTC()))
            .where(OUTBOX_EVENT.SOURCE_EVENT_ID.eq(batchOutboxMessage.sourceId)
                .and(OUTBOX_EVENT.PROCESS_GROUP_ID.eq(batchOutboxMessage.processGroupId))
            ).execute()
    }


    companion object {
        const val DEFAULT_USER = "TECHNICAL_USER"
        object OutboxStatus{
            val PENDING = "PENDING"
            val PROCESSING = "PROCESSING"
            val PROCESSED = "PROCESSED"
            val ERROR = "ERROR"

        }
    }
}