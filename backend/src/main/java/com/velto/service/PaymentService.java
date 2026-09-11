package com.velto.service;

import com.velto.model.Payment;
import com.velto.pattern.adapter.PaymentRequest;

import java.util.List;

public interface PaymentService {
    Payment processPayment(PaymentRequest request);
    Payment getPaymentById(String id);
    List<Payment> getPaymentsByBookingId(String bookingId);
    List<Payment> getPaymentsByPassengerId(String passengerId);
}
