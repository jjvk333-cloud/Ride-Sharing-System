package com.velto.pattern.adapter.thirdparty;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Simulated internal/mock direct settlement gateway (e.g., cash or test bypass).
 */
@Component
public class ThirdPartyMockGateway {

    public MockTransaction settleDirect(String orderRef, double amount) {
        String receipt = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new MockTransaction(receipt, "SETTLED");
    }
}
