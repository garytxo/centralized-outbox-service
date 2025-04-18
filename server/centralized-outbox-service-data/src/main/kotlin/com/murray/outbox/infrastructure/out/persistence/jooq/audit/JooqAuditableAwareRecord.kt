package com.murray.outbox.infrastructure.out.persistence.jooq.audit

import java.time.LocalDateTime

interface JooqAuditableAwareRecord {
    var rowVersion: Int?
    var rowCreatedBy: String
    var rowCreatedOn: LocalDateTime?
    var rowUpdatedBy: String?
    var rowUpdatedOn: LocalDateTime?
}