package com.velto.pattern.strategy;

import com.velto.pattern.singleton.AppConfigSingleton;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Shared ride / carpooling discount dynamically fetching the discount multiplier from AppConfigSingleton.
 */
@Component
public class SharedRidePricingStrategy implements PricingStrategy {

    @Override
    public String getStrategyName() {
        double currentDiscount = AppConfigSingleton.getInstance().getSharedDiscountMultiplier();
        return "Shared Ride Carpooling Discount (" + currentDiscount + "x)";
    }

    @Override
    public PricingType getPricingType() {
        return PricingType.SHARED;
    }

    @Override
    public double calculatePrice(double basePrice, int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats must be at least 1");
        }
        double discountMultiplier = AppConfigSingleton.getInstance().getSharedDiscountMultiplier();
        return Math.round((basePrice * discountMultiplier * seats) * 100.0) / 100.0;
    }
}
