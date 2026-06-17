package com.fintech.exception;

/**
 * Thrown when a withdrawal or transfer exceeds the available balance.
 */
public class InsufficientFundsException extends Exception {

    private final double amountRequested;
    private final double availableBalance;

    public InsufficientFundsException(double amountRequested, double availableBalance) {
        super(String.format(
            "Insufficient funds: requested %.2f but available balance is %.2f",
            amountRequested, availableBalance
        ));
        this.amountRequested = amountRequested;
        this.availableBalance = availableBalance;
    }

    public double getAmountRequested() {
        return amountRequested;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}
