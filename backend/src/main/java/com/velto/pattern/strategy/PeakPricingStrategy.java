package com.velto.pattern.strategy;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Peak surge pricing applying a 1.5x multiplier during high demand.
 */
@Component
public class PeakPricingStrategy implements PricingStrategy {

    private static final double SURGE_MULTIPLIER = 1.5;

    @Override
    public String getStrategyName() {
        return "Peak Surge Pricing (1.5x)";
    }

    @Override
    public PricingType getPricingType() {
        return PricingType.PEAK;
    }

    @Override
    public double calculatePrice(double basePrice, int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats must be at least 1");
        }
        return Math.round((basePrice * SURGE_MULTIPLIER * seats) * 100.0) / 100.0;
    }
}
