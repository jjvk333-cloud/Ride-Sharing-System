package com.velto.pattern.adapter.thirdparty;

/**
 * 3rd-party SDK response representation for Card processing (e.g. Stripe / Mastercard).
 * Incompatible with Velto's internal PaymentResponse.
 */
public class CardAuthResult {
    private final String authCode;
    private final boolean authorized;
    private final String declineReason;

    public CardAuthResult(String authCode, boolean authorized, String declineReason) {
        this.authCode = authCode;
        this.authorized = authorized;
        this.declineReason = declineReason;
    }

    public String getAuthCode() {
        return authCode;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getDeclineReason() {
        return declineReason;
    }
}
