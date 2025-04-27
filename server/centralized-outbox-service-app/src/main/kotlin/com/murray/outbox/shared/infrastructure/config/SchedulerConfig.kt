package com.murray.outbox.shared.infrastructure.config

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jooq.JooqLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Clock
import java.util.concurrent.ScheduledThreadPoolExecutor


@Configuration
@EnableAspectJAutoProxy
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT59S")
class SchedulerConfig(private val dslContext: DSLContext) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    @Primary
    fun getLockProvider(): LockProvider
    = JooqLockProvider(dslContext)

    @Bean
    fun taskScheduler(): TaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.clock = Clock.systemUTC()
        threadPoolTaskScheduler.setThreadNamePrefix("outbox-thread")
        threadPoolTaskScheduler.poolSize = 20
        threadPoolTaskScheduler.setErrorHandler {
            logger.error("Error while running outbox-thread caused:${it.message} ", it)
        }
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true)
        threadPoolTaskScheduler.initialize()
        return threadPoolTaskScheduler
    }

}