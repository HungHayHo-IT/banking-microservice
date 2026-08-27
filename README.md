
# 🏦 Banking Microservices

A production-oriented **Banking Microservices Backend** built with **Java, Spring Boot, Spring Cloud, Apache Kafka, Redis, MySQL, Docker, Kubernetes and GitHub Actions**.

The project is designed to demonstrate how a traditional banking backend can be decomposed into independent microservices while applying practical distributed-system patterns such as **Service Discovery, API Gateway, Event-Driven Architecture, Transactional Outbox, Idempotency, Resilience and Database-per-Service**.

> 🎯 **Project goal:** Build a realistic banking backend suitable for learning and demonstrating Java Backend / Microservices engineering skills at Fresher–Junior level.

---

## 📌 Table of Contents

* [Overview](#-overview)
* [Architecture](#-architecture)
* [Services](#-services)
* [Main Features](#-main-features)
* [Technology Stack](#-technology-stack)
* [Microservices Architecture](#-microservices-architecture)
* [Communication Between Services](#-communication-between-services)
* [Event-Driven Architecture](#-event-driven-architecture)
* [Transactional Outbox](#-transactional-outbox)
* [Idempotency](#-idempotency)
* [Resilience](#-resilience)
* [Security](#-security)
* [Database Design](#-database-design)
* [Caching](#-caching)
* [API Gateway](#-api-gateway)
* [Service Discovery](#-service-discovery)
* [Docker](#-docker)
* [Kubernetes](#-kubernetes)
* [CI/CD](#-cicd)
* [Project Structure](#-project-structure)
* [Prerequisites](#-prerequisites)
* [Environment Variables](#-environment-variables)
* [Running Locally](#-running-locally)
* [Useful URLs](#-useful-urls)
* [Testing](#-testing)
* [Failure Scenarios](#-failure-scenarios)
* [API Documentation](#-api-documentation)
* [Development Workflow](#-development-workflow)
* [Future Improvements](#-future-improvements)
* [What I Learned](#-what-i-learned)
* [Author](#-author)

---

# 🚀 Overview

This project is a backend system for a simplified banking platform.

Instead of building the entire application as a single monolithic Spring Boot application, the system is divided into multiple independently deployable services.

The architecture currently contains:

* User & Account Management
* Transaction Management
* Notification Management
* API Gateway
* Service Discovery
* MySQL databases
* Redis
* Apache Kafka
* Kafka UI
* Docker Compose
* Kubernetes
* GitHub Actions CI/CD

Each business service owns its own database to reduce coupling and follow the **Database-per-Service** principle.

---

# 🏗 Architecture

## High-Level Architecture

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │ Postman / Frontend  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    API Gateway      │
                         │   Spring Gateway    │
                         │      :8080          │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
          ┌────────────────┐ ┌───────────────┐ ┌──────────────────┐
          │ User Account   │ │ Transaction   │ │ Notification     │
          │    Service     │ │    Service    │ │     Service      │
          │     :8081      │ │     :8082     │ │      :8083       │
          └───────┬────────┘ └───────┬───────┘ └─────────┬────────┘
                  │                  │                   │
                  ▼                  ▼                   ▼
          ┌──────────────┐   ┌──────────────┐   ┌───────────────┐
          │ MySQL        │   │ MySQL        │   │ MySQL         │
          │ User Account │   │ Transaction  │   │ Notification  │
          └──────────────┘   └──────────────┘   └───────────────┘

                         ┌─────────────────────┐
                         │   Service Discovery │
                         │       Eureka       │
                         │       :8761        │
                         └─────────────────────┘

                         ┌─────────────────────┐
                         │        Redis        │
                         │       :6379         │
                         └─────────────────────┘

                         ┌─────────────────────┐
                         │   Apache Kafka      │
                         │       :9092        │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      Kafka UI       │
                         │       :8090         │
                         └─────────────────────┘
```

---

# 🧩 Services

## 1. API Gateway

**Port:** `8080`

Responsibilities:

* Single entry point for clients
* Route requests to backend services
* JWT validation
* Authentication-related filtering
* Service-to-service routing
* Integration with Eureka
* Redis integration

Example:

```text
Client
   │
   ▼
Gateway :8080
   │
   ├── /api/users/**        → User Account Service
   │
   ├── /api/transactions/** → Transaction Service
   │
   └── /api/notifications/** → Notification Service
```

---

## 2. User Account Service

**Port:** `8081`

Responsibilities:

* User registration
* User authentication
* User management
* Account management
* Role management
* Balance-related business operations
* JWT authentication
* Kafka event publishing/consuming
* Database persistence

Technologies:

* Spring Boot
* Spring Data JPA
* Spring Security
* JWT
* MySQL
* Apache Kafka
* Eureka Client
* Validation
* Actuator
* OpenAPI / Swagger

---

## 3. Transaction Service

**Port:** `8082`

Responsibilities:

* Create banking transactions
* Validate transaction requests
* Coordinate transaction-related operations
* Communicate with User Account Service
* Publish transaction events
* Consume relevant Kafka events
* Handle distributed-service failures
* Maintain transaction persistence

Additional technologies:

* OpenFeign
* Resilience4j
* Apache Kafka
* Transactional Outbox
* Idempotency

---

## 4. Notification Service

**Port:** `8083`

Responsibilities:

* Consume notification-related Kafka events
* Store notification information
* Send email notifications
* Process asynchronous events
* Prevent duplicate event processing

Technologies:

* Spring Boot
* Spring Data JPA
* MySQL
* Apache Kafka
* Eureka Client
* Java Mail
* Actuator

---

## 5. Discovery Service

**Port:** `8761`

The Discovery Service uses **Netflix Eureka Server**.

Responsibilities:

* Service registration
* Service discovery
* Dynamic service lookup
* Remove the need for hard-coded service locations

Example:

```text
Gateway
   │
   ▼
Eureka
   │
   ├── user-account-service
   ├── transaction-service
   └── notification-service
```

---

# ⭐ Main Features

## Authentication & Authorization

* User registration
* User login
* JWT authentication
* Password hashing
* Role-based authorization
* Spring Security
* Gateway-level JWT validation

---

## Banking Operations

The system provides a simplified banking domain including:

* User management
* Account management
* Transaction processing
* Balance-related operations
* Transaction history
* Notification processing

---

## Event-Driven Architecture

Apache Kafka is used for asynchronous communication between services.

Example:

```text
Transaction Service
        │
        │ Transaction Event
        ▼
      Kafka
        │
        ├───────────────┐
        ▼               ▼
User Account       Notification
   Service             Service
```

This reduces direct coupling between services and allows asynchronous processing.

---

# 🔄 Communication Between Services

The project uses two major communication styles.

## 1. Synchronous Communication

Used when the caller needs an immediate response.

```text
Transaction Service
        │
        │ HTTP / OpenFeign
        ▼
User Account Service
```

OpenFeign is used to simplify HTTP communication between Spring Boot services.

---

## 2. Asynchronous Communication

Used for event-driven operations.

```text
Producer
   │
   ▼
Kafka Topic
   │
   ├───────────────► Consumer A
   │
   └───────────────► Consumer B
```

Benefits:

* Loose coupling
* Asynchronous processing
* Better scalability
* Independent consumers
* Better fault isolation

---

# 📬 Apache Kafka

Kafka is used as the event backbone of the system.

Kafka responsibilities:

* Event publishing
* Event consumption
* Service decoupling
* Asynchronous processing
* Event-driven communication
* Consumer groups
* Event retry handling

Kafka is configured with separate internal and host listeners in Docker Compose.

```text
Docker network:

kafka:29092

Host machine:

localhost:9092
```

Kafka UI is available for inspecting:

* Topics
* Messages
* Consumer groups
* Brokers
* Partitions

---

# 📦 Transactional Outbox

The project applies the **Transactional Outbox Pattern** to reduce the risk of losing events when database operations and Kafka publishing happen independently.

## Problem

Without Outbox:

```text
1. Save transaction to MySQL
2. Publish Kafka event
3. Kafka fails
```

The transaction is persisted but the event may never be published.

---

## With Transactional Outbox

```text
                Database Transaction
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
    Business Data              Outbox Event
          │                         │
          └────────────┬────────────┘
                       │
                  COMMIT
                       │
                       ▼
                Outbox Publisher
                       │
                       ▼
                     Kafka
```

This provides stronger consistency between business data and event publication.

---

# 🔁 Idempotency

Distributed systems can deliver the same event more than once.

For example:

```text
Kafka
  │
  ├── Event #123
  │
  ├── Event #123   ← duplicate
  │
  └── Event #123   ← duplicate
```

The consumer should not process the same business event multiple times.

The project therefore applies event-processing safeguards to prevent duplicate processing.

General flow:

```text
Receive Event
     │
     ▼
Check Event ID
     │
 ┌───┴────┐
 │        │
 ▼        ▼
Exists   New
 │        │
 ▼        ▼
Skip    Process
          │
          ▼
     Mark Processed
```

This is particularly important for banking-related operations where duplicate processing can lead to incorrect balances or duplicate side effects.

---

# 🛡 Resilience

The Transaction Service uses **Resilience4j** for handling failures in synchronous service communication.

Example:

```text
Transaction Service
        │
        ▼
User Account Service
        │
        X
     Failure
        │
        ▼
 Circuit Breaker
        │
        ▼
 Fallback / Error Handling
```

Benefits:

* Prevent cascading failures
* Fail fast
* Improve service availability
* Protect downstream services

---

# 🔐 Security

Security is implemented using **Spring Security + JWT**.

Authentication flow:

```text
             Login
               │
               ▼
       User Account Service
               │
               ▼
        Validate Credentials
               │
               ▼
           Generate JWT
               │
               ▼
             Client
               │
               │ Authorization: Bearer <token>
               ▼
         API Gateway
               │
          Validate JWT
               │
               ▼
       Backend Service
```

Security features include:

* JWT authentication
* BCrypt password hashing
* Role-based authorization
* Request authentication
* Gateway security filtering
* Protected endpoints
* Validation of JWT claims

---

# 🗄 Database Design

The project follows the **Database-per-Service** principle.

```text
User Account Service
        │
        ▼
users-account-db


Transaction Service
        │
        ▼
transaction-db


Notification Service
        │
        ▼
notification-db
```

This avoids having every service directly access a shared database.

### Benefits

* Loose coupling
* Independent schema evolution
* Independent deployment
* Service ownership of data
* Better microservice boundaries

---

# ⚡ Redis

Redis is used as an in-memory data store.

Main purposes include:

* Caching
* Reducing unnecessary database access
* Improving response time
* Supporting distributed application infrastructure

Architecture:

```text
Client
  │
  ▼
Gateway
  │
  ├──── Cache HIT ────► Redis
  │
  └──── Cache MISS ───► Backend Service
                            │
                            ▼
                          MySQL
```

---

# 🌐 API Gateway

The Gateway acts as the single entry point to the backend.

Instead of exposing every service directly:

```text
Client
  │
  ├── :8081
  ├── :8082
  └── :8083
```

The client communicates through:

```text
Client
   │
   ▼
Gateway :8080
   │
   ├── User Account Service
   ├── Transaction Service
   └── Notification Service
```

Benefits:

* Centralized authentication
* Centralized routing
* Hide internal services
* Simplified client communication
* Easier future implementation of rate limiting and observability

---

# 🔎 Service Discovery

Netflix Eureka is used for service discovery.

Instead of configuring:

```text
http://localhost:8081
http://localhost:8082
http://localhost:8083
```

services can discover each other through Eureka.

```text
                Eureka Server
                    │
        ┌───────────┼───────────┐
        ▼           ▼           ▼
      User     Transaction  Notification
    Service       Service       Service
```

This makes the architecture more suitable for dynamic deployments.

---

# 🐳 Docker

Every service can be containerized.

The project provides a Docker Compose environment containing:

### Infrastructure

* MySQL User Account
* MySQL Transaction
* MySQL Notification
* Redis
* Apache Kafka
* Kafka UI

### Application Services

* Discovery Service
* User Account Service
* Transaction Service
* Notification Service
* Gateway Service

The Docker Compose configuration also includes:

* Health checks
* Service dependencies
* Persistent volumes
* Internal Docker networking
* Environment-based secrets

---

# 🐳 Docker Compose Architecture

```text
                         bankapp-net
                              │
      ┌───────────────────────┼────────────────────────┐
      │                       │                        │
      ▼                       ▼                        ▼
  MySQL DBs                 Redis                    Kafka
      │                       │                        │
      │                       │                        ▼
      │                       │                    Kafka UI
      │                       │
      └──────────────┬────────┘
                     │
                     ▼
              Discovery Service
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
      User       Transaction   Notification
     Service       Service       Service
        │            │            │
        └────────────┼────────────┘
                     │
                     ▼
                  Gateway
```

---

# ☸️ Kubernetes

The project also contains Kubernetes manifests for container orchestration.

Kubernetes is used to demonstrate:

* Deployments
* Pods
* Services
* Configurations
* Container orchestration
* Service exposure
* Scaling concepts
* Health management

Example:

```text
                    Kubernetes Cluster
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
   Gateway Pod        User Account Pod   Transaction Pod
        │                  │                  │
        ▼                  ▼                  ▼
   Gateway SVC        User Account SVC   Transaction SVC
```

Kubernetes allows the services to be deployed independently from the local Docker Compose environment.

---

# 🔄 Docker Compose vs Kubernetes

## Docker Compose

Recommended for:

* Local development
* Quick demonstrations
* Integration testing
* Learning microservices

```bash
docker compose -f docker-compose/docker-compose.yml up -d
```

---

## Kubernetes

Recommended for:

* Container orchestration
* Deployment practice
* Scaling
* Service management
* Production-like environments

```bash
kubectl apply -f kubernetes/
```

---

# 🤖 CI/CD

GitHub Actions is used to automate the development pipeline.

Current workflow structure includes:

```text
.github/
└── workflows/
    ├── ci.yml
    ├── docker-build.yml
    └── publish-images.yml
```

## CI

The CI pipeline validates the project after code changes.

Typical flow:

```text
Developer
    │
    ▼
Git Push / Pull Request
    │
    ▼
GitHub Actions
    │
    ├── Checkout
    ├── Setup Java
    ├── Build
    └── Test
```

---

## Docker Build

Docker images are automatically built through GitHub Actions.

```text
Git Push
   │
   ▼
GitHub Actions
   │
   ▼
Build Docker Images
   │
   ▼
Validate Images
```

---

## Publish Docker Images

Images can be published to a container registry.

```text
GitHub Actions
       │
       ▼
Docker Build
       │
       ▼
Docker Registry
       │
       ▼
Deployment
```

This allows Kubernetes or another environment to pull versioned application images.

---

# 📁 Project Structure

```text
banking-microservice/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       ├── docker-build.yml
│       └── publish-images.yml
│
├── discovery-service/
│
├── gateway-service/
│
├── user-account-service/
│
├── transaction-service/
│
├── notification-service/
│
├── docker-compose/
│   └── docker-compose.yml
│
├── kubernetes/
│
├── .gitignore
│
└── README.md
```

---

# 🛠 Technology Stack

| Category                | Technology                  |
| ----------------------- | --------------------------- |
| Language                | Java 21                     |
| Framework               | Spring Boot                 |
| Web                     | Spring MVC                  |
| Security                | Spring Security             |
| Authentication          | JWT                         |
| ORM                     | Spring Data JPA / Hibernate |
| Database                | MySQL 8                     |
| Cache                   | Redis 7                     |
| Messaging               | Apache Kafka                |
| Service Discovery       | Netflix Eureka              |
| API Gateway             | Spring Cloud Gateway        |
| HTTP Client             | OpenFeign                   |
| Resilience              | Resilience4j                |
| API Documentation       | SpringDoc OpenAPI / Swagger |
| Monitoring              | Spring Boot Actuator        |
| Containerization        | Docker                      |
| Local Orchestration     | Docker Compose              |
| Container Orchestration | Kubernetes                  |
| CI/CD                   | GitHub Actions              |
| Build Tool              | Maven                       |

---

# 📋 Prerequisites

Before running the project, install:

* Java 21+
* Maven
* Docker Desktop
* Docker Compose
* Git
* Kubernetes / Docker Desktop Kubernetes or Kind
* kubectl
* Postman or another REST API client

Optional:

* IntelliJ IDEA
* Kafka UI
* MySQL Workbench
* Redis CLI

---

# 🔐 Environment Variables

The project uses environment variables instead of hard-coding sensitive credentials.

Example `.env`:

```env
MYSQL_ROOT_PASSWORD=your_password
JWT_SECRET=your_jwt_secret

MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
```

> ⚠️ Never commit real passwords, JWT secrets, email credentials or other secrets to GitHub.

Add `.env` to `.gitignore`.

---

# ▶️ Running Locally with Docker Compose

## 1. Clone the repository

```bash
git clone https://github.com/HungHayHo-IT/banking-microservice.git

cd banking-microservice
```

---

## 2. Create `.env`

Create:

```text
.env
```

Example:

```env
MYSQL_ROOT_PASSWORD=your_password
JWT_SECRET=your_jwt_secret

MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
```

---

## 3. Start the system

```bash
docker compose -f docker-compose/docker-compose.yml up -d
```

---

## 4. Check containers

```bash
docker compose -f docker-compose/docker-compose.yml ps
```

All required services should eventually become healthy/running.

---

## 5. View logs

Example:

```bash
docker compose -f docker-compose/docker-compose.yml logs -f transaction-service
```

Or:

```bash
docker compose -f docker-compose/docker-compose.yml logs -f user-account-service
```

---

## 6. Stop the system

```bash
docker compose -f docker-compose/docker-compose.yml down
```

To also remove persistent volumes:

```bash
docker compose -f docker-compose/docker-compose.yml down -v
```

> ⚠️ Removing volumes deletes local database data.

---

# 🌐 Useful URLs

| Component            | URL                     |
| -------------------- | ----------------------- |
| API Gateway          | `http://localhost:8080` |
| User Account Service | `http://localhost:8081` |
| Transaction Service  | `http://localhost:8082` |
| Notification Service | `http://localhost:8083` |
| Eureka Dashboard     | `http://localhost:8761` |
| Kafka UI             | `http://localhost:8090` |
| Redis                | `localhost:6379`        |
| Kafka                | `localhost:9092`        |
| MySQL User Account   | `localhost:3306`        |
| MySQL Transaction    | `localhost:3307`        |
| MySQL Notification   | `localhost:3308`        |

---

# 📖 API Documentation

Each Spring Boot service uses SpringDoc OpenAPI.

Swagger UI can be accessed through the corresponding service.

Example:

```text
http://localhost:8081/swagger-ui/index.html
http://localhost:8082/swagger-ui/index.html
http://localhost:8083/swagger-ui/index.html
```

The recommended production-style entry point is the API Gateway.

---

# 🧪 Testing

The project contains testing support for major Spring components.

Testing areas include:

* Controller layer
* Service layer
* Repository layer
* Security
* Kafka
* Validation
* Actuator
* Integration scenarios

Basic Maven test command:

```bash
mvn test
```

For an individual service:

```bash
cd user-account-service
mvn test
```

---

# 🧪 Distributed Failure Testing

One important goal of this project is not only testing the happy path, but also understanding how distributed systems behave when dependencies fail.

Examples:

## Kafka Down

```text
Transaction Service
       │
       ▼
      Kafka
       X
    DOWN
```

Expected behavior should be verified according to the event-processing strategy.

---

## Database Down

```text
Transaction Service
       │
       ▼
     MySQL
       X
    DOWN
```

The service should fail gracefully instead of silently corrupting business data.

---

## Consumer Restart

```text
Kafka
 │
 ▼
Consumer
 │
 X
Restart
 │
 ▼
Continue Processing
```

This is useful for validating:

* Consumer groups
* Offset management
* Idempotency
* Event recovery

---

## Duplicate Event

Send/process the same event more than once and verify that the business operation is not executed multiple times.

---

# 🔄 Example Transaction Flow

A simplified transaction flow can be represented as:

```text
Client
  │
  │ POST /transactions
  ▼
API Gateway
  │
  ▼
Transaction Service
  │
  ├── Validate JWT
  │
  ├── Validate request
  │
  ├── Validate account information
  │
  ├── Execute transaction logic
  │
  ├── Persist transaction
  │
  ├── Persist Outbox Event
  │
  ▼
Database Commit
  │
  ▼
Outbox Publisher
  │
  ▼
Kafka
  │
  ├───────────────► User Account Service
  │
  └───────────────► Notification Service
```

This architecture separates synchronous business operations from asynchronous side effects.

---

# 🧠 Distributed Systems Patterns Demonstrated

This project is intentionally designed around several common microservice patterns.

| Pattern                   | Purpose                         |
| ------------------------- | ------------------------------- |
| API Gateway               | Single entry point              |
| Service Discovery         | Dynamic service lookup          |
| Database-per-Service      | Data ownership                  |
| Event-Driven Architecture | Loose coupling                  |
| Transactional Outbox      | Reliable event publication      |
| Idempotency               | Safe duplicate processing       |
| Circuit Breaker           | Failure isolation               |
| Synchronous HTTP          | Immediate service communication |
| Asynchronous Kafka        | Event-driven communication      |
| Health Checks             | Service health management       |
| Containerization          | Consistent environments         |
| Kubernetes                | Container orchestration         |
| CI/CD                     | Automated build and delivery    |

---

# 📈 Scalability Considerations

The architecture allows individual services to be scaled independently.

For example:

```text
                Load
                 │
        ┌────────┼────────┐
        ▼        ▼        ▼
   Transaction Transaction Transaction
      Pod          Pod       Pod
```

This is one of the main benefits of the microservices architecture.

Instead of scaling the entire application, only the bottleneck service needs additional instances.

---

# 🔒 Security Considerations

The project follows several security practices:

* Passwords are hashed using BCrypt
* JWT is used for stateless authentication
* Sensitive configuration is supplied through environment variables
* Secrets should not be committed to source control
* Services can be protected behind the API Gateway
* Role-based access control is used for authorization

For production deployment, additional mechanisms would be required such as:

* HTTPS/TLS
* Secret Manager / Kubernetes Secrets
* Key rotation
* Rate limiting
* Audit logging
* Centralized security monitoring

---

# 📊 Observability

Spring Boot Actuator is included to expose application health information.

Example:

```text
/actuator/health
```

Health checks are also used by Docker Compose to control service startup dependencies.

This helps prevent a service from being considered ready before its dependencies are available.

---

# 🔧 Development Workflow

Recommended workflow:

```text
1. Create feature branch
          │
          ▼
2. Implement feature
          │
          ▼
3. Write/update tests
          │
          ▼
4. Run Maven tests
          │
          ▼
5. Run Docker Compose
          │
          ▼
6. Test API with Postman
          │
          ▼
7. Push to GitHub
          │
          ▼
8. GitHub Actions CI
          │
          ▼
9. Build Docker image
          │
          ▼
10. Publish image
          │
          ▼
11. Deploy to Kubernetes
```

---

# 🧪 Example Verification Checklist

Before considering a release, verify:

```text
[ ] All services start successfully
[ ] Eureka registers all services
[ ] Gateway can route requests
[ ] JWT authentication works
[ ] Role authorization works
[ ] User registration works
[ ] Login works
[ ] Transaction creation works
[ ] Kafka events are published
[ ] Kafka consumers process events
[ ] Duplicate events are handled safely
[ ] Outbox events are persisted
[ ] Redis is reachable
[ ] Database-per-service is respected
[ ] Circuit breaker behavior is tested
[ ] Docker Compose starts successfully
[ ] Kubernetes manifests deploy successfully
[ ] GitHub Actions CI passes
[ ] Docker images build successfully
```

---

# 🚧 Future Improvements

The project is intentionally kept at a practical Fresher–Junior scope.

Possible future improvements include:

### Observability

* Centralized logging
* Distributed tracing
* Prometheus
* Grafana
* OpenTelemetry

### Security

* OAuth2 / OpenID Connect
* Keycloak
* Refresh token rotation
* API rate limiting
* Secret management

### Kafka

* Dead Letter Topic
* Retry topics
* Schema Registry
* Avro / Protobuf
* Kafka monitoring
* More advanced partitioning strategies

### Kubernetes

* Helm
* ConfigMap
* Kubernetes Secrets
* Horizontal Pod Autoscaler
* Ingress
* Persistent Volumes
* Readiness and liveness probes
* Resource requests and limits

### CI/CD

* Automated deployment
* Environment separation
* Staging environment
* Production environment
* Automated integration tests
* Container vulnerability scanning

### Observability

* Prometheus
* Grafana
* OpenTelemetry
* Jaeger
* Centralized logs

### Distributed Transactions

Further improvement could include a more complete **Saga Pattern** implementation for long-running distributed business transactions, together with explicit compensation workflows.

---

# 🎯 Project Level

This project is designed to demonstrate practical knowledge of:

```text
Java
  │
  ▼
Spring Boot
  │
  ├── Spring MVC
  ├── Spring Data JPA
  ├── Spring Security
  └── Validation
  │
  ▼
Spring Cloud
  │
  ├── Eureka
  ├── Gateway
  ├── OpenFeign
  └── Resilience4j
  │
  ▼
Distributed Systems
  │
  ├── Kafka
  ├── Event-driven architecture
  ├── Transactional Outbox
  ├── Idempotency
  └── Fault tolerance
  │
  ▼
Infrastructure
  │
  ├── Docker
  ├── Docker Compose
  └── Kubernetes
  │
  ▼
DevOps
  │
  └── GitHub Actions
```

---

# 💡 Why This Project?

A simple CRUD application demonstrates how to create REST APIs.

This project goes further by demonstrating how multiple independent services communicate and behave in a distributed environment.

The main learning objectives were:

* Designing microservice boundaries
* Separating databases by service
* Implementing synchronous and asynchronous communication
* Understanding Kafka producers and consumers
* Handling duplicate events
* Improving reliability with the Transactional Outbox pattern
* Handling service failures with Circuit Breaker
* Securing APIs with JWT
* Containerizing applications with Docker
* Running services using Docker Compose
* Deploying services with Kubernetes
* Automating builds using GitHub Actions

---

# 📚 Key Concepts Practiced

### Backend

* Java
* OOP
* Exception Handling
* Collections
* Streams
* REST API
* Validation
* Dependency Injection
* DTO
* Entity
* Repository / Service / Controller architecture

### Spring

* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* Spring Cloud
* Spring Gateway
* Eureka
* OpenFeign
* Resilience4j

### Database

* MySQL
* JPA / Hibernate
* Transactions
* Database-per-Service
* Entity relationships
* Persistence

### Distributed Systems

* Microservices
* Event-driven architecture
* Kafka
* Consumer Groups
* Idempotency
* Transactional Outbox
* Circuit Breaker
* Synchronous communication
* Asynchronous communication

### DevOps

* Docker
* Docker Compose
* Kubernetes
* GitHub Actions
* Container images
* Health checks
* Environment variables

---

# 👨‍💻 Author

**Hung Hay Ho**

Java Backend Developer — Fresher / Junior

GitHub:

https://github.com/HungHayHo-IT

Repository:

https://github.com/HungHayHo-IT/banking-microservice

---

# ⭐ If You Find This Project Useful

If this project helps you learn about Java Backend, Spring Boot or Microservices, feel free to ⭐ star the repository.

---

## 📌 Project Status

**Status:** Active Learning / Development

The project is continuously improved to explore practical backend engineering and distributed-system concepts while keeping the implementation appropriate for a Java Backend Fresher–Junior portfolio.
