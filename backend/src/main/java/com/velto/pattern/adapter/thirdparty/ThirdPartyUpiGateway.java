package com.velto.pattern.adapter.thirdparty;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Simulated external 3rd-party UPI gateway (e.g. PhonePe / GooglePay / NPCI).
 * Incompatible interface: expects VPA address string and amount in double rupees.
 */
@Component
public class ThirdPartyUpiGateway {

    public UpiTransactionResult payViaVpa(String virtualPaymentAddress, double rupees) {
        if (virtualPaymentAddress == null || !virtualPaymentAddress.contains("@")) {
            return new UpiTransactionResult(null, 400, "Invalid Virtual Payment Address (VPA)");
        }
        if (rupees <= 0) {
            return new UpiTransactionResult(null, 400, "Amount must be greater than zero");
        }
        String refId = "UPI-NPCI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new UpiTransactionResult(refId, 200, "UPI payment authorized successfully");
    }
}
