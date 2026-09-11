package com.velto.pattern.strategy;

import com.velto.pattern.singleton.AppConfigSingleton;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Peak surge pricing dynamically fetching the multiplier from AppConfigSingleton.
 */
@Component
public class PeakPricingStrategy implements PricingStrategy {

    @Override
    public String getStrategyName() {
        double currentMultiplier = AppConfigSingleton.getInstance().getSurgeMultiplier();
        return "Peak Surge Pricing (" + currentMultiplier + "x)";
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
        double surgeMultiplier = AppConfigSingleton.getInstance().getSurgeMultiplier();
        return Math.round((basePrice * surgeMultiplier * seats) * 100.0) / 100.0;
    }
}
