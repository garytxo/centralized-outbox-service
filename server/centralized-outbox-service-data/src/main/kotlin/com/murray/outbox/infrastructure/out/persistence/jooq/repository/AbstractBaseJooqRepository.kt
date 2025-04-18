package com.murray.outbox.infrastructure.out.persistence.jooq.repository

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import org.jooq.DSLContext
import org.jooq.impl.DAOImpl
import org.slf4j.LoggerFactory

abstract class AbstractBaseJooqRepository<DAO>(
    private val dslContext: DSLContext,
    private val auditAwareService: AuditAwareService,
    protected val dao: DAO

) where DAO : DAOImpl<*, *, *> {

    private val logger = LoggerFactory.getLogger(javaClass)


    init {
        logger.info("DAO:${dao::class.java.simpleName} configured ")
    }

    fun dslContext() = dslContext.dsl()

    fun auditAwareService() = auditAwareService

}