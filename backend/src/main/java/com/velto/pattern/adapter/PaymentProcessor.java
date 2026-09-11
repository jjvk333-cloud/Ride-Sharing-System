package com.velto.pattern.adapter;

/**
 * Target interface for the Adapter Pattern.
 * Velto business logic interacts exclusively with this interface.
 */
public interface PaymentProcessor {

    /**
     * Process payment adapting unified PaymentRequest to the specific gateway protocol.
     */
    PaymentResponse processPayment(PaymentRequest request);

    /**
     * Identifies the supported payment method (e.g. UPI, CARD, MOCK).
     */
    String getPaymentMethod();
}
