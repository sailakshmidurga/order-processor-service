# 📦 Order Processor Service

A Spring Boot microservice that consumes order events from RabbitMQ, processes them transactionally, and persists the results into a MySQL database.

---

## 🚀 Overview

This service listens for `OrderPlacedEvent` messages published to RabbitMQ.
When a message is received:

1. The event is deserialized from JSON.
2. The order is processed.
3. The order status is set to `PROCESSED`.
4. The order is saved to MySQL.
5. The message is manually acknowledged.
6. If an error occurs, the message is routed to a Dead Letter Queue (DLQ).

---

## 🏗 Architecture

```
Publisher → RabbitMQ (Exchange) → Queue → Order Processor Service → MySQL
                                          ↓
                                      Dead Letter Queue
```

---

## 🧰 Technologies Used

* Java 17
* Spring Boot 4.x
* Spring Data JPA
* Spring AMQP (RabbitMQ)
* MySQL 8
* Docker & Docker Compose
* Lombok

---

## 📂 Project Structure

```
src/main/java/com/example/orderprocessor
│
├── config
│   └── RabbitMQConfig.java
│
├── model
│   ├── Order.java
│   ├── OrderStatus.java
│   └── OrderPlacedEvent.java
│
├── repository
│   └── OrderRepository.java
│
├── service
│   ├── OrderProcessingService.java
│   └── OrderEventListener.java
│
└── OrderProcessorApplication.java
```

---

## 📨 RabbitMQ Configuration

### Exchanges

* `order.events` (Main Topic Exchange)
* `dlx.order.events` (Dead Letter Exchange)

### Queues

* `order.placed.queue`
* `order.dlq` (Dead Letter Queue)

### Routing Key

* `order.placed`

---

## 🔄 Message Flow

1. Message published to:

   ```
   Exchange: order.events
   Routing Key: order.placed
   ```

2. Message routed to:

   ```
   order.placed.queue
   ```

3. Listener:

   * Converts JSON → `OrderPlacedEvent`
   * Calls `OrderProcessingService`
   * Saves order to database
   * Manually ACKs message

4. If:

   * Permanent error → Sent to DLQ
   * Transient error → Requeued

---

## 🗄 Database Schema

Table: `orders`

| Column      | Type    |
| ----------- | ------- |
| id          | VARCHAR |
| product_id  | VARCHAR |
| customer_id | VARCHAR |
| quantity    | INT     |
| status      | VARCHAR |

Example record:

```
| ORD-2004 | CUST-4 | PROD-4 | 2 | PROCESSED |
```

---

## 🐳 Running with Docker

### 1️⃣ Start the system

```bash
docker-compose up --build
```

### 2️⃣ Services Started

* Order Processor → [http://localhost:8080](http://localhost:8080)
* RabbitMQ UI → [http://localhost:15672](http://localhost:15672)
  Username: guest
  Password: guest
* MySQL running internally

---

## 🧪 Testing the System

### Publish Test Message

From RabbitMQ UI:

Exchange: `order.events`
Routing Key: `order.placed`

Message Body:

```json
{
  "orderId": "ORD-2004",
  "productId": "PROD-4",
  "customerId": "CUST-4",
  "quantity": 2
}
```

---

## ✅ Expected Result

* Order saved in MySQL
* Status = `PROCESSED`
* Message acknowledged
* No message left in queue

Verify:

```bash
docker exec -it order-processor-service-mysql-db-1 mysql -u user -p
```

Then:

```sql
USE orderdb;
SELECT * FROM orders;
```

---

## ⚙ Key Features Implemented

* ✔ Topic Exchange
* ✔ Dead Letter Exchange
* ✔ Dead Letter Queue
* ✔ Manual Message Acknowledgment
* ✔ JSON Message Conversion
* ✔ Transactional Order Processing
* ✔ Database Persistence
* ✔ Dockerized Setup
* ✔ Health Checks
* ✔ End-to-End Message Flow

---

## 🔒 Error Handling Strategy

| Error Type     | Action           |
| -------------- | ---------------- |
| Business Error | Sent to DLQ      |
| System Error   | Message Requeued |
| Success        | Manual ACK       |

---

## 👩‍💻 Author

Sai Lakshmi Durga Koneti
Order Processor Microservice Assignment