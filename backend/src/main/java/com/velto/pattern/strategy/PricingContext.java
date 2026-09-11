package com.velto.pattern.strategy;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Context class in the GoF Strategy Pattern.
 * Maintains a reference to one of the concrete PricingStrategy objects
 * and allows dynamic strategy switching at runtime.
 */
@Component
public class PricingContext {

    private PricingStrategy currentStrategy;
    private final Map<PricingType, PricingStrategy> strategyRegistry = new EnumMap<>(PricingType.class);

    public PricingContext(List<PricingStrategy> strategies) {
        for (PricingStrategy s : strategies) {
            strategyRegistry.put(s.getPricingType(), s);
        }
        // Default strategy is Standard
        this.currentStrategy = strategyRegistry.getOrDefault(PricingType.STANDARD, new StandardPricingStrategy());
    }

    /**
     * Dynamically replace the active pricing strategy.
     */
    public void setStrategy(PricingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Pricing strategy cannot be null");
        }
        this.currentStrategy = strategy;
    }

    /**
     * Switch strategy using PricingType enum.
     */
    public void setStrategyByType(PricingType type) {
        PricingStrategy found = strategyRegistry.get(type);
        if (found == null) {
            throw new IllegalArgumentException("No strategy registered for type: " + type);
        }
        this.currentStrategy = found;
    }

    public PricingStrategy getStrategy() {
        return currentStrategy;
    }

    /**
     * Execute strategy calculation.
     */
    public double calculatePrice(double basePrice, int seats) {
        return currentStrategy.calculatePrice(basePrice, seats);
    }
}
