package com.velto.pattern.builder;

import com.velto.model.Booking;
import com.velto.model.BookingStatus;
import com.velto.model.PaymentStatus;
import com.velto.pattern.strategy.PricingType;

import java.time.LocalDateTime;

/**
 * =========================================================================
 * DESIGN PATTERN 3 — BUILDER PATTERN (Creational)
 * =========================================================================
 * BookingBuilder constructs complex Booking documents step-by-step
 * with validation for passenger, ride, seats, and fare attributes.
 */
public class BookingBuilder {

    private String id;
    private String rideId;
    private String passengerId;
    private String passengerName;
    private int seats;
    private double amount;
    private double distance;
    private PricingType pricingType = PricingType.STANDARD;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private BookingStatus bookingStatus = BookingStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    public BookingBuilder() {}

    public BookingBuilder id(String id) {
        this.id = id;
        return this;
    }

    public BookingBuilder rideId(String rideId) {
        this.rideId = rideId;
        return this;
    }

    public BookingBuilder passengerId(String passengerId) {
        this.passengerId = passengerId;
        return this;
    }

    public BookingBuilder passengerName(String passengerName) {
        this.passengerName = passengerName;
        return this;
    }

    public BookingBuilder seats(int seats) {
        this.seats = seats;
        return this;
    }

    public BookingBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public BookingBuilder distance(double distance) {
        this.distance = distance;
        return this;
    }

    public BookingBuilder pricingType(PricingType pricingType) {
        this.pricingType = pricingType != null ? pricingType : PricingType.STANDARD;
        return this;
    }

    public BookingBuilder paymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public BookingBuilder bookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
        return this;
    }

    public BookingBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        return this;
    }

    public Booking build() {
        if (rideId == null || rideId.isBlank()) {
            throw new IllegalStateException("Ride ID is required to build a Booking");
        }
        if (passengerId == null || passengerId.isBlank()) {
            throw new IllegalStateException("Passenger ID is required to build a Booking");
        }
        if (seats <= 0) {
            throw new IllegalStateException("Seats must be at least 1");
        }
        if (amount < 0) {
            throw new IllegalStateException("Amount cannot be negative");
        }

        Booking booking = new Booking(
                this.rideId,
                this.passengerId,
                this.passengerName != null ? this.passengerName : "Passenger",
                this.seats,
                this.amount,
                this.distance,
                this.pricingType,
                this.paymentStatus != null ? this.paymentStatus : PaymentStatus.PAID,
                this.bookingStatus != null ? this.bookingStatus : BookingStatus.CONFIRMED
        );
        if (this.id != null) {
            booking.setId(this.id);
        }
        return booking;
    }
}
