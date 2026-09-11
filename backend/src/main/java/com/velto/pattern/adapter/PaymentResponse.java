package com.velto.pattern.adapter;

import java.time.LocalDateTime;

/**
 * Unified Payment Response returned by all Payment Adapters.
 */
public class PaymentResponse {

    private final boolean success;
    private final String transactionId;
    private final String message;
    private final String gatewayReference;
    private final LocalDateTime processedAt;

    public PaymentResponse(boolean success, String transactionId, String message, String gatewayReference) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.gatewayReference = gatewayReference;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
