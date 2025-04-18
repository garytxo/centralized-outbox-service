package com.murray.outbox.shared.infrastructure.out.persistence

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.AuditAwareService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LoggedUserAuditAwareService : AuditAwareService {

    companion object {
        val TEST_USER = UUID.fromString("0f00b5ec-1efd-425d-9fb4-3e72f01e1609")
    }


    override fun getCurrentUserId() = TEST_USER.toString()

    override fun getCurrentExternalUserId() = TEST_USER.toString()


}