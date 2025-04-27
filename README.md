# Centralized Outbox Service

Centralized Outbox Service with REST Integration

## Table of contents
- [Getting started](#getting-started)
   * [Prerequisites](#prerequisites)
   * [Frameworks and Tools](#frameworks-and-tools-used)
- [Installation](#installation)
   * [1. Get the code](#1-get-the-code)
   * [2. Create DB schema](#2-create-db-schema)
   * [3. Build and verify project](#3-build-and-verify-project)
- [How this repository is organized](#how-this-repository-is-organized)
- [How Centralized Outbox Service Works](#how-the-centralized-outbox-service-works)
   * [Architecture Design](#architecture-diagram)
   * [Outbox Service API](#outbox-service-api)
   * [Outbox Service Background Job](#outbox-service-background-job)
- [How support a new event](#how-support-a-new-event-)
- [Things to consider in future releases](#things-to-consider-in-future-releases)


## Getting Started

### Prerequisites
To build and run the application, ensure you have:
* Java JDK (v17+)
* Maven: (v3+)
* Docker Desktop
* Kotlin (1.9.25) (via Maven plugin)

## Frameworks and Tools Used
- **Java** JDK (v17+)
- **Maven** (v3+)
- **Kotlin** (1.8.20) (via Maven plugin)
- [Shedlock](https://github.com/lukas-krecan/ShedLock)
- [Spring Cloud AWS](https://awspring.io/)
- [JOOQ](https://www.jooq.org/)
- [Flyway](https://www.red-gate.com/products/flyway/community/)
- **OpenAPI**: For REST API documentation
- **JUnit 5**: For unit testing
- **REST Assured**: For integration testing
- **Docker**: For local environment setup
- **Test Containers** v1.19.8
- [Test Container JOOQ Code Generator Maven Plugin](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin) v0.0.4

## Installation

### 1. Get the code
The code can be found in GitHub repo https://github.com/garytxo/centralized-outbox-service

Clone repository locally using

```shell 
 git clone git@github.com:garytxo/centralized-outbox-service.git
```

### 2. Create DB schema

Create the empty database using the following
```shell
    psql -h localhost -p 5432 -U outbox -c "CREATE DATABASE central_outbox";
```

If you need to DROP the database execute
```shell
    psql -h localhost -p 5432 -U outbox -c "DROP DATABASE central_outbox";
```

The default user/password is `outbox/outbox`

### 3. Build and verify project

Install dependencies and run test

```shell
    cd server
    mvn clean install
```

Verify code coverage

```shell
  cd server
  mvn clean verify
```

## How this repository is organized
This project is structured into three core modules:

### 1. `centralized-outbox-service-data`
- **Purpose**: Manages database operations, including schema migration with Flyway and query generation using JOOQ.
- **Contents**: Auto-generated JOOQ code, database repositories, and migrations.

### 2. `centralized-outbox-service-api`
- **Purpose**: Defines all the common REST API endpoints contracts and communication messages definitions, that can shared with other applications.
- **Contents**: REST API Controller Interface and their corresponding Request and Response contracts and communication message definitions.

### 2. `centralized-outbox-service-app`
- **Purpose**: The main Spring Boot application that exposes the REST APIs.
- **Contents**: Business logic implementing **DDD** principles. Contains bounded contexts for accounts and transactions.

### 3. `centralized-outbox-service-test`
- **Purpose**: Aggregates all tests for `finmid-financial-data` and `finmid-financial-app`.
- **Contents**: Unit tests (Junit 5), integration tests (REST Assured), and test utilities.


## How The Centralized Outbox Service Works
The primary responsibility of the outbox service aims to address most of the 
pain points with queues and projections while allowing room for scalability.

### **Architecture Diagram**
```
+--------------------+          +--------------------+
| Microservice A     |          | Microservice B     |
|                    |          |                    |
| +----------------+ |          | +----------------+ |
| | Main DB Table  | |          | | Main DB Table  | |
| +----------------+ |          | +----------------+ |
|      REST API Call            |      REST API Call
|           |                              |
+-----------v------------------------------v------------+
            |      Centralized Outbox Service           |
            |  +-------------------------------------+  |
            |  | Centralized Outbox Table (DB)       |  |
            |  | Polling & Message Publishing        |  |
            |  +-------------------------------------+  |
            +-------------------------------------------+
                              |
                              v
                    Message Broker (e.g., Kafka, SQS, RabbitMQ)
```

#### **Key Advantages**
1. **Simpler Microservices**:
- Microservices no longer need their own outbox table or polling logic.
- Their responsibility is limited to calling the outbox service via REST.

2. **Centralized Failure Handling**:
- All retries, dead-letter queue management, and status tracking are managed by the outbox service.

3. **Consistent Event Management**:
- One place to monitor and process all events, ensuring consistent logic and behavior.

4. **Easier Scalability**:
- The outbox service can scale independently based on the load (e.g., by horizontally scaling).


### **Outbox Service API**
1. **Other Microservice**
   When an event occurs the application send the details to the out box service via REST API call instead of publishing directly to the message broker
```shell
curl -X 'POST' \
  'http://localhost:3001/api-outbox-service/v1/event' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "eventType": "RECALCULATE_REPORT_FOR_ACCOUNT",
  "sourceId": "c2d6b809-07bd-43d6-bae2-305a15a37017",
  "sourcePayload": "{\"projectionOperation\":\"RECALCULATE_REPORT_FOR_ACCOUNT\",\"accountId\":\"c2d6b809-07bd-43d6-bae2-305a15a37017\"}",
  "skip": false
}'

``` 

2. **Outbox Service**
   Accept the REST Request for the following contract which are persisted to the **outbox_event** table
```kotlin
class CreateOutboxEventRequest(
    val eventType: String,
    val sourceId: String,
    val sourcePayload: String,
    val skip: Boolean = false
)
```
- Processes events from the **outbox_event** table (publishing to the message broker) in a background process.

3. **Outbox Event Database table**
All events which are not directly published to the message broker are persisted to the table  **outbox_event**:
Then these events are then processed by the background job, which is explained below,  that query the events grouping by `event_type_id` and `source_event_id`
thus reducing publishing duplicated events.


### **Outbox Service Background Job**
The Outbox Service will have a background job that polls the outbox table and  publish the event payload to a particular message broker.



## How support a new event
The following explain how to configure the outbox service to handle a new event from another service and publish to a message broker

Define a new event type in the **outbox_event_type** table with the following :
- Define a unique **event_type** name which the external service needs to use when publishing new events
- Define the new event type **queue_name** where **infra** needs to ensure that the outbox service is permitted to publish event to it
- Define the new event type **scheduled_cron** which explains how often the events are aggregated and published to the message broker
- Define the new event type **scheduled_lock_at_most_for** and **scheduled_lock_at_least_for** shedlock parameters.


## Useful info 

[Outbox REST API Swagger](http://localhost:3001/api-outbox-service/swagger-ui/index.html#)

## Things to consider in future releases
- Add GH workflow to run test in PRs
- Update localstack to create queues defined in the flyway for running locally
- Support further message brokers
