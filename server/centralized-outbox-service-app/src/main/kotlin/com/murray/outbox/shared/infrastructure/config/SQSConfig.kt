package com.murray.outbox.shared.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
class SQSConfig (
    private val awsClientProperties: AwsClientProperties,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun sqsAsyncClient() = awsClientProperties.toSqsAsyncClient()


    init {
        logger.info("Configuring SQS Async Client Properties $awsClientProperties")
    }

    @Bean
    @Primary
    fun sqsTemplate(): SqsTemplate {
        //The SqsMessagingMessageConverter by default does not pick up our defined objectMapper which means
        // that we could run into issues when serializing timestamps fields etc...
        // Therefore, to overcome this issue we need to instantiate like this to set our customized jackson mapper
        val converter: SqsMessagingMessageConverter = object : SqsMessagingMessageConverter() {}
        converter.setObjectMapper(this.objectMapper)

        return SqsTemplate.builder()
            .sqsAsyncClient(sqsAsyncClient())
            .messageConverter(converter)
            .configure { it.queueNotFoundStrategy(QueueNotFoundStrategy.FAIL) }
            .build()
    }

    private fun AwsClientProperties.toSqsAsyncClient(): SqsAsyncClient {
        return if (this.isAccessKeyEmpty()) {
            SqsAsyncClient
                .builder()
                .region(this.toRegion())
                .endpointOverride(this.toEndpointUri())
                .build()
        } else {
            SqsAsyncClient
                .builder()
                .region(this.toRegion())
                .endpointOverride(this.toEndpointUri())
                .credentialsProvider(
                    StaticCredentialsProvider
                        .create(this.toAwsBasicCredentials())
                )
                .build()
        }

    }

    private fun AwsClientProperties.isAccessKeyEmpty() = this.accessKey.isEmpty()

    private fun AwsClientProperties.toEndpointUri() = URI(this.sqs.endpoint)

    private fun AwsClientProperties.toRegion() = software.amazon.awssdk.regions.Region.of(this.region)

    private fun AwsClientProperties.toAwsBasicCredentials() =
        software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(this.accessKey, this.secretAccessKey)
}