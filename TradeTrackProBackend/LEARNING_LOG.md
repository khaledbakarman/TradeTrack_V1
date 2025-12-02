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

-Last updated: 2025-11-22

## 10) 2025-11-13 — Work Summary

- **Git setup and automation**
  - Initialized Git repo, set remote `origin` to `https://github.com/khaledbakarman/TradeTrack_V1.git`, branch `main`.
  - Created/updated `.gitignore` (project and module-specific ignores) and `README.md`.
  - Added post-commit hook to auto-push after each commit.
  - Added optional Maven profile `auto-git` (exec-maven-plugin) to auto-commit on successful build when run with `-Pauto-git`.

- **Auth feature (register/login)**
  - Added DTOs: `RegisterRequest`, `LoginRequest`, `AuthResponse`.
  - Updated `UserRepository` with `findByUsername`.
  - Implemented `UserService` with registration/login logic and validations:
    - No duplicate usernames (checks repository).
    - Password required. Note: stored in plain text for now (to be replaced with hashing later).
  - Added `UserController` with REST endpoints:
    - `POST /api/auth/register`
    - `POST /api/auth/login`
  - JSON responses like `{ "userId": 1, "message": "Registration successful" }` / `"Login successful"`.

- **Run and test**
  - Application started successfully. When port 8080 was in use, ran on `8081`.
  - Fixed 404 caused by newline encoded in Postman URL (`%0A`). Correct URL must be exactly `http://localhost:<port>/api/auth/register` with no trailing newline.

- **Quick test examples**
  - Register: `POST /api/auth/register` with body `{ "username": "alice", "password": "pass" }` → `200 OK` with userId and message.
  - Login: `POST /api/auth/login` with same credentials → `200 OK` with userId and message.

## 11) 2025-11-15 — Work Summary

- **Trade feature build**
  - Added `TradeRequest` and `TradeResponse` DTOs (clean fields requested by frontend, doubles for prices, userId reference).
  - Created `TradeService` and `TradeController` with full CRUD endpoints:
    - `POST /api/trades` (create), `GET /api/trades?userId` (list by user), `GET /api/trades/{id}`, `PUT /api/trades/{id}`, `DELETE /api/trades/{id}`.
  - Each controller method includes brief comments to memorize the REST flow.

- **Validation & data rules**
  - Manual validation in service: userId required, symbol required, entryPrice > 0.
  - Added helper to convert incoming Double prices to `BigDecimal` so entity fields remain precise.

- **Troubleshooting**
  - Fixed corrupted `TradeRequest.java` by replacing it with a clean version (fields: symbol, entryPrice, exitPrice, profitLoss, notes, userId).
  - Resolved compilation errors caused by type mismatch (Double vs BigDecimal) and missing `tradeDate` getter by updating `TradeService` to match the new DTO structure.

- **Testing**
  - After fixes, project ready to run with `mvn spring-boot:run`. Test endpoints with valid JSON payloads, e.g.:
    ```json
    { "userId": 1, "symbol": "AAPL", "entryPrice": 150.5, "exitPrice": 152.0, "profitLoss": 1.5, "notes": "swing" }
    ```

## 12) 2025-11-17 — Work Summary

- **Frontend-ready CORS config**
  - Created `config/` package and added `WebConfig` bean.
  - Enabled CORS for `http://localhost:4200` with GET/POST/PUT/DELETE/OPTIONS, custom headers, and credentials.

- **Notes**
  - Angular frontend can now call all REST endpoints without browser blocking.
  - Keep additional origins or headers in mind when deploying to other environments.

## 13) 2025-11-18 — Work Summary

- **Angular phase 3 kickoff**
  - Scaffolded `TradeTrackProFrontend` with routing and SCSS, installed npm dependencies.
  - Started `ng serve` so http://localhost:4200 serves the brand-new SPA.
- **Component groundwork**
  - Generated `components/login`, `components/register`, `components/trade-list`, `components/add-trade`, `components/analytics`, and `components/navbar` to support the upcoming UI.
  - The CLI updated `AppModule` with these declarations automatically.

## 14) 2025-11-19 — Work Summary

- **Angular models + service**
  - Added `User` and `Trade` interfaces under `src/app/models` so the frontend knows the payload shapes.
  - Created `TradeService` in `src/app/services` with register/login, trade CRUD, and analytics methods pointing at `http://localhost:8080/api`.
  - Service now uses `HttpClient` to call Spring Boot, so the UI has a shared entry point for backend data. phase 3 step 4 done.

## 15) 2025-11-20 — Work Summary

- **Routing & login UI**
  - Declared routes for `login`, `register`, `trades`, `add-trade`, and `analytics`, defaulting to `login` so the SPA hits the auth screen first.
  - Wired `HttpClientModule` into `AppModule` so HTTP calls can flow from service to API.
  - Implemented `LoginComponent` with two-way binding, the service call, localStorage storage of `userId`, and navigation to `/trades` on success.

## 16) 2025-11-21 — Work Summary

- **Simplified routing and app component/module wiring**
  - Removed redundant imports and simplified routing configuration.
  - Improved module wiring for better organization and maintainability.
- **Login UI enhancements**
  - Added form validation and error handling for a better user experience.
  - Improved UI styling for a more visually appealing design.

**Login polish**
  - Finalized login UI with input validation and error messages.
  - Updated last updated date to reflect the latest changes.

## 17) 2025-11-22 — Work Summary

- **Register workflow polish**
  - Crafted `register.component.html`/SCSS for a clean form, added alerts, and navigation back to `/login`.
  - Fixed `register.component.ts` to post to `http://localhost:8080/api/auth/register` with next/error subscribers so success is handled correctly and backend 400s show the right message.
  - Recovered the `/register` route plus redirect in `app-routing.module.ts` so opening `/register` no longer bounces to `/login`.
  - Confirmed `AppModule` still declares every UI component and imports `FormsModule` so both login/register templates bind properly.

## 18) 2025-11-24 — Work Summary

- **Auth routing and services**
  - Added `AuthService` (login + register helpers) pointing exactly at `http://localhost:8080/api/auth/login`/`register`.
  - Swapped `LoginComponent` to call `AuthService` so the JWT token can be stored and `/trades` is now resolved via router.
  - Added the `/trades` route and kept `/login`/`/register` so navigation remains stable after authentication.

## 19) 2025-11-25 — Work Summary

- **Trade list UI**
  - Rebuilt `trade-list.component.html` to show a trades list, including a zero-state message, profit/loss styles, and trade detail blocks.
  - Added corresponding SCSS so the trade cards have spacing, elevation, and conditional coloring for profit vs loss.

## 20) 2025-11-26 — Work Summary

- **Services & routing polish**
  - Centralized `TradeService` on `http://localhost:8080/api/trades` and added request logging via `tap(...)` so each GET logs success/failure for the current user.
  - Updated `angular.json` serve options to enable `historyApiFallback` so deep links keep landing on the SPA.

## 21) 2025-11-26 — Navbar Implementation

- **Navbar Component**
  - Implemented `NavbarComponent` with "Trades", "Add Trade", "Analytics" links and "Logout" button.
  - Added logic to hide navbar on `/login` and `/register` routes using `Router` events in `AppComponent`.
  - Styled with a clean, professional look (white background, shadow, responsive layout).
  - Implemented `logout()` to clear `userId` from localStorage and redirect to login.

- **Routing Updates**
  - Added routes for `add-trade` and `analytics` in `AppRoutingModule`.
  - Integrated `NavbarComponent` into `AppComponent` template.

## 22) 2025-11-27 — Add Trade Page Implementation

- **Add Trade Component**
  - Implemented `AddTradeComponent` with a form for Symbol, Entry Price, Exit Price, and Notes.
  - Added validation to ensure required fields are filled.
  - Calculates `profitLoss` automatically before sending to backend.
  - Uses `TradeService.addTrade()` to POST data to the API.
  - Redirects to `/trades` upon successful creation.

- **Service Updates**
  - Added `addTrade(trade: any)` method to `TradeService` to handle POST requests.

- **Routing**
  - Confirmed `/add-trade` route is active and linked from the Navbar.

## 23) 2025-11-27 — Real Login API Connection

- **Auth Service**
  - Updated `AuthService.login()` to strictly return the `Observable` from the backend.

- **Login Component**
  - Renamed `login()` to `submit()` for clarity.
  - Removed hardcoded `userId` fallback.
  - Now stores the real `userId` from the backend response into `localStorage`.
  - Redirects to `/trades` immediately after successful login.
  - Updated template to call `submit()` on button click.

## 24) 2025-11-28 — Auth Guard Implementation

- **Auth Guard**
  - Created `AuthGuard` (`src/app/guards/auth.guard.ts`) implementing `CanActivate`.
  - Checks for `userId` in `localStorage`.
  - Returns `true` if user is logged in.
  - Redirects to `/login` and returns `false` if not logged in.

- **Route Protection**
  - Applied `canActivate: [AuthGuard]` to `/trades`, `/add-trade`, and `/analytics` routes in `AppRoutingModule`.
  - Ensures users cannot access these pages without logging in first.
  - Ensures users cannot access these pages without logging in first.

## 25) 2025-11-29 — Analytics Page Implementation

- **Analytics Component**
  - Implemented `AnalyticsComponent` to fetch trades and calculate key metrics.
  - Metrics: Total Trades, Wins, Losses, Win Rate (%), Total Profit/Loss.
  - Created a responsive grid layout for the dashboard using CSS Grid.
  - Added conditional styling (Green for profit/wins, Red for losses).

- **Logic**
  - `calculateStats()` processes the trade list to derive insights dynamically.
  - Handles zero-trade cases gracefully (e.g., 0% win rate).

- **Routing**
  - Confirmed `/analytics` route is protected by `AuthGuard`.

## 26) 2025-11-29 — Edit & Delete Trade Implementation

- **Trade Service**
  - Added `deleteTrade(id)` and `updateTrade(id, payload)` methods.

- **Trade List**
  - Added "Edit" and "Delete" buttons to each trade card.
  - Implemented `deleteTrade()` with a confirmation dialog.
  - Implemented `editTrade()` to navigate to the edit page.

- **Edit Trade Component**
  - Generated `EditTradeComponent` and added route `/edit-trade/:id`.
  - Implemented logic to fetch existing trade data and populate the form.
  - Implemented `save()` method to update the trade via API.
  - Added styles for the edit form.

## 27) 2025-11-29 — Bug Fix: Angular Compile Error

- **Issue**
  - `trade.id` was typed as `number | undefined` in the model, causing compile errors in `trade-list.component.html` when passing it to functions expecting `number`.

- **Fix**
  - Updated `Trade` model (`src/app/models/trade.model.ts`) to make `id` mandatory (`id: number`).
  - Updated `TradeService.getTrades()` to return `Observable<Trade[]>` for strict typing.
  - This resolves the error and ensures type safety across the application.

## 28) 2025-12-01 — Visual Analytics (Charts)

- **Dependencies**
  - Installed `chart.js`, `ng2-charts@5`, and `@angular/cdk@16`.
  - Imported `NgChartsModule` in `AppModule`.

- **Analytics Component**
  - Added **Win/Loss Bar Chart**: Visualizes the count of winning vs. losing trades.
  - Added **Equity Curve (Line Chart)**: Tracks the cumulative profit/loss over time.
  - Implemented logic in `calculateStats()` to dynamically update chart data based on loaded trades.
  - Added CSS Flexbox styling to display charts side-by-side.

## 29) 2025-12-01 — Chart Data Fix

- **Issue**
  - Charts were displaying incorrect data order and counts because trades were not sorted.
  - Win/Loss chart was not updating correctly.

- **Fix**
  - Updated `calculateStats()` to sort trades by ID (`this.trades.sort((a, b) => a.id - b.id)`).
  - Re-implemented Equity Curve logic to push cumulative profit and labels in sync.
  - Fixed Win/Loss chart dataset assignment to ensure correct counts and colors.

## 30) 2025-12-02 — Work Summary

- **JWT Authentication (Backend)**
  - **Dependencies**: Added `jjwt-api`, `jjwt-impl`, and `jjwt-jackson` to `pom.xml`.
  - **Security Components**:
    - Created `JwtUtil`: Handles token generation (signing) and validation (claims extraction).
    - Created `JwtFilter`: Intercepts requests, extracts JWT from `Authorization` header, and sets `userId` in request attribute.
    - Updated `WebConfig`: Registered `JwtFilter` to protect `/api/trades/*` endpoints.
  - **Controller Updates**:
    - **UserController**: Updated `/login` to validate credentials and return a JWT token.
    - **TradeController**: Updated `getTrades` (and `createTrade`) to retrieve `userId` securely from the request attribute instead of request parameters.
    - **UserService**: Added `validateUser` method to support the new login flow.

- **JWT Authentication (Frontend)**
  - **Login Flow**:
    - Updated `LoginComponent` to store the JWT token in `localStorage` upon successful login.
    - Updated `AuthService` to call the correct login endpoint.
  - **Token Interceptor**:
    - Created `TokenInterceptor` to automatically attach the JWT token (Bearer token) to the `Authorization` header of every outgoing HTTP request.
    - Registered the interceptor in `AppModule`.
  - **Service & Component Updates**:
    - Updated `TradeService.getTrades()` to remove the `userId` parameter, as the backend now identifies the user via the token.
    - Updated `TradeListComponent` and `AnalyticsComponent` to call `getTrades()` without arguments.
    - Removed manual `userId` handling from frontend components.

- **Bug Fixes & Stability**
  - **Backend Compilation**: Restored missing package declaration and imports in `UserController.java`.
  - **Bean Conflict**: Renamed `jwtFilter` bean in `WebConfig` to avoid conflict with the class name.
  - **Frontend Compilation**: Updated `EditTradeComponent` and `AddTradeComponent` to match the new `TradeService` signature (no `userId`).
  - **Edit Trade Save**: Fixed `TradeController.updateTrade()` to inject `userId` from the token, allowing updates to succeed without frontend payload changes.
  - **Login Loop**: Updated `AuthGuard` to check for `token` instead of `userId`, fixing the immediate redirect issue.

- **Day Summary**
  - **Achievement Unlocked: Full JWT Authentication** 🔒
    - Replaced temporary "userId in localStorage" with secure JWTs.
    - Backend generates and validates tokens; Frontend attaches them to requests.
    - API endpoints are now protected.
  - **Next Steps**: Advanced Trade Filtering, User Profile, Deployment.
