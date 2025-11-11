# TradeTrackProBackend — Learning Log

This document tracks everything we build in this Spring Boot project, with brief explanations for learning.

## 1) Project Setup

- Created a Spring Boot 3 project (Java 17, Maven) named `TradeTrackProBackend`.
- Base package: `com.tradetrackpro`.
- Standard structure:
  - `src/main/java/com/tradetrackpro/...`
    - `config/`, `controller/`, `dto/`, `exception/`, `model/`, `repository/`, `service/`
    - `TradeTrackProBackendApplication.java` (main class)
  - `src/main/resources/`
    - `application.properties`, `static/`, `templates/`
  - `src/test/java/com/tradetrackpro/...`

## 2) pom.xml Dependencies (What and Why)

- `spring-boot-starter-web`: Build REST APIs with Spring MVC, embedded Tomcat, Jackson, validation.
- `spring-boot-starter-data-jpa`: JPA/Hibernate + repositories + transactions for database access.
- `mysql-connector-j`: MySQL JDBC driver to connect to MySQL.
- `lombok`: Reduces boilerplate (getters/setters/constructors/builders) via annotations.
- `spring-boot-devtools`: Developer convenience for auto-restart and live reload (dev only).
- `spring-boot-starter-test`: Testing (JUnit, AssertJ, Spring Test).

Build plugin:
- `spring-boot-maven-plugin`: Allows `mvn spring-boot:run` and creates an executable jar.

## 3) Configuration (application.properties)

```
spring.datasource.url=jdbc:mysql://localhost:3306/tradetrackdb
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

server.port=8080
```

- `spring.datasource.*`: Database connection details for MySQL.
- `spring.jpa.hibernate.ddl-auto=update`: Auto create/update tables from entities (good for dev).
- `spring.jpa.show-sql=true`: Log SQL statements for learning/debugging.
- `hibernate.dialect`: MySQL dialect (Hibernate 6+ can infer; recommended `org.hibernate.dialect.MySQLDialect`).
- `server.port=8080`: App runs on port 8080.

## 4) Main Application

- `TradeTrackProBackendApplication.java` with `@SpringBootApplication` and `main` method to bootstrap the app.

## 5) Entities (Database Tables via JPA)

### 5.1 User Entity (`model/User.java`)
Fields:
- `id` (Long, PK, auto-generated)
- `username` (String)
- `password` (String)
- `createdAt` (LocalDateTime)

Purpose:
- Represents an application user; rows stored in `users` table.

Key annotations:
- `@Entity`, `@Table("users")`: Maps class to DB table.
- `@Id`, `@GeneratedValue(IDENTITY)`: Primary key with auto-increment.
- Lombok: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`.

### 5.2 Trade Entity (`model/Trade.java`)
Fields:
- `id` (Long, PK, auto-generated)
- `symbol` (String)
- `entryPrice` (BigDecimal)
- `exitPrice` (BigDecimal)
- `profitLoss` (BigDecimal)
- `notes` (String, up to 2000 chars)
- `tradeDate` (LocalDate)
- `user` (User, ManyToOne relationship)

Purpose:
- Represents a single trade, linked to a user; rows stored in `trades` table.

Key annotations:
- `@Entity`, `@Table("trades")`.
- `@ManyToOne(fetch = LAZY)` + `@JoinColumn(name = "user_id", nullable = false)`: Many trades belong to one user using FK `user_id`.

## 6) Relationships

- `@ManyToOne`: Many `Trade` records refer to the same `User`.
- DB effect: `trades.user_id` is a foreign key to `users.id`.

## 7) Repositories (Data Access Layer)

- `UserRepository` extends `JpaRepository<User, Long>`.
- `TradeRepository` extends `JpaRepository<Trade, Long>`.

What `JpaRepository` gives us:
- CRUD methods out of the box: `save`, `findById`, `findAll`, `deleteById`, etc.
- Spring Data auto-implements repositories and uses MySQL datasource to execute SQL via Hibernate.

## 8) Build and Run

From project folder:
- Build: `mvn clean package`
- Run via Maven: `mvn spring-boot:run`
- Run via Jar: `java -jar target/TradeTrackProBackend-0.0.1-SNAPSHOT.jar`

Common notes:
- Ensure MySQL is running and DB `tradetrackdb` exists.
- Credentials used: `root` / `root` (adjust as needed).
- Hibernate warnings about dialect are informational; you can set `org.hibernate.dialect.MySQLDialect` or remove the property to let it auto-detect.

## 9) Next Learning Steps (Planned)

- Add DTOs and controllers for User and Trade CRUD.
- Add service layer with basic business logic.
- Validation on request payloads (Jakarta Validation).
- Basic error handling in `exception/` (`@ControllerAdvice`).
- Seed data / schema evolution (Flyway or Liquibase).
- Security (Spring Security) if needed later.

---
Last updated: 2025-11-10
