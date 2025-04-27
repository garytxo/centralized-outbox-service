package com.murray.outbox.test.sqs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest

@Service
class SQSTestQueueManager @Autowired private constructor(private val sqsAsyncClient: SqsAsyncClient) {

    enum class TestQueueName(val queueName: String, val queueEventType:String) {
        TEST_EVENT("test_event_queue","TEST_EVENT_TYPE"),
        TEST_DIRECT_SEND("test_direct_send_queue","TEST_DIRECT_SEND"),
    }


    fun createQueue(testQueueNames:TestQueueName)=
        sqsAsyncClient.createQueue(
            CreateQueueRequest.builder()
            .queueName(testQueueNames.queueName)
            .build()).join()
}