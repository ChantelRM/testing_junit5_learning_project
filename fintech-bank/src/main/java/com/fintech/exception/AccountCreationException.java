package com.fintech.exception;

import com.fintech.model.BankAccount;

public class AccountCreationException extends Exception {

    private final BankAccount account;

    public AccountCreationException(BankAccount newAccount) {
        super(String.format(
                "New account Error: Invalid accountID, %s ,already exist",
                newAccount.getAccountNumber()
        ));
        this.account= newAccount;
    }

    public BankAccount getAccount() {
        return this.account;
    }
}
