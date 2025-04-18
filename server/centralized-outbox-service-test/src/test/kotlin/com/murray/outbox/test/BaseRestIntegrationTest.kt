package com.murray.outbox.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.murray.outbox.test.data.OutBoxTestDataGenerator
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort

abstract  class BaseRestIntegrationTest: BaseIntegrationTest() {

    @Autowired
    lateinit var  outBoxTestDataGenerator : OutBoxTestDataGenerator
    @Autowired
    lateinit var  objectMapper: ObjectMapper

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port


    }
}