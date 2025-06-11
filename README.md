# Order Management Platform

## Overview
The Order Management Platform is a Spring Boot application designed to manage orders, products, and payments. It uses an in-memory H2 database for testing purposes and provides RESTful APIs for order management.

## Prerequisites
Before running the application, ensure you have the following installed:
- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose

## GitHub Repository

[Order Management Platform Repository](https://github.com/ahmedelsayed123/order-platform.git)

## How to Run the Application

### Option 1: Run with Docker
```bash
mvn clean package
```
```bash
docker-compose up --build
```
##
H2 Database Console: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb 

Username: sa

Password:

## 
Swagger UI: http://localhost:8080/swagger-ui.html

### How to test the flow

1- we will call the `api/categories` endpoint to create a category:

request:
```bash
curl -X POST http://localhost:8080/api/categories -H "Content-Type: application/json" -d '{"name": "Electronics"}'
```
response:
```json
{
  "id": 1,
  "name": "Electronics"
}
```
2- we will call the `api/products` endpoint to create a product:

request:
```bash
curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name": "Smartphone", "price": 699.99,"stock": 10, "categoryId": 1}'
```
response:
```json
{
  "id": 1,
  "name": "Smartphone",
  "stock": 10,
  "price": 699.99,
  "categoryId": 1
}
```
3- we will call the `api/orders` endpoint to create an order:

request:
```bash
curl -X 'POST' \
  'http://localhost:8080/api/orders?seatLetter=A&seatNumber=13' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d ''
```
response:
```json
{
  "id": 2,
  "buyerInfo": {
    "buyerEmail": null,
    "seatLetter": "A",
    "seatNumber": 13
  },
  "totalPrice": 0,
  "status": "OPEN",
  "paymentInfo": null,
  "productQuantities": []
}
```
4- we will call the `api/orders/{id}` endpoint to update the order:

request:
```bash
curl -X 'PUT' \
  'http://localhost:8080/api/orders/2?email=ahmedlesnar1%40gmail.com' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '[
  {
    "productId": 1,
    "quantity": 3
  }
]'
```

response:
```json
{
  "id": 2,
  "buyerInfo": {
    "buyerEmail": "ahmedlesnar1@gmail.com",
    "seatLetter": "A",
    "seatNumber": 13
  },
  "totalPrice": 9,
  "status": "OPEN",
  "paymentInfo": null,
  "productQuantities": [
    {
      "id": 2,
      "productId": 1,
      "quantity": 3
    }
  ]
}
```

5-we will call the `api/orders/{id}/finish` endpoint to pay the order:

request:
```bash
curl -X 'POST' \
  'http://localhost:8080/api/orders/2/finish?token=1255&gateway=ONLINE' \
  -H 'accept: */*' \
  -d ''
```

response:

```json
{
  "id": 2,
  "buyerInfo": {
    "buyerEmail": "ahmedlesnar1@gmail.com",
    "seatLetter": "A",
    "seatNumber": 13
  },
  "totalPrice": 9,
  "status": "FINISHED",
  "paymentInfo": {
    "cardToken": "1255",
    "paymentGateway": "ONLINE",
    "paymentDate": "2025-06-11T13:35:54.848175554",
    "paymentStatus": "PAYMENT_FAILED"
  },
  "productQuantities": [
    {
      "id": 2,
      "productId": 1,
      "quantity": 3
    }
  ]
}
```
### Database tables design of the prev example


![db-schema.png](..%2Fdb-schema.png)

### Additional business logic handled in code:
- **Order cancellation**: When an order is canceled, the service restores the stock of the associated products to ensure inventory accuracy. The canceled order remains in the database for auditing purposes, preserving its details such as buyer information, product quantities, and status. This approach ensures traceability and compliance with business requirements.
- **Payment Failure Handling**: Currently, the application uses a mocked payment service to simulate payment processing. If the payment fails, the order remains in the database with its status unchanged, allowing for future handling or retry mechanisms. This ensures that the order details, including buyer information and product quantities, are preserved for auditing and potential reprocessing. Future enhancements could include automatic stock restoration or notification mechanisms for failed payments.

### Points taken care of in code:
- **Race Condition and Concurrency Handling**: The application ensures safe concurrent operations during order and product updates by leveraging optimistic locking, retry mechanisms, and transactional annotations. Optimistic locking is applied to prevent data conflicts when multiple users attempt to modify the same order or product simultaneously. Retry mechanisms handle transient failures caused by concurrent modifications, ensuring the operations are retried until successful. The use of @Transactional guarantees atomicity and consistency in database operations, such as updating product stock, calculating order totals, and processing payments, safeguarding the integrity of both order and product data.


