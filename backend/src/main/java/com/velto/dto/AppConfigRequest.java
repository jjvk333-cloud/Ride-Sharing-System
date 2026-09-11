package com.velto.dto;

public class AppConfigRequest {

    private Double baseFare;
    private Double perKmRate;
    private Double surgeMultiplier;
    private Double sharedDiscountMultiplier;
    private Double platformFeePercentage;
    private String currency;
    private Boolean maintenanceMode;
    private Integer maxSeatsPerBooking;

    public AppConfigRequest() {}

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }

    public Double getPerKmRate() { return perKmRate; }
    public void setPerKmRate(Double perKmRate) { this.perKmRate = perKmRate; }

    public Double getSurgeMultiplier() { return surgeMultiplier; }
    public void setSurgeMultiplier(Double surgeMultiplier) { this.surgeMultiplier = surgeMultiplier; }

    public Double getSharedDiscountMultiplier() { return sharedDiscountMultiplier; }
    public void setSharedDiscountMultiplier(Double sharedDiscountMultiplier) { this.sharedDiscountMultiplier = sharedDiscountMultiplier; }

    public Double getPlatformFeePercentage() { return platformFeePercentage; }
    public void setPlatformFeePercentage(Double platformFeePercentage) { this.platformFeePercentage = platformFeePercentage; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(Boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public Integer getMaxSeatsPerBooking() { return maxSeatsPerBooking; }
    public void setMaxSeatsPerBooking(Integer maxSeatsPerBooking) { this.maxSeatsPerBooking = maxSeatsPerBooking; }
}
