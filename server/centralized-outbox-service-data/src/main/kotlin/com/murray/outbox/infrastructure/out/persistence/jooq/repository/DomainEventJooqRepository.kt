package com.murray.outbox.infrastructure.out.persistence.jooq.repository

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.daos.OutboxDomainEventDao
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.pojos.OutboxDomainEvent
import com.murray.outbox.infrastructure.out.persistence.jooq.codegen.tables.references.OUTBOX_DOMAIN_EVENT
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class DomainEventJooqRepository(
    dslContext: DSLContext,
    auditAwareService: AuditAwareService,
    domainEventDao: OutboxDomainEventDao
) : AbstractBaseJooqRepository<OutboxDomainEventDao>(dslContext,auditAwareService, domainEventDao) {

    fun store(domainEvent: OutboxDomainEvent){
        val eventRecord = dslContext().newRecord(OUTBOX_DOMAIN_EVENT, domainEvent)
        eventRecord.store()
    }
}