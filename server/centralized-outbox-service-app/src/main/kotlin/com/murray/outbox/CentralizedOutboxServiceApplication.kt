package com.murray.outbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CentralizedOutboxServiceApplication

fun main(args: Array<String>) {
    runApplication<CentralizedOutboxServiceApplication>(*args)
}