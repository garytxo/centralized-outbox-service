package com.murray.outbox.shared.infrastructure.config

import com.murray.outbox.infrastructure.out.persistence.jooq.audit.JooqAuditableListener
import com.murray.outbox.infrastructure.out.persistence.jooq.listener.SpringExceptionTranslationExecuteListener
import com.murray.outbox.shared.infrastructure.out.persistence.LoggedUserAuditAwareService
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultExecuteListenerProvider
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class JooqPersistenceConfig : DefaultConfigurationCustomizer {

    override fun customize(config: DefaultConfiguration) {
        config.setSQLDialect(SQLDialect.POSTGRES)
        config.setExecuteListenerProvider(DefaultExecuteListenerProvider(SpringExceptionTranslationExecuteListener()))
        config.set(jooqAuditRecordListener())

        val settings: Settings = Settings()
            .withRenderFormatted(true)
            .withBatchSize(10_000)
            .withExecuteWithOptimisticLocking(true)
            .withUpdateRecordVersion(true)
            .withUpdateRecordTimestamp(true)
            .withRenderMapping(
                RenderMapping()
                    .withSchemata(
                        MappedSchema().withInput("") //Default schema
                            .withOutput("central_outbox")
                    )

            )


        config.setSettings(settings)
    }

    @Bean
    fun jooqAuditRecordListener() = JooqAuditableListener(loggedUserAuditAwareService())

    @Bean
    fun loggedUserAuditAwareService() = LoggedUserAuditAwareService()
}