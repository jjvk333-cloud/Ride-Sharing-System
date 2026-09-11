package com.velto.pattern.adapter.thirdparty;

/**
 * 3rd-party legacy or mock transaction receipt.
 */
public class MockTransaction {
    private final String receiptNumber;
    private final String settlementStatus;

    public MockTransaction(String receiptNumber, String settlementStatus) {
        this.receiptNumber = receiptNumber;
        this.settlementStatus = settlementStatus;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }
}
