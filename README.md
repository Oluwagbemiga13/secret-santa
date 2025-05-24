# 🎅 Secret Santa App

A Java Spring Boot application to automate your Secret Santa gift exchange – from participant invitation to anonymous gift assignment, all done via email!

---

## ✨ Features

- ✅ Account creation & secure login with JWT
- 👥 Add participants by email
- 📬 Automatically sends email invitations with gift preference form
- 🎁 Once all participants submit their preferences, the app assigns gifters and emails gift details
- 🔐 Secure authentication via Spring Security + JWT
- 📦 REST API documented via OpenAPI (Swagger)
- 📨 Email delivery via Seznam.cz SMTP
- 💡 Clean architecture using Lombok and MapStruct

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3.4.4
- PostgreSQL
- Spring Security + JWT (JJWT)
- Spring Mail (Seznam.cz SMTP)
- MapStruct + Lombok
- OpenAPI (Springdoc)
- Docker & Docker Compose

---

## 🚀 Quick Start

### 🔧 Requirements
- Docker & Docker Compose installed
- Java 17 and Maven (for local builds)

---

### 🔄 Run with Docker Compose

```bash
docker-compose up --build
```

By default, it expects your Spring Boot app to mount `application.properties` from the `./config` directory.

---

### 🧑‍💻 Manual Setup (Local Development)

#### 1. Clone the repository

```bash
git clone https://github.com/your-username/secret-santa.git
cd secret-santa
```

#### 2. Create a PostgreSQL database

You can run a local container or use your own database. Example using Docker:

```bash
docker run --name secretsanta-db -e POSTGRES_DB=secretsanta -e POSTGRES_USER=youruser -e POSTGRES_PASSWORD=yourpass -p 5432:5432 -d postgres
```

#### 3. Configure your properties

Create a file at `./config/application.properties` with the following:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/secretsanta
spring.datasource.username=youruser
spring.datasource.password=yourpass

spring.mail.host=smtp.seznam.cz
spring.mail.port=587
spring.mail.username=your_email@seznam.cz
spring.mail.password=your_email_password

jwt.secret=your_jwt_secret
```

#### 4. Run the application

```bash
mvn spring-boot:run
```

---

## 📁 Frontend

The frontend is a separate project and not bundled in this repo. It communicates with the backend API (e.g., `http://localhost:8080/api/...`).

---

## 🔍 API Documentation

Once running, you can explore the full API via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## ✅ Test the App

```bash
mvn test
```

---

## 📄 License

MIT License. Feel free to use and modify.