package com.murray.outbox.test.data


import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.Shedlock
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEvent
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.records.OutboxDomainEventRecord
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.records.OutboxEventRecord
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.references.OUTBOX_DOMAIN_EVENT
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.references.OUTBOX_EVENT
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.references.OUTBOX_EVENT_TYPE
import com.murray.outbox.outboxevent.domain.model.OutboxEventTypeId
import com.murray.outbox.outboxevent.domain.model.OutboxSourceId
import com.murray.outbox.test.objectmother.OutboxEventObjectMother
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class OutBoxTestDataGenerator @Autowired private constructor(private val dslContext: DSLContext) {

    fun generateEventsWithSameSourceIdAndEventTypeFor(outboxEvent: OutboxEvent, numberOfEvents: Int):Int {

        var stored =  this.saveOutboxEvent(outboxEvent)
        for (i in 1..<numberOfEvents) {

            val eventJooq = OutboxEventObjectMother.buildJooqEntity {
                eventTypeId = OutboxEventTypeId(outboxEvent.eventTypeId)
                sourceId = OutboxSourceId.asSource(outboxEvent.sourceEventId)
            }
            stored +=  this.saveOutboxEvent(eventJooq)
        }

        return stored
    }

    fun saveEventType(eventType: OutboxEventType) {
        dslContext.insertInto(OUTBOX_EVENT_TYPE)
            .set(OUTBOX_EVENT_TYPE.ID,eventType.id)
            .set(OUTBOX_EVENT_TYPE.EVENT_TYPE, eventType.eventType)
            .set(OUTBOX_EVENT_TYPE.ACTIVE, eventType.active)
            .set(OUTBOX_EVENT_TYPE.SCHEDULED_CRON, eventType.scheduledCron)
            .set(OUTBOX_EVENT_TYPE.SCHEDULED_LOCK_AT_MOST_FOR, eventType.scheduledLockAtMostFor)
            .set(OUTBOX_EVENT_TYPE.SCHEDULED_LOCK_AT_LEAST_FOR, eventType.scheduledLockAtLeastFor)
            .set(OUTBOX_EVENT_TYPE.QUEUE_NAME, eventType.queueName)
            .set(OUTBOX_EVENT_TYPE.DESCRIPTION, eventType.description)
            .set(OUTBOX_EVENT_TYPE.ROW_CREATED_BY, eventType.rowCreatedBy)
            .set(OUTBOX_EVENT_TYPE.ROW_CREATED_ON, eventType.rowCreatedOn)
            .onConflict().doNothing()
            .execute()
    }

    fun saveOutboxEvent(outboxEvent:OutboxEvent) =
        dslContext.insertInto(OUTBOX_EVENT)
            .set(OUTBOX_EVENT.ID,outboxEvent.id)
            .set(OUTBOX_EVENT.SOURCE_EVENT_ID,outboxEvent.sourceEventId)
            .set(OUTBOX_EVENT.SOURCE_PAYLOAD,outboxEvent.sourcePayload)
            .set(OUTBOX_EVENT.EVENT_TYPE_ID,outboxEvent.eventTypeId)
            .set(OUTBOX_EVENT.EVENT_STATUS,outboxEvent.eventStatus)
            .set(OUTBOX_EVENT.ROW_CREATED_BY,outboxEvent.rowCreatedBy)
            .set(OUTBOX_EVENT.ROW_UPDATED_BY, outboxEvent.rowUpdatedBy)
            .onConflict().doNothing()
            .execute()


    fun truncateOutboxEvent() = dslContext.truncate(OUTBOX_EVENT).execute()

    fun findOutBoxEventById(id: UUID): OutboxEventRecord {
        return dslContext.selectFrom(OUTBOX_EVENT)
            .where(OUTBOX_EVENT.ID.eq(id))
            .fetchOne() ?: throw NullPointerException("No outbox event found for id $id")
    }

    fun findOutBoxEventTypeById(type: String): OutboxEventType {

        return dslContext.selectFrom(OUTBOX_EVENT_TYPE)
            .where(OUTBOX_EVENT_TYPE.EVENT_TYPE.eq(type))
            .fetchOne()
            ?.let { record->
                OutboxEventType(
                    id = record.id,
                    eventType = record.eventType,
                    active = record.active,
                    scheduledCron = record.scheduledCron,
                    scheduledLockAtMostFor = record.scheduledLockAtMostFor,
                    scheduledLockAtLeastFor = record.scheduledLockAtLeastFor,
                    queueName = record.queueName,
                    description = record.description,
                    rowCreatedBy = record.rowCreatedBy
                )
            }
            ?: throw NullPointerException("No outbox event type found for type $type")
    }

    fun findDomainEventByAggregateId(id: String): OutboxDomainEventRecord {
        return dslContext.selectFrom(OUTBOX_DOMAIN_EVENT)
            .where(OUTBOX_DOMAIN_EVENT.AGGREGATE_ID.eq(id))
            .fetchOne() ?: throw NullPointerException("No domain event found for id $id")
    }

    fun findDomainEventsByAggregateId(id: String): List<OutboxDomainEventRecord> {
        return dslContext.selectFrom(OUTBOX_DOMAIN_EVENT)
            .where(OUTBOX_DOMAIN_EVENT.AGGREGATE_ID.eq(id))
            .fetch()
    }

    fun findShedlockCountFor(taskName: String) = dslContext.select(DSL.count())
        .from(Shedlock.SHEDLOCK)
        .where(Shedlock.SHEDLOCK.NAME.eq(taskName))
        .fetchOne(0,Int::class.java)?:0

}