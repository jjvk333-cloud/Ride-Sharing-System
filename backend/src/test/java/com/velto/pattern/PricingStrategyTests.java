package com.velto.pattern;

import com.velto.pattern.strategy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PricingStrategyTests {

    @Autowired
    private PricingContext pricingContext;

    @Autowired
    private StandardPricingStrategy standardStrategy;

    @Autowired
    private PeakPricingStrategy peakStrategy;

    @Autowired
    private SharedRidePricingStrategy sharedStrategy;

    @Test
    @DisplayName("StandardPricingStrategy calculates exact 1.0x price")
    void testStandardPricing() {
        double base = 200.0;
        int seats = 2;
        double price = standardStrategy.calculatePrice(base, seats);
        assertEquals(400.0, price);
        assertEquals(PricingType.STANDARD, standardStrategy.getPricingType());
    }

    @Test
    @DisplayName("PeakPricingStrategy calculates 1.5x surge price")
    void testPeakPricing() {
        double base = 200.0;
        int seats = 2;
        double price = peakStrategy.calculatePrice(base, seats);
        // (200 * 1.5) * 2 = 600.0
        assertEquals(600.0, price);
        assertEquals(PricingType.PEAK, peakStrategy.getPricingType());
    }

    @Test
    @DisplayName("SharedRidePricingStrategy calculates 0.8x discounted price")
    void testSharedPricing() {
        double base = 200.0;
        int seats = 2;
        double price = sharedStrategy.calculatePrice(base, seats);
        // (200 * 0.8) * 2 = 320.0
        assertEquals(320.0, price);
        assertEquals(PricingType.SHARED, sharedStrategy.getPricingType());
    }

    @Test
    @DisplayName("PricingContext dynamically switches strategies at runtime")
    void testDynamicStrategySwitching() {
        double basePrice = 100.0;
        int seats = 1;

        // Switch to STANDARD
        pricingContext.setStrategyByType(PricingType.STANDARD);
        assertEquals(100.0, pricingContext.calculatePrice(basePrice, seats));

        // Switch to PEAK
        pricingContext.setStrategyByType(PricingType.PEAK);
        assertEquals(150.0, pricingContext.calculatePrice(basePrice, seats));

        // Switch to SHARED
        pricingContext.setStrategyByType(PricingType.SHARED);
        assertEquals(80.0, pricingContext.calculatePrice(basePrice, seats));
    }
}
