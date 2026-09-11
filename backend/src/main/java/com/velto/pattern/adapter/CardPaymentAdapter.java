package com.velto.pattern.adapter;

import com.velto.pattern.adapter.thirdparty.CardAuthResult;
import com.velto.pattern.adapter.thirdparty.ThirdPartyCardGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that converts Velto's unified PaymentRequest to ThirdPartyCardGateway's executeCardCharge call,
 * and translates CardAuthResult back to unified PaymentResponse.
 */
@Component
public class CardPaymentAdapter implements PaymentProcessor {

    private final ThirdPartyCardGateway cardGateway;

    public CardPaymentAdapter(ThirdPartyCardGateway cardGateway) {
        this.cardGateway = cardGateway;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        CardAuthResult result = cardGateway.executeCardCharge(
                request.getCardNumber(),
                request.getExpiryDate(),
                request.getCvv(),
                request.getAmount()
        );

        String internalTxnId = "TXN-CARD-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        String message = result.isAuthorized() ? "Card payment authorized" : result.getDeclineReason();

        return new PaymentResponse(
                result.isAuthorized(),
                internalTxnId,
                message,
                result.getAuthCode()
        );
    }

    @Override
    public String getPaymentMethod() {
        return "CARD";
    }
}
