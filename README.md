# 🚗 VELTO — Intelligent Ride Sharing System

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB Community 8.x](https://img.shields.io/badge/MongoDB-Community%20Server-green.svg)](https://www.mongodb.com/)
[![Maven 3.9+](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![Bootstrap 5.3](https://img.shields.io/badge/Frontend-Bootstrap%205.3-purple.svg)](https://getbootstrap.com/)
[![Tests Passing](https://img.shields.io/badge/Tests-44%20Passed-success.svg)](#automated-testing)

**VELTO** is an enterprise-grade, full-stack ride sharing platform engineered for campus and metropolitan commuter ecosystems. Built with **Java 17**, **Spring Boot 3.3.4**, **MongoDB**, and a responsive **HTML5/Bootstrap 5** frontend, VELTO demonstrates **8 Gang of Four (GoF) Design Patterns** solving real-world transportation challenges.

---

## 🌟 Key Highlights
- **No-Bloat Architecture**: Pure Java 17 + Spring Boot 3 backend and pure HTML5/CSS3/Vanilla JS frontend (no heavy npm build steps or bloated frontend frameworks).
- **8 GoF Design Patterns**: Factory Method, Strategy, Builder, Facade, State, Observer, Adapter, and Singleton patterns fully implemented, tested, and documented.
- **1-Click Viva Demo Bar**: Top banner with instant 1-click login for Admin, Driver, and Passenger roles.
- **Dynamic Pricing Engine**: Real-time Strategy pattern computing Standard (1.0x), Peak Surge (1.5x), and Shared Carpooling (0.8x) fares.
- **Unified Payment Adapters**: Harmonizes UPI VPA, Credit/Debit Cards, and Cash settlement behind a single processor interface.
- **Automated Seeder**: Pre-provisions sample demo accounts, active rides, and notifications on clean startup.

---

## 👥 Demo Accounts (Pre-Seeded)

| Role | Name | Email | Password | Details |
|---|---|---|---|---|
| **ADMIN** | Velto Admin | `admin@velto.com` | `Admin@123` | Full system audit & runtime configuration |
| **DRIVER** | Rajesh Kumar | `rajesh.driver@velto.com` | `Driver@123` | Vehicle: `MH-12-AB-1234` |
| **DRIVER** | Amit Sharma | `amit.driver@velto.com` | `Driver@123` | Vehicle: `MH-14-XY-5678` |
| **PASSENGER** | Priya Patel | `priya.passenger@velto.com` | `Passenger@123` | Booking & Payment access |
| **PASSENGER** | Rohit Verma | `rohit.passenger@velto.com` | `Passenger@123` | Booking & Payment access |

---

## 🏗️ 8 Gang of Four (GoF) Design Patterns

| Pattern | Category | Java Package | Real-World Problem Solved |
|---|---|---|---|
| **1. Factory Method** | Creational | `com.velto.pattern.factory` | Polymorphic instantiation of `Passenger`, `Driver`, and `Admin` user types. |
| **2. Strategy** | Behavioral | `com.velto.pattern.strategy` | Dynamic pricing calculation (Standard, Peak Surge 1.5x, Shared Discount 0.8x). |
| **3. Builder** | Creational | `com.velto.pattern.builder` | Fluent construction and invariant validation of complex `Ride` documents. |
| **4. Facade** | Structural | `com.velto.pattern.facade` | Atomic orchestration of the 6-step ride booking and seat-decrement transaction. |
| **5. State** | Behavioral | `com.velto.pattern.state` | Managing the 7-phase ride lifecycle finite state machine and blocking illegal transitions. |
| **6. Observer** | Behavioral | `com.velto.pattern.observer` | Real-time decoupled notification broadcasting to passengers, drivers, and admins. |
| **7. Adapter** | Structural | `com.velto.pattern.adapter` | Unified payment interface adapting UPI VPA, 16-digit Card, and Cash counter APIs. |
| **8. Singleton** | Creational | `com.velto.pattern.singleton` | Centralized, thread-safe runtime configuration with Double-Checked Locking (DCL). |

---

## 🚀 Quick Start Guide

### Prerequisites
1. **Java 17 JDK** (`java -version` returns `17.x`)
2. **Maven 3.9+** (`mvn -version`)
3. **MongoDB Community Server** running locally on port `27017`

### 1. One-Click Startup (Windows)
Double-click **`start-velto.bat`** in the project root. It will:
1. Verify MongoDB connectivity.
2. Build and launch the Spring Boot backend (`http://localhost:8080`).
3. Launch the responsive frontend in your default browser.

### 2. Manual Startup

#### Step A: Start Backend
```bash
cd backend
mvn spring-boot:run
```
Backend will start on: `http://localhost:8080` (Auto-seeds demo data if database is empty).

#### Step B: Open Frontend
Simply open `frontend/index.html` in any modern web browser, or serve via VS Code Live Server / Python:
```bash
cd frontend
python -m http.server 3000
```
Open: `http://localhost:3000`

---

## 🧪 Automated Testing
Run the complete test suite containing **44 automated tests**:
```bash
cd backend
mvn clean test
```

### Test Suite Summary
- `UserFactoryTests`: Factory Method polymorphic instantiation.
- `PricingStrategyTests`: Dynamic strategy pricing calculations.
- `RideBuilderTests`: Builder pattern mandatory parameter validation.
- `BookingFacadeTests`: 6-step facade reservation coordination.
- `RideStateTests`: Lifecycle forward transitions and illegal transition rejection.
- `RideObserverTests`: Multi-actor alert broadcasting.
- `PaymentAdapterTests`: UPI, Card, and Cash adapter translations.
- `SingletonPatternTests`: 50 concurrent worker threads, reflection attack defense, and serialization preservation.

---

## 📁 Project Directory Structure
```
Ride sharing System/
├── backend/                              # Spring Boot 3.3.4 Application
│   ├── src/main/java/com/velto/
│   │   ├── config/                       # CORS, MongoDB Verifier, DataSeeder
│   │   ├── controller/                   # REST API Controllers
│   │   ├── dto/                          # Data Transfer Objects
│   │   ├── exception/                    # Global Exception Handler & Custom Errors
│   │   ├── model/                        # MongoDB Document Entities & Enums
│   │   ├── pattern/                      # 8 Gang of Four Design Patterns
│   │   │   ├── factory/                  # Factory Method Pattern
│   │   │   ├── strategy/                 # Strategy Pattern
│   │   │   ├── builder/                  # Builder Pattern
│   │   │   ├── facade/                   # Facade Pattern
│   │   │   ├── state/                    # State Pattern
│   │   │   ├── observer/                 # Observer Pattern
│   │   │   ├── adapter/                  # Adapter Pattern
│   │   │   └── singleton/                # Singleton Pattern
│   │   ├── repository/                   # Spring Data Repositories
│   │   └── service/                      # Business Services
│   └── src/test/java/com/velto/          # 44 Automated Unit & Pattern Tests
├── frontend/                             # Responsive Web Frontend
│   ├── css/styles.css                    # Modern CSS Theme
│   ├── js/api.js                         # Central REST API Client
│   ├── js/auth.js                        # Session Management
│   ├── js/app.js                         # View Controller & Pattern Showcase
│   └── index.html                        # Main Single-Page Application
├── database/                             # Database Schema & Initialization
│   └── init-mongo.js
├── documentation/                        # Comprehensive Architectural & Viva Docs
│   ├── ARCHITECTURE.md                   # System Architecture Specification
│   ├── DESIGN_PATTERNS.md                # 8 GoF Patterns Catalog & UML Diagrams
│   └── VIVA_QUESTIONS_AND_ANSWERS.md     # 30+ Viva Examiner Q&A Guide
├── postman/                              # Postman API Collections
│   └── Velto_Master_API.postman_collection.json
├── start-velto.bat                       # 1-Click Windows Launch Script
└── README.md                             # Project Documentation
```

---

## 📜 Documentation Links
- [System Architecture Specification](documentation/ARCHITECTURE.md)
- [Complete 8 GoF Design Patterns Catalog](documentation/DESIGN_PATTERNS.md)
- [Examiner Viva Q&A Guide](documentation/VIVA_QUESTIONS_AND_ANSWERS.md)
