package com.murray.outbox.infrastructure.out.persistence.jooq.repository

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.daos.OutboxEventTypeDao
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxEventType as OutboxEventTypePojo
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OutboxEventTypeJooqRepository(
    dslContext: DSLContext,
    auditAwareService: AuditAwareService,
    outboxEventTypeDao: OutboxEventTypeDao
): AbstractBaseJooqRepository<OutboxEventTypeDao>(dslContext, auditAwareService, outboxEventTypeDao) {

    fun findByType(eventType: String): OutboxEventTypePojo?
            = dao.fetchByEventType(eventType).firstOrNull()

    fun findById(id: UUID): OutboxEventTypePojo?
            = dao.findById(id)

    fun findAllActiveEventsType()
            = dao.fetchByActive(true)

}