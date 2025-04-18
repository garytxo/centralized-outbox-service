/*
 * This file is generated by jOOQ.
 */
package com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.interfaces


import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
interface IOutboxEventType : Serializable {
    var id: UUID
    var eventType: String
    var active: Boolean?
    var description: String
    var queueName: String
    var scheduledCron: String
    var scheduledLockAtMostFor: String?
    var scheduledLockAtLeastFor: String?
    var rowVersion: Int?
    var rowCreatedBy: String
    var rowCreatedOn: LocalDateTime?
    var rowUpdatedBy: String?
    var rowUpdatedOn: LocalDateTime?

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IOutboxEventType
     */
    fun from(from: com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.interfaces.IOutboxEventType)

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IOutboxEventType
     */
    fun <E : com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.interfaces.IOutboxEventType> into(into: E): E
}
