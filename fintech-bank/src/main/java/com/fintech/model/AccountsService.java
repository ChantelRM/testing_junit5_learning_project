package com.fintech.model;

import com.fintech.exception.AccountCreationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AccountsService {
    private Map<String, BankAccount> accounts;

    public AccountsService(){
        accounts = new HashMap<>();
    }

    public void addAccount(BankAccount newAccount) throws AccountCreationException {
        if(accounts.containsKey(newAccount.getAccountNumber())){
            throw new AccountCreationException(newAccount);
        }

        accounts.put(newAccount.getAccountNumber(),newAccount);
    }

    public BankAccount findBankAccount(String accountNumber){
        return accounts.getOrDefault(accountNumber,null);
    }

    public Map<String, BankAccount> getAccounts() {
        Map<String,BankAccount> modifiableCopy= new HashMap<>(accounts);
        return modifiableCopy;
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

    public List<Transaction> getAllTransactionsByType(Transaction.Type type) {
        return accounts.values().stream()
                .flatMap(account -> account.getTransactionsByType(type).stream())
                .collect(Collectors.toList());
    }
    
    public List<Transaction> getAllTransactionsByMonth(int year, int month) {
        return accounts.values().stream()
                .flatMap(account -> account.getTransactionsByMonth(year,month).stream())
                .collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactionsBetween(LocalDateTime from, LocalDateTime to) {
        return accounts.values().stream()
                .flatMap(account -> account.getTransactionsBetween(from,to).stream())
                .collect(Collectors.toList());
    }
    
}
