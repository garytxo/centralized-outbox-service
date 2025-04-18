package com.murray.outbox.infrastructure.out.persistence.jooq.audit

interface AuditAwareService {

    fun getCurrentUserId(): String {
        return "TO_BE_REPLACED"
    }

    fun getCurrentExternalUserId(): String {
        return "TO_BE_REPLACED"
    }
}