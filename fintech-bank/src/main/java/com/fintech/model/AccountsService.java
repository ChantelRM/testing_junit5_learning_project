package com.fintech.model;

import com.fintech.exception.AccountCreationException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountsService {
    private Map<String, BankAccount> accounts;

    public AccountsService(){
        accounts = new HashMap<>();
    }

    public void addAccount(BankAccount newAccount) throws AccountCreationException {
        if(accounts.containsKey(newAccount.getAccountNumber())) throw new AccountCreationException(newAccount);
        accounts.put(newAccount.getAccountNumber(),newAccount);
    }

    public BankAccount findBankAccount(String accountNumber){
        return accounts.getOrDefault(accountNumber,null);
    }

    public Map<String, BankAccount> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    public List<SavingsAccount> getSavingsAccounts(){
        return  accounts.values().stream().filter(account -> account instanceof SavingsAccount)
                .map(account ->(SavingsAccount) account)
                .collect(Collectors.toList());
    }

    public void removeAccount(String accountID){
        accounts.remove(accountID);
    }

    public boolean accountExists(String accountNumber){
        return accounts.containsKey(accountNumber);
    }
}
