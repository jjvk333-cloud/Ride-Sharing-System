package com.velto.pattern.adapter.thirdparty;

/**
 * 3rd-party SDK response representation for UPI transactions.
 * Incompatible with Velto's internal PaymentResponse.
 */
public class UpiTransactionResult {
    private final String upiRefId;
    private final int responseCode; // 200 = SUCCESS, 400 = INVALID_VPA, 500 = FAILURE
    private final String statusMessage;

    public UpiTransactionResult(String upiRefId, int responseCode, String statusMessage) {
        this.upiRefId = upiRefId;
        this.responseCode = responseCode;
        this.statusMessage = statusMessage;
    }

    public String getUpiRefId() {
        return upiRefId;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
