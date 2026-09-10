# Microservices Assignment

## What This Project Does
This project has two small services that work together:
- **Order Service** (port 8080) — you can create orders, view them, and update their status
- **Notification Service** (port 8082) — whenever an order is created, it automatically saves a notification

---

## How to Run

### With Docker (Recommended approach)
Make sure Docker Desktop is running first. Then from the root folder:

```bash
cd order-service
./mvnw clean package -DskipTests
cd ../notification-service
./mvnw clean package -DskipTests
cd ..
docker-compose up --build

``` 
## Without Docker (run locally)
Once started: Notification-service is started at first, followed which Order-service

Order Service → http://localhost:8080

Notification Service → http://localhost:8082

## API Endpoints
```bash
Order Service
Method	      Endpoint	                Description
POST	    /api/orders	            Create a new order
GET	    /api/orders	            Get all orders
GET	    /api/orders/{id}	    Get one order by ID
PATCH	    /api/orders/{id}/status?status=	Update order status

Notification Service
Method	  Endpoint	     Description
GET	/api/notifications	Get all notifications
``` 
## How services communicate with each other ?
I went with synchronous REST — meaning when an order is created, the Order Service directly calls the Notification Service over HTTP.

I chose this over a message broker like RabbitMQ or Kafka because:

1)This is a small project and a message broker would be overkill

2)It's much simpler to set up and understand and  can immediately see if something goes wrong.

3)The downside is that if the Notification Service is down, the order creation will also fail. For a bigger production system, I would use a message broker to avoid this.

## Database
Both services use H2 — a lightweight in-memory database. I chose this because:

No setup needed and relevant for these kind of small projects

The tradeoff is that all data is lost when the service restarts. In a real system, I would use PostgreSQL or MySQL.

# Running Tests
```bash
cd order-service
./mvnw test
cd ../notification-service
./mvnw test
```
## Assumptions 
1)Every new order triggers exactly one notification

2)Order status can jump to any status directly (e.g. PENDING → SHIPPED without going through CONFIRMED)

3)In-memory database is fine for this scope






