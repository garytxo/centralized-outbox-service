package com.murray.outbox.test.initializer


import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service
import org.testcontainers.utility.DockerImageName

class LocalStackContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {

        private const val LOCALSTACK_ACCESS_KEY = "TESTCONTAINERS_LOCALSTACK_ACCESS_KEY"
        private const val LOCALSTACK_SECRET_ACCESS_KEY = "TESTCONTAINERS_LOCALSTACK_SECRET_ACCESS_KEY"
        private const val LOCALSTACK_ENDPOINT = "TESTCONTAINERS_LOCALSTACK_ENDPOINT"
        private const val LOCALSTACK_REGION = "TESTCONTAINERS_LOCALSTACK_REGION"
    }

    private val localStackContainer = LocalStackContainer(DockerImageName.parse("localstack/localstack:3.7"))
        .apply {
            withExposedPorts(4566)
            withServices(Service.SQS)
            withEnv(
                mapOf(
                    "AWS_ACCESS_KEY_ID" to "accessKey",
                    "AWS_SECRET_ACCESS_KEY" to "secretAccessKey",
                    "DEFAULT_REGION" to "eu-west-1",
                )
            )
        }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        localStackContainer.start()

        TestPropertyValues.of(
            mapOf(
                LOCALSTACK_ACCESS_KEY to localStackContainer.accessKey,
                LOCALSTACK_SECRET_ACCESS_KEY to localStackContainer.secretKey,
                LOCALSTACK_ENDPOINT to localStackContainer.getEndpointOverride(Service.SQS).toString(),
                LOCALSTACK_REGION to localStackContainer.region
            ),
        ).applyTo(applicationContext.environment)

    }

}