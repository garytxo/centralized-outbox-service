/*
 * This file is generated by jOOQ.
 */
package com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.daos


import java.time.LocalDateTime
import java.util.UUID

import kotlin.collections.List

import org.jooq.Configuration
import org.jooq.JSONB
import org.jooq.impl.DAOImpl
import org.springframework.stereotype.Repository


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
@Repository
open class OutboxDomainEventDao(configuration: Configuration?) : DAOImpl<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.OutboxDomainEventRecord, com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent, UUID>(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT, com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent::class.java, configuration) {

    /**
     * Create a new OutboxDomainEventDao without any configuration
     */
    constructor(): this(null)

    override fun getId(o: com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent): UUID = o.id

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfId(lowerInclusive: UUID, upperInclusive: UUID): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ID, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    fun fetchById(vararg values: UUID): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ID, *values)

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    fun fetchOneById(value: UUID): com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent? = fetchOne(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ID, value)

    /**
     * Fetch records that have <code>event_type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfEventType(lowerInclusive: String, upperInclusive: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.EVENT_TYPE, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>event_type IN (values)</code>
     */
    fun fetchByEventType(vararg values: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.EVENT_TYPE, *values)

    /**
     * Fetch records that have <code>aggregate_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfAggregateId(lowerInclusive: String, upperInclusive: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.AGGREGATE_ID, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>aggregate_id IN (values)</code>
     */
    fun fetchByAggregateId(vararg values: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.AGGREGATE_ID, *values)

    /**
     * Fetch records that have <code>aggregate_type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfAggregateType(lowerInclusive: String, upperInclusive: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.AGGREGATE_TYPE, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>aggregate_type IN (values)</code>
     */
    fun fetchByAggregateType(vararg values: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.AGGREGATE_TYPE, *values)

    /**
     * Fetch records that have <code>event_data BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfEventData(lowerInclusive: JSONB, upperInclusive: JSONB): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.EVENT_DATA, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>event_data IN (values)</code>
     */
    fun fetchByEventData(vararg values: JSONB): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.EVENT_DATA, *values)

    /**
     * Fetch records that have <code>happened_on BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfHappenedOn(lowerInclusive: LocalDateTime?, upperInclusive: LocalDateTime?): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.HAPPENED_ON, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>happened_on IN (values)</code>
     */
    fun fetchByHappenedOn(vararg values: LocalDateTime): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.HAPPENED_ON, *values)

    /**
     * Fetch records that have <code>row_version BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfRowVersion(lowerInclusive: Int?, upperInclusive: Int?): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_VERSION, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>row_version IN (values)</code>
     */
    fun fetchByRowVersion(vararg values: Int): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_VERSION, *values.toTypedArray())

    /**
     * Fetch records that have <code>row_created_by BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfRowCreatedBy(lowerInclusive: String, upperInclusive: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_CREATED_BY, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>row_created_by IN (values)</code>
     */
    fun fetchByRowCreatedBy(vararg values: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_CREATED_BY, *values)

    /**
     * Fetch records that have <code>row_created_on BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfRowCreatedOn(lowerInclusive: LocalDateTime?, upperInclusive: LocalDateTime?): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_CREATED_ON, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>row_created_on IN (values)</code>
     */
    fun fetchByRowCreatedOn(vararg values: LocalDateTime): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_CREATED_ON, *values)

    /**
     * Fetch records that have <code>row_updated_by BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfRowUpdatedBy(lowerInclusive: String?, upperInclusive: String?): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_UPDATED_BY, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>row_updated_by IN (values)</code>
     */
    fun fetchByRowUpdatedBy(vararg values: String): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_UPDATED_BY, *values)

    /**
     * Fetch records that have <code>row_updated_on BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfRowUpdatedOn(lowerInclusive: LocalDateTime?, upperInclusive: LocalDateTime?): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetchRange(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_UPDATED_ON, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>row_updated_on IN (values)</code>
     */
    fun fetchByRowUpdatedOn(vararg values: LocalDateTime): List<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent> = fetch(com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.OutboxDomainEvent.OUTBOX_DOMAIN_EVENT.ROW_UPDATED_ON, *values)
}
