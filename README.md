# 🎅 Secret Santa App

A Java Spring Boot application to automate your Secret Santa gift exchange – from participant invitation to anonymous gift assignment, all done via email!
Frontend needs a lot of work! This is just POC and my main goal was always BE. 

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
- JUnit + Mockito

---

## 🚀 Quick Start

### 🔧 Requirements
- Docker & Docker Compose installed
- Java 17 and Maven (for local builds)

---

### 🔄 Run with Docker Compose

### 1. Create a `.env` file in the root directory with your configuration:
```env
# Database
DB_URL=jdbc:postgresql://db:5432/postgres
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=3600000

# Mail
MAIL_HOST=smtp.seznam.cz
MAIL_PORT=465
MAIL_USERNAME=your_email@seznam.cz
MAIL_PASSWORD=your_password

# Application specific
FE_BASE_URL=http://127.0.0.1:5501
CHECK_INTERVAL=360000
EMAIL_ENABLED=true
```
### 2. Start the application

```bash
docker-compose up --build
```
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


#### 3. Run the application

```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

---

### 📁 Frontend

The frontend is served at http://localhost:5501 when running with Docker Compose.
It was my first encounter with HTML,JS,CSS so it´s working and I learned something. Next time I´ll be using propably React instead of vanilaJS and Bootstrap instead of custom CSS.

---

## 🔍 API Documentation

Once running, you can explore the full API via Swagger UI:
http://localhost:8080/swagger-ui.html


---

## ✅ Test the App

```bash
mvn test
```

---

## 📄 License

MIT License. Feel free to use and modify.
