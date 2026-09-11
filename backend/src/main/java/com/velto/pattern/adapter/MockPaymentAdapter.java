package com.velto.pattern.adapter;

import com.velto.pattern.adapter.thirdparty.MockTransaction;
import com.velto.pattern.adapter.thirdparty.ThirdPartyMockGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that converts Velto's unified PaymentRequest to ThirdPartyMockGateway's settleDirect call.
 */
@Component
public class MockPaymentAdapter implements PaymentProcessor {

    private final ThirdPartyMockGateway mockGateway;

    public MockPaymentAdapter(ThirdPartyMockGateway mockGateway) {
        this.mockGateway = mockGateway;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        MockTransaction txn = mockGateway.settleDirect(request.getBookingId(), request.getAmount());
        String internalTxnId = "TXN-MOCK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        return new PaymentResponse(
                true,
                internalTxnId,
                "Payment settled successfully via direct counter/mock",
                txn.getReceiptNumber()
        );
    }

    @Override
    public String getPaymentMethod() {
        return "MOCK";
    }
}
