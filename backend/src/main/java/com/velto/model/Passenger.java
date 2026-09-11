package com.velto.model;

import org.springframework.data.annotation.TypeAlias;

/**
 * Concrete User subclass representing a Passenger.
 */
@TypeAlias("passenger")
public class Passenger extends User {

    private String preferredPaymentMethod = "MOCK_CARD";
    private double rating = 5.0;

    public Passenger() {
        super();
        setRole(Role.PASSENGER);
    }

    public Passenger(String name, String email, String password, String phone) {
        super(name, email, password, Role.PASSENGER, phone);
        this.preferredPaymentMethod = "MOCK_CARD";
        this.rating = 5.0;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
