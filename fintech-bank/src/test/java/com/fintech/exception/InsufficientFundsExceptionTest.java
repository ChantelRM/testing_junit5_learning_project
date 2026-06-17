package com.fintech.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InsufficientFundsException.
 *
 * Even exceptions deserve their own tests.
 * This ensures error messages and fields are correct
 * so they're useful when things go wrong in production.
 */
class InsufficientFundsExceptionTest {

    @Test
    @DisplayName("Exception should store the requested amount")
    void exception_shouldStoreRequestedAmount() {
        InsufficientFundsException ex = new InsufficientFundsException(500.00, 100.00);
        assertEquals(500.00, ex.getAmountRequested());
    }

    @Test
    @DisplayName("Exception should store the available balance")
    void exception_shouldStoreAvailableBalance() {
        InsufficientFundsException ex = new InsufficientFundsException(500.00, 100.00);
        assertEquals(100.00, ex.getAvailableBalance());
    }

    @Test
    @DisplayName("Exception message should mention both amounts")
    void exception_messageShouldContainAmounts() {
        InsufficientFundsException ex = new InsufficientFundsException(500.00, 100.00);
        String message = ex.getMessage();

        assertTrue(message.contains("500.00"), "Message should mention requested amount");
        assertTrue(message.contains("100.00"), "Message should mention available balance");
    }
}
