package com.fintech.model;

import com.fintech.exception.InsufficientFundsException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ============================================================
 *  JUNIT 5 LEARNING FILE — BankAccountTest
 * ============================================================
 *
 *  Each section introduces a new JUnit concept.
 *  Read the comments before each test to understand WHY it's
 *  written that way, not just what it does.
 *
 *  CONCEPTS COVERED:
 *  - @Test, @BeforeEach, @AfterEach
 *  - assertEquals, assertTrue, assertThrows
 *  - Testing happy paths vs unhappy paths
 *  - Parameterized tests with @ValueSource
 *  - @Nested classes for organisation
 *  - @DisplayName for readable test output
 * ============================================================
 */
class BankAccountTest {

    // ----------------------------------------------------------------
    // CONCEPT 1: @BeforeEach
    //
    // Runs before EVERY test method. Use it to set up fresh state
    // so tests don't affect each other. This is called "test isolation".
    //
    // Think of it like re-setting a board game before each round.
    // ----------------------------------------------------------------
    private BankAccount account;
    private BankAccount otherAccount;

    @BeforeEach
    void setUp() {
        account = new BankAccount("ACC-001", "Thabo Nkosi", 1000.00);
        otherAccount = new BankAccount("ACC-002", "Lerato Dlamini", 500.00);
    }

    // ----------------------------------------------------------------
    // CONCEPT 2: @AfterEach (optional, for cleanup)
    //
    // Runs after every test. Useful for closing files or connections.
    // Not needed here, but good to know it exists.
    // ----------------------------------------------------------------
    @AfterEach
    void tearDown() {
        // Nothing to clean up here — just showing the annotation exists.
    }


    // ================================================================
    //  SECTION 1: Constructor & initial state
    // ================================================================

    // CONCEPT 3: @Test and assertEquals
    //
    // @Test marks a method as a test case.
    // assertEquals(expected, actual) — order matters: expected first!
    // ----------------------------------------------------------------
    @Test
    @DisplayName("New account should have correct initial balance")
    void newAccount_shouldHaveCorrectInitialBalance() {
        assertEquals(1000.00, account.getBalance());
    }

    @Test
    @DisplayName("New account should store owner name and account number")
    void newAccount_shouldStoreOwnerDetails() {
        assertEquals("Thabo Nkosi", account.getOwnerName());
        assertEquals("ACC-001", account.getAccountNumber());
    }

    // CONCEPT 4: assertThrows — testing the UNHAPPY path
    //
    // Don't only test what should work. Test what should FAIL.
    // assertThrows checks that the right exception is thrown.
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Creating account with negative balance should throw IllegalArgumentException")
    void newAccount_withNegativeBalance_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            new BankAccount("ACC-ERR", "Bad User", -100.00)
        );
    }


    // ================================================================
    //  SECTION 2: Deposit tests
    //  Grouped with @Nested to keep things organised.
    // ================================================================

    // CONCEPT 5: @Nested — group related tests together
    //
    // Makes test output readable and keeps related tests close.
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Deposit tests")
    class DepositTests {

        @Test
        @DisplayName("Valid deposit should increase balance")
        void deposit_validAmount_shouldIncreaseBalance() {
            account.deposit(500.00, "Salary");
            assertEquals(1500.00, account.getBalance());
        }

        @Test
        @DisplayName("Deposit should add a transaction to history")
        void deposit_shouldAddToTransactionHistory() {
            account.deposit(200.00, "Freelance payment");
            assertEquals(1, account.getTransactionHistory().size());
        }

        // CONCEPT 6: Parameterized tests with @ValueSource
        //
        // Instead of writing 3 separate tests for invalid amounts,
        // run the SAME test with multiple inputs automatically.
        // Here: 0, -1, and -999 should all throw an exception.
        // ----------------------------------------------------------------
        @ParameterizedTest(name = "Deposit of {0} should be rejected")
        @ValueSource(doubles = {0, -1, -999.99})
        @DisplayName("Zero or negative deposit should throw IllegalArgumentException")
        void deposit_invalidAmount_shouldThrowException(double invalidAmount) {
            assertThrows(IllegalArgumentException.class, () ->
                account.deposit(invalidAmount, "Bad deposit")
            );
        }
    }


    // ================================================================
    //  SECTION 3: Withdrawal tests
    // ================================================================
    @Nested
    @DisplayName("Withdrawal tests")
    class WithdrawalTests {

        @Test
        @DisplayName("Valid withdrawal should decrease balance")
        void withdraw_validAmount_shouldDecreaseBalance() throws InsufficientFundsException {
            account.withdraw(300.00, "Groceries");
            assertEquals(700.00, account.getBalance());
        }

        @Test
        @DisplayName("Withdrawal should add a transaction to history")
        void withdraw_shouldAddToTransactionHistory() throws InsufficientFundsException {
            account.withdraw(100.00, "ATM");
            assertEquals(1, account.getTransactionHistory().size());
        }

        // CONCEPT 7: Catching and inspecting the exception
        //
        // assertThrows returns the exception so you can check its details.
        // Here we verify the message/fields, not just the type.
        // ----------------------------------------------------------------
        @Test
        @DisplayName("Withdrawing more than balance should throw InsufficientFundsException")
        void withdraw_moreThanBalance_shouldThrowInsufficientFunds() {
            InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(9999.00, "Too much")
            );

            // Check the exception carries the right values
            assertEquals(9999.00, ex.getAmountRequested());
            assertEquals(1000.00, ex.getAvailableBalance());
        }

        @Test
        @DisplayName("Withdrawing exact balance should succeed and leave zero balance")
        void withdraw_exactBalance_shouldLeaveZero() throws InsufficientFundsException {
            account.withdraw(1000.00, "Full withdrawal");
            assertEquals(0.00, account.getBalance());
        }

        @ParameterizedTest(name = "Withdrawal of {0} should be rejected")
        @ValueSource(doubles = {0, -50, -0.01})
        @DisplayName("Zero or negative withdrawal should throw IllegalArgumentException")
        void withdraw_invalidAmount_shouldThrowException(double invalidAmount) {
            assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(invalidAmount, "Bad withdrawal")
            );
        }
    }


    // ================================================================
    //  SECTION 4: Transfer tests
    // ================================================================
    @Nested
    @DisplayName("Transfer tests")
    class TransferTests {

        @Test
        @DisplayName("Transfer should debit source and credit target")
        void transfer_shouldUpdateBothBalances() throws InsufficientFundsException {
            account.transfer(otherAccount, 200.00, "Rent split");

            assertEquals(800.00, account.getBalance());
            assertEquals(700.00, otherAccount.getBalance());
        }

        @Test
        @DisplayName("Transfer should add TRANSFER_OUT to source history")
        void transfer_shouldAddTransferOutToSource() throws InsufficientFundsException {
            account.transfer(otherAccount, 100.00, "Payment");

            Transaction last = account.getTransactionHistory().get(0);
            assertEquals(Transaction.Type.TRANSFER_OUT, last.getType());
        }

        @Test
        @DisplayName("Transfer should add TRANSFER_IN to target history")
        void transfer_shouldAddTransferInToTarget() throws InsufficientFundsException {
            account.transfer(otherAccount, 100.00, "Payment");

            Transaction last = otherAccount.getTransactionHistory().get(0);
            assertEquals(Transaction.Type.TRANSFER_IN, last.getType());
        }

        @Test
        @DisplayName("Transfer exceeding balance should throw InsufficientFundsException")
        void transfer_insufficientFunds_shouldThrowException() {
            assertThrows(InsufficientFundsException.class, () ->
                account.transfer(otherAccount, 5000.00, "Too much")
            );
        }

        // CONCEPT 8: Testing that state DOESN'T change on failure
        //
        // If a transfer fails, neither account should be modified.
        // This tests the "atomicity" — all or nothing.
        // ----------------------------------------------------------------
        @Test
        @DisplayName("Failed transfer should not change either account balance")
        void transfer_onFailure_shouldNotChangeAnyBalance() {
            try {
                account.transfer(otherAccount, 5000.00, "Should fail");
            } catch (InsufficientFundsException ignored) {}

            // Balances should be unchanged
            assertEquals(1000.00, account.getBalance());
            assertEquals(500.00, otherAccount.getBalance());
        }
    }


    // ================================================================
    //  SECTION 5: Transaction history tests
    // ================================================================
    @Nested
    @DisplayName("Transaction history tests")
    class TransactionHistoryTests {

        @Test
        @DisplayName("New account should have empty transaction history")
        void newAccount_shouldHaveEmptyHistory() {
            assertTrue(account.getTransactionHistory().isEmpty());
        }

        @Test
        @DisplayName("Multiple operations should all appear in history")
        void multipleOperations_shouldAllAppearInHistory() throws InsufficientFundsException {
            account.deposit(100.00, "D1");
            account.deposit(200.00, "D2");
            account.withdraw(50.00, "W1");

            assertEquals(3, account.getTransactionHistory().size());
        }

        // CONCEPT 9: assertThrows for immutability
        //
        // getTransactionHistory() returns an unmodifiable list.
        // Trying to modify it should throw UnsupportedOperationException.
        // ----------------------------------------------------------------
        @Test
        @DisplayName("Transaction history should be unmodifiable from outside")
        void transactionHistory_shouldBeUnmodifiable() {
            assertThrows(UnsupportedOperationException.class, () ->
                account.getTransactionHistory().clear()
            );
        }
    }
}
