package com.velto.pattern.adapter;

import com.velto.exception.PaymentException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry / Factory that resolves the correct PaymentProcessor Adapter for a given payment method.
 */
@Component
public class PaymentProcessorFactory {

    private final Map<String, PaymentProcessor> processorMap = new HashMap<>();

    public PaymentProcessorFactory(List<PaymentProcessor> processors) {
        for (PaymentProcessor processor : processors) {
            processorMap.put(processor.getPaymentMethod().toUpperCase(), processor);
        }
        // Aliases
        if (processorMap.containsKey("CARD")) {
            processorMap.put("CREDIT_CARD", processorMap.get("CARD"));
            processorMap.put("DEBIT_CARD", processorMap.get("CARD"));
        }
        if (processorMap.containsKey("MOCK")) {
            processorMap.put("CASH", processorMap.get("MOCK"));
        }
    }

    public PaymentProcessor getProcessor(String paymentMethod) {
        if (paymentMethod == null) {
            throw new PaymentException("Payment method cannot be null");
        }
        PaymentProcessor processor = processorMap.get(paymentMethod.trim().toUpperCase());
        if (processor == null) {
            throw new PaymentException("Unsupported payment method: '" + paymentMethod + 
                    "'. Supported methods: " + processorMap.keySet());
        }
        return processor;
    }
}
