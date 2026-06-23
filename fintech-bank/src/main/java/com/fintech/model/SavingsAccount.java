package com.fintech.model;

public class SavingsAccount extends BankAccount{
    private double interestRate;

    public SavingsAccount(String accountNo,String name,double balance,double interestRate){
        super(accountNo,name,balance);
        this.interestRate = interestRate;
    }

    public void applyInterest(double rate){
        double depositInterest = getBalance() * interestRate;

        deposit(depositInterest,"Savings earned from savings")
    }
}
