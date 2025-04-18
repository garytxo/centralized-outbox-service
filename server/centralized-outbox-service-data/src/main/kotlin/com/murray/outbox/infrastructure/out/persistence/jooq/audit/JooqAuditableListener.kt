package com.murray.outbox.infrastructure.out.persistence.jooq.audit


import org.jooq.RecordContext
import org.jooq.RecordListener
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.LocalDateTime

/**
 * The customized JOOQ record listener that is used to populate the auditable record is insert and updated
 * it ensures that the audit field values are set before inserts, updates and deletes
 *
 * This is simplified using the JOOQ commercial version
 * https://www.jooq.org/doc/latest/manual/code-generation/codegen-advanced/codegen-config-database/codegen-database-forced-types/codegen-database-forced-types-audit/
 */
class JooqAuditableListener(private val auditAwareService: AuditAwareService) : RecordListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("loaded JooqAuditableListener")
    }

    override fun insertStart(ctx: RecordContext) {
        ctx.getAuditableAwareRecord()?.let {
            val userId = auditAwareService.getCurrentUserId()
            it.rowCreatedOn = LocalDateTime.now(Clock.systemUTC())
            it.rowCreatedBy = userId
            // Already set by default
            it.rowUpdatedBy = userId
            it.rowUpdatedOn = LocalDateTime.now(Clock.systemUTC())
            logger.debug("Insert ${ctx.getClassName()} audit fields for id:${it}")
        }


        super.insertStart(ctx)
    }

    override fun updateStart(ctx: RecordContext) {
        ctx.getAuditableAwareRecord()?.let {
            val userId = auditAwareService.getCurrentUserId()
            it.rowUpdatedBy = userId
            it.rowUpdatedOn = LocalDateTime.now(Clock.systemUTC())
            logger.debug("Update ${ctx.getClassName()}  audit fields for id:${it}")
        }
        super.updateStart(ctx)
    }

    override fun deleteStart(ctx: RecordContext) {
        if (ctx.record() is JooqAuditableAwareRecord) {
            val updatableRecord = ctx.record() as JooqAuditableAwareRecord
            logger.info("Deleting ${ctx.getClassName()}  with id:${updatableRecord}")
        }
        super.deleteStart(ctx)
    }

    private fun RecordContext.getClassName() =
        this.record()::class.simpleName ?: "unknown"

    private fun RecordContext.getAuditableAwareRecord(): JooqAuditableAwareRecord? {
        return if (this.record() is JooqAuditableAwareRecord) {
            this.record() as JooqAuditableAwareRecord
        } else {
            null
        }

    }
}