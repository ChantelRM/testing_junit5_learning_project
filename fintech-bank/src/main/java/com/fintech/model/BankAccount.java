package com.fintech.model;

import com.fintech.exception.InsufficientFundsException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core bank account model.
 *
 * Supports deposits, withdrawals, transfers, and transaction history.
 * All amounts must be positive. Balance cannot go below zero.
 */
public class BankAccount {

    private final String accountNumber;
    private final String ownerName;
    private double balance;
    private final List<Transaction> transactionHistory;

    public BankAccount(String accountNumber, String ownerName, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Core operations
    // -------------------------------------------------------------------------

    /**
     * Deposits a positive amount into the account.
     *
     * @throws IllegalArgumentException if amount is zero or negative
     */
    public void deposit(double amount, String description) {
        deposit(amount,description,Transaction.Type.DEPOSIT);
    }

    public void deposit(double amount, String description, Transaction.Type type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        transactionHistory.add(new Transaction(type, amount, description));
    }

    /**
     * Withdraws an amount from the account.
     *
     * @throws IllegalArgumentException      if amount is zero or negative
     * @throws InsufficientFundsException    if amount exceeds current balance
     */
    public void withdraw(double amount, String description)
            throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);
        }
        balance -= amount;
        transactionHistory.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, description));
    }

    /**
     * Transfers an amount from this account to a target account.
     *
     * @throws IllegalArgumentException      if amount is zero or negative
     * @throws InsufficientFundsException    if this account has insufficient funds
     */
    public void transfer(BankAccount target, double amount, String description)
            throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        this.withdraw(amount, "Transfer out to " + target.getAccountNumber());
        target.deposit(amount, "Transfer in from " + this.accountNumber);

        // Replace the last transaction entries with more descriptive ones
        transactionHistory.remove(transactionHistory.size() - 1);
        target.transactionHistory.remove(target.transactionHistory.size() - 1);

        transactionHistory.add(new Transaction(
            Transaction.Type.TRANSFER_OUT, amount,
            description + " -> " + target.getAccountNumber()
        ));
        target.transactionHistory.add(new Transaction(
            Transaction.Type.TRANSFER_IN, amount,
            description + " <- " + this.accountNumber
        ));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }

    /** Returns an unmodifiable view of transaction history. */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public List<Transaction> getTransactionsByType(Transaction.Type type){
        List<Transaction> filtered = new ArrayList<>();

        for(Transaction t:getTransactionHistory()){
            if(t.getType()==type){
                filtered.add(t);
            }
        }
        return filtered;
    }

    public List<Transaction> getTransactionsByMonth(int year, int month){
        List<Transaction> filtered = new ArrayList<>();

        for(Transaction t:getTransactionHistory()){
            if(t.getTimestamp().getMonth().getValue()==month && t.getTimestamp().getYear()==year){
                filtered.add(t);
            }
        }
        return filtered;
    }

    /*
        Returns transactions where the timestamp >= from and timestamp < to
    */
    public List<Transaction> getTransactionsBetween(LocalDateTime from, LocalDateTime to){
        List<Transaction> filtered = new ArrayList<>();

        for(Transaction t:getTransactionHistory()){
            if(t.getTimestamp().isAfter(from) && t.getTimestamp().isBefore(to)){
                filtered.add(t);
            }
        }
        return filtered;
    }

    @Override
    public String toString() {
        return String.format("Account[%s | %s | Balance: %.2f]",
            accountNumber, ownerName, balance);
    }
}
