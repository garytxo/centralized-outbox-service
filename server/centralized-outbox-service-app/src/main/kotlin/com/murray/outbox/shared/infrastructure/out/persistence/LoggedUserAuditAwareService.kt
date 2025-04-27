package com.murray.outbox.shared.infrastructure.out.persistence

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService

class LoggedUserAuditAwareService : AuditAwareService {

    companion object {
        val TEST_USER = LoggedUser.generateTestUser()
    }


    override fun getCurrentUserId() = TEST_USER.id.toString()

    override fun getCurrentExternalUserId() = TEST_USER.userId


}