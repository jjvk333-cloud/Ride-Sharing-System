# VELTO — College Project Viva Questions & Master Answers

This comprehensive guide is designed for external examiners, university viva evaluations, and technical defenses. Every question has a precise, technically sound answer with exact codebase references.

---

## Category 1: Architecture & Technical Foundations

### Q1. What is VELTO and what is its underlying architectural design?
**Answer:**
VELTO is an enterprise-grade ride sharing platform built on a **Layered (Hexagonal) Architecture** using **Java 17**, **Spring Boot 3.3.4**, and **MongoDB**. The architecture cleanly decouples the Client Presentation layer (HTML5/Bootstrap 5/Fetch API) from the REST Controller API layer, which coordinates domain services, 8 GoF design pattern subsystems, Spring Data MongoDB repositories, and an ACID-transactional database layer.

---

### Q2. Why did you choose Spring Boot 3 instead of older Spring Boot 2?
**Answer:**
Spring Boot 3 brings baseline support for **Java 17**, upgraded HTTP/2 and Virtual Threads capabilities, and fully adopts the **Jakarta EE 10** specifications (`jakarta.validation.*`, `jakarta.servlet.*`) replacing legacy `javax.*` packages. It also provides enhanced MongoDB 8 observation metrics and native GraalVM compatibility.

---

### Q3. Why did you choose MongoDB over a traditional Relational Database (like MySQL)?
**Answer:**
1. **Polymorphic Modeling**: Using MongoDB, our `User` document collection seamlessly persists `Passenger`, `Driver`, and `Admin` subclasses with their respective fields (like vehicle number and license) in a single collection using the Spring Data `_class` type discriminator. In SQL, this would require complex Single Table Inheritance or multi-table JOINs.
2. **Document Locality**: A ride, its bookings, and its lifecycle states map naturally to hierarchical JSON BSON documents, delivering sub-millisecond retrieval without relational foreign-key joins.
3. **Horizontal Scalability**: Ride sharing generates high-velocity location and status updates where NoSQL document stores scale horizontally via replica sets and sharding.

---

## Category 2: Deep Dive into the 8 GoF Design Patterns

### Q4. Walk us through all 8 Design Patterns implemented in your project.
**Answer:**
1. **Factory Method (Creational)**: `UserFactory` in `com.velto.pattern.factory` dynamically instantiates role-specific `Passenger`, `Driver`, or `Admin` user documents.
2. **Strategy (Behavioral)**: `PricingStrategy` in `com.velto.pattern.strategy` enables dynamic runtime fare calculations (`StandardPricingStrategy`, `PeakPricingStrategy`, `SharedRidePricingStrategy`) via `PricingContext`.
3. **Builder (Creational)**: Dedicated `RideBuilder` in `com.velto.pattern.builder` constructs immutable, validated `Ride` documents avoiding telescoping constructors.
4. **Facade (Structural)**: `RideBookingFacade` in `com.velto.pattern.facade` coordinates the entire end-to-end booking transaction (verification, strategy pricing, payment adapter settlement, seat reservation, persistence, and observer alerting) behind one unified method call.
5. **State (Behavioral)**: `RideState` in `com.velto.pattern.state` encapsulates the 7-phase ride lifecycle finite state machine and guards against illegal transitions.
6. **Observer (Behavioral)**: `RideEventSubject` in `com.velto.pattern.observer` broadcasts ride status events to `PassengerNotificationObserver`, `DriverNotificationObserver`, and `AdminNotificationObserver`.
7. **Adapter (Structural)**: `PaymentProcessor` in `com.velto.pattern.adapter` adapts incompatible external gateway APIs (UPI VPA, Credit/Debit Card 16-digit engine, Cash counter).
8. **Singleton (Creational)**: `AppConfigSingleton` in `com.velto.pattern.singleton` ensures a thread-safe, double-checked locked global runtime configuration instance.

---

### Q5. Explain the State Pattern in Velto. What happens if someone tries an illegal transition?
**Answer:**
The State Pattern allows a `Ride` object to alter its behavior when its internal state changes. We defined the `RideState` interface with operations like `confirm()`, `assignDriver()`, `startRide()`, and `completeRide()`. 
If a client attempts an illegal transition (e.g. attempting to jump from `REQUESTED` directly to `COMPLETED` or `IN_PROGRESS`), the state class throws an `InvalidRideStateException`. Our `@RestControllerAdvice` intercepts this and returns a clean `HTTP 400 Bad Request` with an exact explanation of why the transition is invalid.

---

### Q6. Why did you use the Facade Pattern for Ride Booking?
**Answer:**
Ride booking is an atomic business transaction touching multiple disparate subsystems:
1. Passenger verification
2. Ride status validation
3. Available seat verification & seat count decrement
4. Dynamic pricing strategy execution
5. Payment processor authorization via Adapter Pattern (UPI, Card, Mock)
6. Booking and Payment documents persistence in MongoDB
7. Notification dispatch via Observer Pattern to Passenger and Driver
Without a Facade, the web controller would need to inject 6 different repositories and services, introducing high coupling and fragile error handling. `RideBookingFacade` encapsulates all steps behind `bookRide(CreateBookingRequest)`, providing high cohesion and low coupling.

---

### Q7. How does your Adapter Pattern solve real payment gateway integration?
**Answer:**
External payment gateways have fundamentally incompatible interfaces:
- PhonePe/GPay expects `payViaVpa(String vpa, double rupees)`
- Stripe/Mastercard expects `executeCardCharge(String card16, String exp, String cvv, double amount)`
- Cash counters expect `settleDirect(String orderRef, double amount)`
Our unified `PaymentProcessor` interface defines `processPayment(PaymentRequest)`. The concrete adapters (`UpiPaymentAdapter`, `CardPaymentAdapter`, `MockPaymentAdapter`) adapt Velto's standard DTOs into the provider's specific API calls and translate results back into a standard `PaymentResponse`.

---

### Q8. How is your Singleton Pattern implemented, and how do you protect it against reflection and multithreading attacks?
**Answer:**
`AppConfigSingleton` uses **Double-Checked Locking (DCL)** with a `volatile` instance variable:
1. **Volatile keyword**: Guarantees cache visibility across CPU cores and prevents instruction reordering during instantiation.
2. **Double-Checked Locking**: Only synchronizes the block if `instance == null`, ensuring high performance once initialized.
3. **Reflection Defense**: The private constructor checks `if (instance != null) throw new IllegalStateException(...)`, preventing reflective instantiation.
4. **Serialization Safety**: Implements `readResolve()` returning `getInstance()` so deserialization does not create duplicate instances.
5. **Clone Prevention**: Overrides `clone()` to throw `CloneNotSupportedException`.

---

### Q9. What is the difference between the Factory Method and Abstract Factory patterns? Why did you choose Factory Method?
**Answer:**
- **Factory Method** defines an interface for creating a single product object, leaving the concrete subclass instantiation to subclasses (`UserFactory` producing `User` subtypes).
- **Abstract Factory** creates families of related or dependent objects without specifying their concrete classes (e.g., creating a MacButton and MacCheckbox).
Since we are producing individual polymorphic user instances with distinct role attributes, Factory Method is the correct, clean design pattern without unnecessary architectural bloat.

---

## Category 3: Database & Persistence

### Q10. How do you handle concurrency when two passengers book the last seat on a ride simultaneously?
**Answer:**
In `RideBookingFacade`, seat availability is checked before decrementing. In high-concurrency production, Spring Data MongoDB supports atomic conditional updates (`findAndModify` with query `{ _id: rideId, availableSeats: { $gte: requestedSeats } }`), ensuring that only one concurrent booking succeeds and the other receives an "Insufficient seats available" exception.

---

### Q11. Did you define any database indexes in MongoDB?
**Answer:**
Yes. In `init-mongo.js` and Spring Data annotations:
- `users`: Unique index on `email` (`{ email: 1 }`).
- `rides`: Compound index on pickup, destination, and status (`{ pickup: 1, destination: 1, status: 1 }`).
- `bookings`: Index on `passengerId` and `rideId`.
- `notifications`: Compound index on `userId` and `read` status (`user_read_idx`).

---

## Category 4: Security, REST API & Testing

### Q12. How are passwords secured in the database?
**Answer:**
Passwords are never stored in plaintext. In `UserServiceImpl`, incoming passwords pass through Spring Security's `BCryptPasswordEncoder.encode(rawPassword)`. BCrypt incorporates a 10-round cryptographic salt, rendering dictionary attacks and rainbow table lookups computationally infeasible.

### Q13. How do you know your application works reliably? What is your test coverage?
**Answer:**
We built a suite of **53 automated JUnit 5 tests** across unit, pattern, repository, and controller layers:
- `UserFactoryTests`: Verifies correct polymorphic user instantiation.
- `PricingStrategyTests`: Verifies 1.0x, 1.5x surge, and 0.8x discount calculations.
- `RideBuilderTests`: Validates mandatory attributes and immutability for both `RideBuilder` and `Ride.Builder`.
- `BookingFacadeTests`: Verifies end-to-end coordination with Payment Adapters, seat decrement, payment failure rollback, and cancellation.
- `SecurityAuthorizationTests`: Verifies role-based authorization for Passenger, Driver, and Admin access.
- `RideStateTests`: Validates legal forward transitions and illegal transition exceptions.
- `RideObserverTests`: Confirms passenger, driver, and admin alert dispatches.
- `PaymentAdapterTests`: Tests UPI, Card, and Mock gateway translations.
- `SingletonPatternTests`: Runs 50 concurrent worker threads verifying identical instance identity, reflection defense, and serialization preservation.
All 53 tests pass with `0 failures, 0 errors` in `mvn clean test`.
