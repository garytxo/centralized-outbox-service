package com.murray.outbox.test

import com.murray.outbox.test.data.OutBoxTestDataGenerator
import com.murray.outbox.test.sqs.SQSTestQueueManager
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseNoneRestIntegrationTest: BaseIntegrationTest() {

    @Autowired
    lateinit var outBoxTestDataGenerator : OutBoxTestDataGenerator

    @Autowired
    lateinit var sqsTestQueueManager: SQSTestQueueManager


}