package com.murray.outbox.test

import com.murray.outbox.CentralizedOutboxServiceApplication
import com.murray.outbox.shared.infrastructure.out.persistence.LoggedUser
import com.murray.outbox.test.initializer.LocalStackContainerInitializer
import com.murray.outbox.test.initializer.PostgresqlContainerInitializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(
    classes = [CentralizedOutboxServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    initializers = [PostgresqlContainerInitializer::class, LocalStackContainerInitializer::class]
)
@ActiveProfiles("test")
abstract class BaseIntegrationTest{

     val TEST_USER = LoggedUser.generateTestUser()

}
