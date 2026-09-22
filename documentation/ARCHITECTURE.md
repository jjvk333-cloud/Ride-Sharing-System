# VELTO — System Architecture & Technical Specification

## 1. Executive Summary
**VELTO** is an enterprise-grade, full-stack ride sharing platform engineered for campus and metropolitan commuter ecosystems. Built with **Java 17**, **Spring Boot 3.3.4**, and **MongoDB**, VELTO models real-world transportation logistics with atomic booking transactions, dynamic demand-driven pricing, unified payment gateway adapters, and lifecycle state machines.

A central objective of the platform is demonstrating **8 Gang of Four (GoF) Design Patterns**, each purposefully addressing a specific architectural bottleneck in real-world distributed ride sharing.

---

## 2. High-Level Architectural Blueprint

```mermaid
graph TD
    Client["Client Presentation Layer<br>(HTML5 / Bootstrap 5 / Fetch API)"]
    
    subgraph SpringBootApp["Spring Boot 3.3.4 Backend Container"]
        Controller["REST API Controllers<br>(Auth, Ride, Booking, Payment, User, Config, Notif)"]
        Security["Spring Security & BCrypt<br>Credential Encryption & Role Authorization"]
        
        subgraph PatternLayer["GoF Design Pattern Subsystems"]
            F1["Factory Method: UserFactory<br>(Passenger, Driver, Admin)"]
            F2["Strategy: PricingStrategy<br>(Standard, Peak Surge, Shared)"]
            F3["Builder: RideBuilder<br>(Validation & Construction)"]
            F4["Facade: RideBookingFacade<br>(Subsystem Orchestration & Payment)"]
            F5["State: RideState<br>(7-Phase Finite State Machine)"]
            F6["Observer: RideEventSubject<br>(Multi-Actor Notifications)"]
            F7["Adapter: PaymentProcessor<br>(UPI, Card, Cash Gateways)"]
            F8["Singleton: AppConfigSingleton<br>(Double-Checked Locking Runtime Config)"]
        end
        
        Services["Domain Services Layer<br>(UserService, RideService, PaymentService, NotificationService)"]
        Repositories["Spring Data Repositories<br>(User, Ride, Booking, Payment, Notification)"]
    end
    
    subgraph DataStore["Persistence Layer"]
        MongoDB[("MongoDB Community Server<br>Database: 'velto' (Port 27017)")]
    end

    Client -->|HTTP / JSON REST API| Controller
    Controller --> Security
    Controller --> Services
    Services --> PatternLayer
    PatternLayer --> Repositories
    Services --> Repositories
    Repositories -->|Spring Data Wire Protocol| MongoDB
```

---

## 3. Layered Architectural Model

VELTO strictly adheres to the **Hexagonal / Layered Architecture** paradigm:

1. **Presentation Layer (`frontend/`)**:
   - Zero-bloat client: HTML5, CSS3 Variables, Bootstrap 5.3, Bootstrap Icons.
   - Modular Fetch API client (`api.js`) communicating asynchronously via standard REST/JSON over HTTP.
   - Client-side session and role management (`auth.js`) stored in `localStorage`.
   - Dynamic view switcher supporting Passenger, Driver, Admin, and 8 Design Patterns Showcase portals.

2. **API & Controller Layer (`com.velto.controller`)**:
   - Exposes RESTful endpoints conforming to HTTP standards (GET, POST, PUT, PATCH, DELETE).
   - Enforces cross-origin resource sharing (`CorsConfig.java`) allowing safe frontend integration.
   - Validates all incoming payloads using Bean Validation (`jakarta.validation.*`).
   - Centralizes error response handling via `@RestControllerAdvice` (`GlobalExceptionHandler.java`).

3. **Pattern & Subsystem Layer (`com.velto.pattern.*`)**:
   - Encapsulates discrete software engineering algorithms into isolated, testable design patterns:
     - `com.velto.pattern.factory`: Dynamic instantiation of polymorphic user classes.
     - `com.velto.pattern.strategy`: Demand and occupancy-based dynamic fare calculations.
     - `com.velto.pattern.builder`: Step-by-step verified construction of immutable ride documents.
     - `com.velto.pattern.facade`: High-level coordination of the 6-step ride booking transaction.
     - `com.velto.pattern.state`: Strict state transitions and invariant protection for ride lifecycles.
     - `com.velto.pattern.observer`: Real-time decoupled notifications to passengers, drivers, and admins.
     - `com.velto.pattern.adapter`: Unification of disparate external payment gateways (UPI, Card, Cash).
     - `com.velto.pattern.singleton`: Thread-safe, double-checked locking global configuration manager.

4. **Service & Business Logic Layer (`com.velto.service`)**:
   - Implements core business logic, orchestrates cross-repository updates, and hashes credentials using BCrypt.

5. **Persistence & Data Access Layer (`com.velto.repository`)**:
   - Leverages Spring Data MongoDB `MongoRepository<T, ID>` with custom query derivations and compound indexing.

6. **Database Layer (`database/`)**:
   - MongoDB Community Server with polymorphic document mapping (`_class` discriminator for Users).

---

## 4. Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    USER ||--o{ RIDE : "publishes (Driver)"
    USER ||--o{ BOOKING : "reserves (Passenger)"
    USER ||--o{ NOTIFICATION : "receives"
    RIDE ||--o{ BOOKING : "contains"
    BOOKING ||--o| PAYMENT : "reconciles"

    USER {
        string id PK
        string name
        string email UK
        string password
        string role
        string phone
        string vehicleNumber
        string licenseNumber
    }

    RIDE {
        string id PK
        string driverId FK
        string driverName
        string pickup
        string destination
        string date
        string time
        int availableSeats
        double price
        string status
        datetime createdAt
    }

    BOOKING {
        string id PK
        string rideId FK
        string passengerId FK
        string passengerName
        int seats
        double amount
        string pricingType
        string bookingStatus
        string paymentStatus
        datetime createdAt
    }

    PAYMENT {
        string id PK
        string bookingId FK
        string passengerId FK
        double amount
        string paymentMethod
        string paymentStatus
        string transactionId
        string gatewayReference
        datetime completedAt
    }

    NOTIFICATION {
        string id PK
        string userId FK
        string message
        string type
        boolean read
        datetime createdAt
    }
```

---

## 5. Security & Authentication Architecture
- **Password Hashing**: BCrypt strong hashing algorithm with 10 salt rounds (`BCryptPasswordEncoder`).
- **Authorization**: Role-based access control checking `Role.PASSENGER`, `Role.DRIVER`, `Role.ADMIN`.
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Min`, `@NotNull`, `@Size`) guarding against malformed payloads.
- **SQL/NoSQL Injection Defense**: Spring Data MongoDB parameterized query builders defend against operator injection.
- **Global Error Masking**: `GlobalExceptionHandler` ensures stack traces are never leaked to external clients.
