package com.velto.pattern.adapter.thirdparty;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Simulated external 3rd-party Card gateway (e.g. Stripe / Razorpay Card Engine).
 * Incompatible interface: expects 16-digit card number, MM/YY expiry, CVV, and total amount.
 */
@Component
public class ThirdPartyCardGateway {

    public CardAuthResult executeCardCharge(String card16Digits, String expiryDate, String cvv, double totalAmount) {
        if (card16Digits == null || card16Digits.replaceAll("\\s+", "").length() < 16) {
            return new CardAuthResult(null, false, "Invalid card number: must be 16 digits");
        }
        if (cvv == null || cvv.length() < 3) {
            return new CardAuthResult(null, false, "Invalid CVV security code");
        }
        if (totalAmount <= 0) {
            return new CardAuthResult(null, false, "Invalid charge amount");
        }
        // Decline test card ending in 0000
        if (card16Digits.endsWith("0000")) {
            return new CardAuthResult(null, false, "Card declined by issuing bank (Insufficient funds)");
        }

        String authCode = "AUTH-STRIPE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new CardAuthResult(authCode, true, null);
    }
}
