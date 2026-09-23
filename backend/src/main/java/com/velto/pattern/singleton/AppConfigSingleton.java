package com.velto.pattern.singleton;

import java.io.Serializable;

/**
 * GoF Singleton Pattern Implementation with Double-Checked Locking (DCL).
 * 
 * Provides thread-safe, centralized, global access to runtime application configurations
 * such as pricing multipliers, fees, maintenance toggles, and limits.
 * 
 * Robustness features:
 * - Thread-safe lazy initialization with 'volatile' and synchronized block.
 * - Reflection attack protection in constructor.
 * - Clone prevention by overriding clone().
 * - Serialization protection via readResolve().
 */
public class AppConfigSingleton implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // Volatile prevents instruction reordering and ensures visibility across threads
    private static volatile AppConfigSingleton instance;

    // Configuration properties
    private String appName = "Velto Ride Sharing System";
    private String appVersion = "1.0.0";
    private double baseFare = 50.0;
    private double perKmRate = 12.0;
    private double surgeMultiplier = 1.5;
    private double sharedDiscountMultiplier = 0.8;
    private double platformFeePercentage = 5.0;
    private String currency = "INR";
    private boolean maintenanceMode = false;
    private int maxSeatsPerBooking = 4;
    private String googleMapsApiKey = "AIzaSyDhwLHzpMwXxaNkGfgWnjScOeVvMn6LJNs";

    /**
     * Private constructor to prevent direct instantiation.
     * Defends against Java Reflection attacks.
     */
    private AppConfigSingleton() {
        if (instance != null) {
            throw new IllegalStateException("AppConfigSingleton already initialized! Please use getInstance().");
        }
    }

    /**
     * Double-Checked Locking (DCL) for thread-safe lazy initialization.
     */
    public static AppConfigSingleton getInstance() {
        if (instance == null) {
            synchronized (AppConfigSingleton.class) {
                if (instance == null) {
                    instance = new AppConfigSingleton();
                }
            }
        }
        return instance;
    }

    /**
     * Prevent cloning.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of AppConfigSingleton is strictly prohibited.");
    }

    /**
     * Preserve singleton identity during deserialization.
     */
    protected Object readResolve() {
        return getInstance();
    }

    /**
     * Thread-safe atomic update of runtime configuration properties.
     */
    public synchronized void updateConfig(Double baseFare, Double perKmRate, Double surgeMultiplier,
                                          Double sharedDiscountMultiplier, Double platformFeePercentage,
                                          String currency, Boolean maintenanceMode, Integer maxSeatsPerBooking) {
        if (baseFare != null && baseFare >= 0) this.baseFare = baseFare;
        if (perKmRate != null && perKmRate >= 0) this.perKmRate = perKmRate;
        if (surgeMultiplier != null && surgeMultiplier > 0) this.surgeMultiplier = surgeMultiplier;
        if (sharedDiscountMultiplier != null && sharedDiscountMultiplier > 0) this.sharedDiscountMultiplier = sharedDiscountMultiplier;
        if (platformFeePercentage != null && platformFeePercentage >= 0) this.platformFeePercentage = platformFeePercentage;
        if (currency != null && !currency.isBlank()) this.currency = currency.trim().toUpperCase();
        if (maintenanceMode != null) this.maintenanceMode = maintenanceMode;
        if (maxSeatsPerBooking != null && maxSeatsPerBooking > 0) this.maxSeatsPerBooking = maxSeatsPerBooking;
    }

    /**
     * Reset to default values (useful for unit tests).
     */
    public synchronized void resetToDefaults() {
        this.appName = "Velto Ride Sharing System";
        this.appVersion = "1.0.0";
        this.baseFare = 50.0;
        this.perKmRate = 12.0;
        this.surgeMultiplier = 1.5;
        this.sharedDiscountMultiplier = 0.8;
        this.platformFeePercentage = 5.0;
        this.currency = "INR";
        this.maintenanceMode = false;
        this.maxSeatsPerBooking = 4;
        this.googleMapsApiKey = "AIzaSyDhwLHzpMwXxaNkGfgWnjScOeVvMn6LJNs";
    }

    // Getters and Setters
    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public synchronized double getBaseFare() {
        return baseFare;
    }

    public synchronized void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public synchronized double getPerKmRate() {
        return perKmRate;
    }

    public synchronized void setPerKmRate(double perKmRate) {
        this.perKmRate = perKmRate;
    }

    public synchronized double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public synchronized void setSurgeMultiplier(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public synchronized double getSharedDiscountMultiplier() {
        return sharedDiscountMultiplier;
    }

    public synchronized void setSharedDiscountMultiplier(double sharedDiscountMultiplier) {
        this.sharedDiscountMultiplier = sharedDiscountMultiplier;
    }

    public synchronized double getPlatformFeePercentage() {
        return platformFeePercentage;
    }

    public synchronized void setPlatformFeePercentage(double platformFeePercentage) {
        this.platformFeePercentage = platformFeePercentage;
    }

    public synchronized String getCurrency() {
        return currency;
    }

    public synchronized void setCurrency(String currency) {
        this.currency = currency;
    }

    public synchronized boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public synchronized void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public synchronized int getMaxSeatsPerBooking() {
        return maxSeatsPerBooking;
    }

    public synchronized void setMaxSeatsPerBooking(int maxSeatsPerBooking) {
        this.maxSeatsPerBooking = maxSeatsPerBooking;
    }

    public synchronized String getGoogleMapsApiKey() {
        return googleMapsApiKey;
    }

    public synchronized void setGoogleMapsApiKey(String googleMapsApiKey) {
        if (googleMapsApiKey != null && !googleMapsApiKey.isBlank()) {
            this.googleMapsApiKey = googleMapsApiKey.trim();
        }
    }
}
