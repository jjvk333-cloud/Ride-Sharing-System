package com.velto.dto;

import com.velto.pattern.singleton.AppConfigSingleton;

public class AppConfigResponse {

    private String appName;
    private String appVersion;
    private double baseFare;
    private double perKmRate;
    private double surgeMultiplier;
    private double sharedDiscountMultiplier;
    private double platformFeePercentage;
    private String currency;
    private boolean maintenanceMode;
    private int maxSeatsPerBooking;
    private int instanceHashCode;

    public AppConfigResponse() {}

    public static AppConfigResponse fromSingleton(AppConfigSingleton config) {
        AppConfigResponse response = new AppConfigResponse();
        response.appName = config.getAppName();
        response.appVersion = config.getAppVersion();
        response.baseFare = config.getBaseFare();
        response.perKmRate = config.getPerKmRate();
        response.surgeMultiplier = config.getSurgeMultiplier();
        response.sharedDiscountMultiplier = config.getSharedDiscountMultiplier();
        response.platformFeePercentage = config.getPlatformFeePercentage();
        response.currency = config.getCurrency();
        response.maintenanceMode = config.isMaintenanceMode();
        response.maxSeatsPerBooking = config.getMaxSeatsPerBooking();
        response.instanceHashCode = System.identityHashCode(config);
        return response;
    }

    public String getAppName() { return appName; }
    public String getAppVersion() { return appVersion; }
    public double getBaseFare() { return baseFare; }
    public double getPerKmRate() { return perKmRate; }
    public double getSurgeMultiplier() { return surgeMultiplier; }
    public double getSharedDiscountMultiplier() { return sharedDiscountMultiplier; }
    public double getPlatformFeePercentage() { return platformFeePercentage; }
    public String getCurrency() { return currency; }
    public boolean isMaintenanceMode() { return maintenanceMode; }
    public int getMaxSeatsPerBooking() { return maxSeatsPerBooking; }
    public int getInstanceHashCode() { return instanceHashCode; }
}
