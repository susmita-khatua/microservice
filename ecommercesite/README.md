# E-Commerce Microservices Platform

Production-grade enterprise E-Commerce application built with **Java 17**, **Spring Boot 3.2**, and **Spring Cloud 2023.0**.

## Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (:8080)                            │
│              Global JWT Authentication Filter                    │
│              Rate Limiting · Route Management                    │
│              H2 In-Memory Database (All Services)                │
└───────────────────────────────────────────────────────────────────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| Discovery Server | 8761 | Netflix Eureka service registry |
| Config Server | 8888 | Centralized configuration |
| API Gateway | 8080 | Entry point with JWT filter |
| Auth Service | 8081 | Registration, login, JWT tokens (H2) |
| Product Service | 8082 | Product CRUD with OpenAPI (H2) |
| Inventory Service | 8083 | Stock management & alerts (H2) |
| Order Service | 8084 | Order orchestration with Feign (H2) |
| Payment Service | 8085 | Payment processing & refunds (H2) |
| Aggregation Service | 8087 | BFF aggregation pattern |

## Tech Stack

- **Runtime**: Java 17, Spring Boot 3.2.5
- **Cloud**: Spring Cloud 2023.0.0 (Eureka, Gateway, Config, OpenFeign)
- **Security**: JWT (jjwt 0.12.5), BCrypt, Spring Security
- **Data**: H2 (In-Memory), Spring Data JPA, Hibernate
- **Resilience**: Resilience4j (Circuit Breaker + Retry)
- **Mapping**: MapStruct 1.5.5
- **Docs**: SpringDoc OpenAPI 3 (Swagger UI)
- **Testing**: JUnit 5, Mockito, JaCoCo

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+

### Local Development
```bash
# 1. Build all modules
mvn clean install -DskipTests

# 2. Start services in order
cd discovery-server && mvn spring-boot:run &
cd config-server && mvn spring-boot:run &
sleep 15
cd auth-service && mvn spring-boot:run &
cd product-service && mvn spring-boot:run &
cd inventory-service && mvn spring-boot:run &
cd order-service && mvn spring-boot:run &
cd payment-service && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
cd aggregation-service && mvn spring-boot:run &
```

### Verify Services
- **Eureka Dashboard**: http://localhost:8761
- **Swagger UI (Products)**: http://localhost:8082/swagger-ui.html
- **Gateway Health**: http://localhost:8080/actuator/health

## API Examples

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"password123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

### Create Product (authenticated)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name":"MacBook Pro","description":"Laptop","price":2499.99,"skuCode":"MBP-001","category":"Electronics"}'
```

### Get Aggregated Product Details
```bash
curl http://localhost:8080/api/product-details/1 \
  -H "Authorization: Bearer <token>"
```

## Testing

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl product-service

# Generate coverage report
mvn test jacoco:report -pl product-service
# View report: product-service/target/site/jacoco/index.html
```

## Project Structure

```
ecommerce-microservices/
├── pom.xml                     # Parent POM
├── common-lib/                 # Shared DTOs, exceptions, utils
├── discovery-server/           # Eureka Server
├── config-server/              # Config Server
├── api-gateway/                # Gateway + JWT Filter
├── auth-service/               # Authentication + JWT
├── product-service/            # Product CRUD (fully implemented)
├── inventory-service/          # Stock Management
├── order-service/              # Order Orchestration
├── payment-service/            # Payment Processing
├── aggregation-service/        # BFF Pattern
├── docker-compose.yml          # Docker Compose
├── Jenkinsfile                 # CI/CD Pipeline
└── README.md
```

## License

This project is licensed under the Apache License 2.0.
