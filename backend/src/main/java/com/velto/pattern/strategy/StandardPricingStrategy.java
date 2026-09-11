package com.velto.pattern.strategy;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Standard flat pricing without surge or discount.
 */
@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public String getStrategyName() {
        return "Standard Regular Pricing (1.0x)";
    }

    @Override
    public PricingType getPricingType() {
        return PricingType.STANDARD;
    }

    @Override
    public double calculatePrice(double basePrice, int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats must be at least 1");
        }
        return Math.round((basePrice * seats) * 100.0) / 100.0;
    }
}
