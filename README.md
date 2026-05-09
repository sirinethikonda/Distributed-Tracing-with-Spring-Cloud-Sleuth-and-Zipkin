# Distributed Tracing with Spring Boot and Zipkin

This project demonstrates distributed tracing in a microservices architecture using Spring Boot 3, Micrometer Tracing (Brave bridge), and Zipkin. It consists of two services: `order-service` and `inventory-service`.

## Features
- **Distributed Tracing**: Request flows are traced across service boundaries.
- **Custom Span Tags**: Business-relevant data (OrderId, ProductId) is attached to spans.
- **Observability**: Real-time visualization of latency and service dependencies via Zipkin.
- **Containerization**: Full setup using Docker Compose with health checks.

## Architecture
1. **Order Service**: Exposes `POST /api/orders`. It calls the Inventory Service to verify stock.
2. **Inventory Service**: Exposes `GET /api/inventory/check/{productId}`. Uses in-memory storage.
3. **Zipkin**: Collects and visualizes trace data.

## Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Maven 3.x

## Setup and Running

1. **Clone the repository** (if applicable).
2. **Build and Start the containers**:
   ```bash
   docker-compose up --build
   ```
3. **Wait for services to be healthy**: All services have health checks and will start in order.

## API Usage

### 1. Create an Order
**Endpoint**: `POST http://localhost:8081/api/orders`  
**Body**:
```json
{
  "orderId": "ord-001",
  "productId": "prod-123",
  "quantity": 5
}
```

### 2. Check Inventory (Directly)
**Endpoint**: `GET http://localhost:8082/api/inventory/check/prod-123?quantity=5`

## Verifying Traces

1. Open the Zipkin UI at [http://localhost:9411](http://localhost:9411).
2. Click "Run Query".
3. You should see traces for the requests sent to `order-service`.
4. Click on a trace to see the span details.
5. Verify the following custom tags:
   - `order-service` span: `order.id`
   - `inventory-service` span: `inventory.productId`

## Project Structure
- `/order-service`: Order processing logic and RestTemplate instrumentation.
- `/inventory-service`: In-memory inventory management.
- `docker-compose.yml`: Infrastructure orchestration.
- `.env.example`: Environment variable template.
