package com.murray.outbox.shared.infrastructure.out.message

import com.murray.outbox.outboxevent.domain.model.OutboxEventMessage
import com.murray.outbox.outboxevent.domain.model.OutboxEventType
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventMessagePublisher
import com.murray.outbox.outboxevent.domain.port.out.OutboxEventTypeRepository
import com.murray.outbox.shared.infrastructure.config.AwsClientProperties
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.LoggerFactory
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class OutboxEventSqSMessagePublisher(
    private val sqsTemplate: SqsTemplate,
    private val outboxEventTypeRepository: OutboxEventTypeRepository,
    private val awsClientProperties: AwsClientProperties
) : OutboxEventMessagePublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun send(outboxEventMessage: OutboxEventMessage) {
        val eventType = outboxEventTypeRepository.findBy(outboxEventMessage.eventTypeId)
        logger.info("Publishing event for sourceId:${outboxEventMessage.sourceId} with payload:${outboxEventMessage.sourcePayload.value} to queueName:${eventType.queueName}")

        val result = sqsTemplate.send(
            eventType.fullEndpoint(),
            MessageBuilder
                .withPayload(outboxEventMessage.sourcePayload.value)
                .setHeader("externalUserId",outboxEventMessage.sourceUserId.value)
                .build()
        )

        logger.info("Published event messageId:${result}")

    }

    private fun OutboxEventType.fullEndpoint(): String {
        return if (awsClientProperties.prefix.isBlank()){
            this.queueName.value
        }else {
            "${awsClientProperties.prefix}${this.queueName.value}"
        }
    }
}