package com.velto.dto;

import com.velto.model.BookingStatus;
import com.velto.model.PaymentStatus;
import com.velto.pattern.strategy.PricingType;

import java.time.LocalDateTime;

public class BookingResponse {

    private String bookingId;
    private String rideId;
    private String passengerId;
    private String passengerName;
    private String pickup;
    private String destination;
    private String date;
    private String time;
    private int seatsBooked;
    private double amountPaid;
    private double distance;
    private PricingType pricingType;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;
    private LocalDateTime createdAt;
    private String message;

    public BookingResponse() {
    }

    public BookingResponse(String bookingId, String rideId, String passengerId, String passengerName, String pickup, String destination, String date, String time, int seatsBooked, double amountPaid, PricingType pricingType, PaymentStatus paymentStatus, BookingStatus bookingStatus, LocalDateTime createdAt, String message) {
        this(bookingId, rideId, passengerId, passengerName, pickup, destination, date, time, seatsBooked, amountPaid, 0.0, pricingType, paymentStatus, bookingStatus, createdAt, message);
    }

    public BookingResponse(String bookingId, String rideId, String passengerId, String passengerName, String pickup, String destination, String date, String time, int seatsBooked, double amountPaid, double distance, PricingType pricingType, PaymentStatus paymentStatus, BookingStatus bookingStatus, LocalDateTime createdAt, String message) {
        this.bookingId = bookingId;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.pickup = pickup;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.seatsBooked = seatsBooked;
        this.amountPaid = amountPaid;
        this.distance = distance;
        this.pricingType = pricingType;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
        this.createdAt = createdAt;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // JSON alias getters for robust frontend and test compatibility
    public String getId() {
        return bookingId;
    }

    public double getAmount() {
        return amountPaid;
    }

    public int getSeats() {
        return seatsBooked;
    }
}
