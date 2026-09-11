package com.velto.pattern.adapter;

import com.velto.pattern.adapter.thirdparty.ThirdPartyUpiGateway;
import com.velto.pattern.adapter.thirdparty.UpiTransactionResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that converts Velto's unified PaymentRequest to ThirdPartyUpiGateway's payViaVpa call,
 * and translates UpiTransactionResult back to unified PaymentResponse.
 */
@Component
public class UpiPaymentAdapter implements PaymentProcessor {

    private final ThirdPartyUpiGateway upiGateway;

    public UpiPaymentAdapter(ThirdPartyUpiGateway upiGateway) {
        this.upiGateway = upiGateway;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        String vpa = request.getUpiId() != null ? request.getUpiId() : "default@veltopay";
        UpiTransactionResult result = upiGateway.payViaVpa(vpa, request.getAmount());

        boolean isSuccess = result.getResponseCode() == 200;
        String internalTxnId = "TXN-UPI-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        return new PaymentResponse(
                isSuccess,
                internalTxnId,
                result.getStatusMessage(),
                result.getUpiRefId()
        );
    }

    @Override
    public String getPaymentMethod() {
        return "UPI";
    }
}
