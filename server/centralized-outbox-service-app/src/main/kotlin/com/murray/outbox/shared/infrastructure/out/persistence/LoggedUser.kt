package com.murray.outbox.shared.infrastructure.out.persistence

import java.util.UUID

class LoggedUser private constructor(
    val id : UUID,
    val userId : String,
) {

    companion object {

        fun generateTestUser() = LoggedUser(
            id = UUID.fromString("0f00b5ec-1efd-425d-9fb4-3e72f01e1609"),
            userId = "12345",
        )
    }
}