# 📦 Tracking Number Generation Service

This is a Spring Boot-based RESTful API service for generating **idempotent tracking numbers**. It ensures that
duplicate requests with the same payload do not create multiple tracking codes.

---

## 🚀 Features

- Generate unique and deterministic tracking numbers
- Redis cache support to speed up response for repeated requests
- Handles race conditions with retry-safe persistence
- Validation for request payloads (country codes, weight, customer data)
- Includes API response formatting and logging

---

## 🧱 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Redis (as cache)
- PostgreSQL (or any JPA-compatible DB)
- Lombok
- JUnit 5 & Mockito

---

## 🛠️ Getting Started

### Prerequisites

- [Java 17+](https://jdk.java.net/17/)
- [Maven](https://maven.apache.org/)
- [Gradle](https://gradle.org/)
- [PostgreSQL database](https://www.postgresql.org/)
- [Redis](https://redis.io/)

### Clone the repository

```bash
git clone https://github.com/your-username/tracking-number-service.git
cd tracking-number-service
```

### Configuration

Edit the application.properties to match your environment:

```
# PostgreSQL datasource settings
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

# liquibase datasource settings
spring.liquibase.url=
spring.liquibase.user=
spring.liquibase.password=
spring.liquibase.driver-class-name=org.postgresql.Driver
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# Redis settings
spring.data.redis.host=
spring.data.redis.port=6379
# spring.redis.password=yourRedisPassword
```

### Run the application

```bash
./mvnw spring-boot:run
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 📬 API Endpoint

### `GET /next-tracking-number`

#### Query Parameters:

| Param                    | Type           | Required | Description                        |
|--------------------------|----------------|----------|------------------------------------|
| origin\_country\_id      | String         | ✅        | ISO country code (e.g., "ID")      |
| destination\_country\_id | String         | ✅        | ISO country code (e.g., "SG")      |
| weight                   | BigDecimal     | ✅        | Minimum weight as defined          |
| created\_at              | OffsetDateTime | ✅        | Timestamp in ISO format            |
| customer\_id             | UUID           | ✅        | Unique customer identifier         |
| customer\_name           | String         | ✅        | Full name of the customer          |
| customer\_slug           | String         | ✅        | Slugified version of customer name |

#### Curl

```
curl --location 'localhost:8080/next-tracking-number?origin_country_id=MY&destination_country_id=MY&weight=0.005&created_at=2018-11-20T19%3A29%3A32%2B08%3A00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=John%20Doe&customer_slug=john-doe'
```

#### Sample Response

```
{
    "success": true,
    "data": {
        "tracking_number": "MUZWCOBZHEZTSYJV",
        "created_at": "2025-05-29T11:48:42.871206439+08:00"
    },
    "error": null
}
```

---

## 📦 Project Structure

``` arduino
src/
├── api/
│   ├── request/
│   └── response/
├── controller/
├── helper/
├── middleware/
├── model/
├── repository/
├── service/
└── config/
```

