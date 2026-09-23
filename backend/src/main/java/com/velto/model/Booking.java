package com.velto.model;

import com.velto.pattern.strategy.PricingType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;
    private String rideId;
    private String passengerId;
    private String passengerName;
    private int seats;
    private double amount;
    private double distance;
    private PricingType pricingType;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;
    private LocalDateTime createdAt;

    public Booking() {
        this.createdAt = LocalDateTime.now();
        this.bookingStatus = BookingStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Booking(String rideId, String passengerId, String passengerName, int seats, double amount, PricingType pricingType, PaymentStatus paymentStatus, BookingStatus bookingStatus) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.seats = seats;
        this.amount = amount;
        this.distance = 0.0;
        this.pricingType = pricingType != null ? pricingType : PricingType.STANDARD;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PAID;
        this.bookingStatus = bookingStatus != null ? bookingStatus : BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public Booking(String rideId, String passengerId, String passengerName, int seats, double amount, double distance, PricingType pricingType, PaymentStatus paymentStatus, BookingStatus bookingStatus) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.seats = seats;
        this.amount = amount;
        this.distance = distance;
        this.pricingType = pricingType != null ? pricingType : PricingType.STANDARD;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PAID;
        this.bookingStatus = bookingStatus != null ? bookingStatus : BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
