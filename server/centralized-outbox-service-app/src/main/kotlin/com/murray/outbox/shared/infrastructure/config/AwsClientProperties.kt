package com.murray.outbox.shared.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AwsClientProperties::class)
//@RefreshScope
class AWSPropertiesLoader

@ConfigurationProperties(prefix = "outbox.aws")
data class AwsClientProperties(
    val prefix: String,
    val region: String,
    val account: String,
    val accessKey: String,
    val secretAccessKey: String,
    val sqs: SQSProperties,
)
data class SQSProperties(
    val endpoint: String
)