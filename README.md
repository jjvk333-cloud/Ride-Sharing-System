# 🚗 VELTO — Intelligent Full-Stack Ride Sharing System

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB Community 8.x](https://img.shields.io/badge/MongoDB-Community%20Server-green.svg)](https://www.mongodb.com/)
[![Maven 3.9+](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![Bootstrap 5.3](https://img.shields.io/badge/Frontend-Bootstrap%205.3-purple.svg)](https://getbootstrap.com/)
[![Tests Passing](https://img.shields.io/badge/Tests-53%20Passed-success.svg)](#-automated-testing-verification)

**VELTO** is an enterprise-grade, realistic full-stack ride sharing platform engineered for campus and metropolitan commuter ecosystems. Built with **Java 17**, **Spring Boot 3.3.4**, **MongoDB**, and a responsive **HTML5/Bootstrap 5/Leaflet.js** frontend, VELTO demonstrates **8 Gang of Four (GoF) Design Patterns** working authentically behind the scenes in real business workflows.

---

## 🌟 Key Application Features

- **Realistic Ride Sharing UI**: Modern hero banner, vehicle category pills (Bike, Auto, Sedan, SUV), interactive ride catalogue, dynamic seat badges, and printable QR boarding passes.
- **Dedicated Live Ride Tracker (`#view-tracking`)**: Real-time visual stepper displaying finite state machine transitions (`REQUESTED` ➔ `CONFIRMED` ➔ `DRIVER_ASSIGNED` ➔ `DRIVER_ARRIVING` ➔ `IN_PROGRESS` ➔ `COMPLETED`), animated progress bar, driver call/message simulation, and embedded Leaflet GPS routing.
- **Driver Portal with Online/Offline Switch**: Manage published rides, toggle availability status, and advance ride state step-by-step with real-time observer notifications.
- **1-Click College Viva Demo Bar**: Instant one-click authentication for Admin, Driver (Rajesh), and Passenger (Priya).
- **Dedicated Academic 8 Patterns Section (`#view-patterns`)**: Full architectural breakdown and interactive live execution consoles for every GoF pattern.
- **53/53 Automated Tests Passing**: Robust test coverage across all layers with clean Maven builds.

---

## 👥 Demo Accounts (Pre-Seeded)

| Role | Name | Email | Password | Details |
|---|---|---|---|---|
| **ADMIN** | Velto Admin | `admin@velto.com` | `Admin@123` | Full system audit, KPI dashboard & runtime Singleton config |
| **DRIVER** | Rajesh Kumar | `rajesh.driver@velto.com` | `Driver@123` | Vehicle: `MH-12-AB-1234` (Sedan / SUV) |
| **DRIVER** | Amit Sharma | `amit.driver@velto.com` | `Driver@123` | Vehicle: `MH-14-XY-5678` (Auto / Bike) |
| **PASSENGER** | Priya Patel | `priya.passenger@velto.com` | `Passenger@123` | Active bookings, boarding pass, Live Tracker |
| **PASSENGER** | Rohit Verma | `rohit.passenger@velto.com` | `Passenger@123` | Active booking & payment access |

---

## 🏗️ 8 Gang of Four (GoF) Design Patterns Implementation

| # | Pattern | Category | Java Package | Real Business Responsibility in VELTO |
|---|---|---|---|---|
| 1 | **Factory Method** | Creational | `com.velto.pattern.factory` | Polymorphic creation of `Passenger`, `Driver`, and `Admin` entities with strict role validation and default attributes. |
| 2 | **Strategy** | Behavioral | `com.velto.pattern.strategy` | Runtime calculation of fares via interchangeable algorithms: `StandardPricingStrategy` (1.0x), `PeakPricingStrategy` (1.5x surge), and `SharedRidePricingStrategy` (0.8x carpool discount). |
| 3 | **Builder** | Creational | `com.velto.pattern.builder` | Fluent construction of complex `Ride` documents (`RideBuilder` / `Ride.Builder`), validating invariants before persistence. |
| 4 | **Facade** | Structural | `com.velto.pattern.facade` | `RideBookingFacade` acts as a unified orchestrator coordinating User verification, Ride state validation, Strategy pricing, Payment Adapter processing, seat decrement, and Observer alerts. |
| 5 | **State** | Behavioral | `com.velto.pattern.state` | Manages 7 distinct ride lifecycle states (`REQUESTED`, `CONFIRMED`, `DRIVER_ASSIGNED`, `DRIVER_ARRIVING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`) and strictly blocks illegal jumps. |
| 6 | **Observer** | Behavioral | `com.velto.pattern.observer` | `RideEventSubject` broadcasts decoupled status alerts to registered `PassengerNotificationObserver`, `DriverNotificationObserver`, and `AdminNotificationObserver`. |
| 7 | **Adapter** | Structural | `com.velto.pattern.adapter` | Harmonizes third-party payment gateways (`UpiPaymentAdapter` for VPA, `CardPaymentAdapter` for 16-digit cards, and `MockPaymentAdapter` for cash settlement) behind `PaymentProcessor`. |
| 8 | **Singleton** | Creational | `com.velto.pattern.singleton` | `AppConfigSingleton` guarantees a single, thread-safe instance managing runtime fare parameters, surge multipliers, and maintenance mode using Double-Checked Locking (DCL). |

---

## 🚀 Quick Start Guide

### Prerequisites
1. **Java 17 JDK** (`java -version` returns `17.x`)
2. **Maven 3.9+** (`mvn -version`)
3. **MongoDB Community Server** running locally on port `27017`

### Method 1: Instant One-Click Launcher (Windows)
Double-click **`start-velto.bat`** in the project root folder. It checks MongoDB, compiles the backend, starts the Spring Boot server on port `8080`, and opens the frontend in your browser.

### Method 2: Manual Terminal Execution

#### Terminal 1 — Start Backend:
```powershell
cd backend
mvn spring-boot:run
```
The server will start at `http://localhost:8080`.

#### Terminal 2 — Access Frontend:
The frontend is served directly by Spring Boot at:
```text
http://localhost:8080/index.html
```
Or open `frontend/index.html` directly in your browser.

---

## 🧪 Automated Testing Verification

The project includes **53 automated unit and integration tests** verifying all business logic and design patterns:

```powershell
cd backend
mvn clean test
```

### Test Suite Execution Output:
```text
[INFO] Results:
[INFO] 
[INFO] Tests run: 53, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🗺️ Project File Structure

```text
Ride sharing System/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/velto/
│       │   │   ├── config/              # Security, CORS, Mongo, DataSeeder
│       │   │   ├── controller/          # REST Controllers (Auth, Rides, Bookings, Config, etc.)
│       │   │   ├── dto/                 # Request & Response DTOs
│       │   │   ├── model/               # Domain Models (User, Ride, Booking, Payment, Notification)
│       │   │   ├── pattern/             # The 8 GoF Design Patterns
│       │   │   │   ├── adapter/         # PaymentProcessor & Adapters
│       │   │   │   ├── builder/         # RideBuilder
│       │   │   │   ├── facade/          # RideBookingFacade
│       │   │   │   ├── factory/         # UserFactory
│       │   │   │   ├── observer/        # RideEventSubject & Observers
│       │   │   │   ├── singleton/       # AppConfigSingleton
│       │   │   │   ├── state/           # RideState finite state machine
│       │   │   │   └── strategy/        # PricingStrategy & Context
│       │   │   ├── repository/          # Spring Data MongoDB Repositories
│       │   │   └── service/             # Business Services
│       │   └── resources/
│       │       ├── application.properties
│       │       └── static/              # Bundled Frontend (HTML/CSS/JS)
│       └── test/java/com/velto/         # 53 Automated Tests for all 8 Patterns
├── frontend/
│   ├── index.html                       # Modern Full-Stack UI with Live Tracker & Academic Tabs
│   ├── css/styles.css                   # Custom Stepper, Vehicle Cards, & Responsive Design
│   └── js/
│       ├── api.js                       # Centralized REST API Client
│       ├── auth.js                      # Token Management & State
│       └── app.js                       # Full Interactive Controller, Maps & Steppers
├── start-velto.bat                      # One-click startup script
└── README.md                            # Comprehensive Documentation
```
