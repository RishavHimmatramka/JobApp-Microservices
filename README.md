# JobApp-Microservices

**JobApp-Microservices** is a self-learning project that demonstrates a **microservices architecture** using Spring Boot. It covers inter-service communication, event-driven patterns, service discovery, API gateway routing, containerization, resilience features, and distributed tracing with Zipkin.  

---

## Features

- **CRUD operations** for jobs, companies, and reviews.
- **Inter-service communication** using OpenFeign for synchronous calls and RabbitMQ for asynchronous events.
- **Event-driven updates:**  
  - When a review is created/updated/deleted, the company's average rating and review count are automatically updated.  
  - When a company is deleted, all its reviews are removed via RabbitMQ events.
- **API Gateway** routes requests to appropriate microservices.
- **Resilience patterns:** Circuit breaker, rate limiting, and retry mechanisms.
- **Distributed Tracing:** Zipkin integration for monitoring inter-service calls.
- **Profiles:** `dev`, `prod`, `docker`, `kubernetes`.
- **Containerization & Orchestration:** Docker and Kubernetes.
- **Service Discovery & Config Management:** Spring Cloud Config Server and Service Registry.

---

## Microservices Overview

| Microservice      | Responsibility |
|------------------|----------------|
| **companyms**     | Manages companies and fetches associated jobs and reviews. |
| **jobms**         | Handles CRUD for jobs and fetches parent company. |
| **reviewms**      | Handles CRUD for reviews and triggers events for updates. |
| **api_gateway**   | Routes requests to appropriate services. |
| **service_registry** | Registers and discovers services. |
| **config_server** | Centralized configuration management. |

---

## Tech Stack

- **Backend:** Spring Boot, Spring Cloud  
- **Messaging:** RabbitMQ  
- **Databases:** H2 (dev), PostgreSQL (prod/docker/k8s)  
- **Containerization & Orchestration:** Docker, Kubernetes  
- **Patterns & Tools:** API Gateway, Service Registry, OpenFeign, Circuit Breaker, Rate Limiting, Retry, Zipkin  

---

## Architecture Overview

- **Synchronous Communication:** OpenFeign is used to fetch related data between services.  
- **Asynchronous Communication:** RabbitMQ handles event-driven updates like review changes and company deletions.  
- **Distributed Tracing:** Zipkin tracks inter-service calls.  

**Example Flows:**

1. **Fetch Company:** Retrieves associated jobs and reviews via OpenFeign.  
2. **Fetch Job:** Retrieves its parent company.  
3. **Review Updates:** Triggers RabbitMQ events to update company’s aggregated ratings.  
4. **Company Deletion:** Triggers RabbitMQ event to delete all associated reviews.  

---

## Setup & Running

### Prerequisites

- Java 17+  
- Maven  
- Docker & Docker Compose (for containerized deployment)  
- Kubernetes (for orchestration)  
- PostgreSQL (for prod/docker/kubernetes profiles)  
- RabbitMQ  
- Zipkin (for distributed tracing)  

---

### Running Locally (Dev Profile)

Each microservice must be started **separately** in the correct order:

```bash
# Clone repository
git clone https://github.com/RishavHimmatramka/JobApp-Microservices.git
cd JobApp-Microservices

# Service Registry
cd service_registry
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Config Server
cd ../config_server
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Company Microservice
cd ../companyms
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Job Microservice
cd ../jobms
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Review Microservice
cd ../reviewms
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# API Gateway
cd ../api_gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running with Docker (Docker Profile)
```bash 
# Build and start all services
docker-compose -f docker-compose.yaml up --build
```

### Running on Kubernetes (Kubernetes Profile)

The Kubernetes manifests are located in k8s/. Apply them in the following order:

```bash
# PostgreSQL
kubectl apply -f k8s/postgres

# RabbitMQ
kubectl apply -f k8s/rabbitmq

# Zipkin for distributed tracing
kubectl apply -f k8s/zipkin

# Bootstrap microservices (companyms, jobms, reviewms)
kubectl apply -f k8s/bootstrap/jobms
kubectl apply -f k8s/bootstrap/companyms
kubectl apply -f k8s/bootstrap/reviewms
```
