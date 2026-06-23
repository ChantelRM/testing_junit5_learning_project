package com.fintech.model;

public class SavingsAccount extends BankAccount{
    private double interestRate;

    public SavingsAccount(String accountNo,String name,double balance,double interestRate){
        super(accountNo,name,balance);
        if(interestRate < 0) throw new IllegalArgumentException();

        this.interestRate = interestRate;
    }

    public void applyInterest(){
        double depositInterest = getBalance() * interestRate;
        if(depositInterest > 0) {
        deposit(depositInterest, "Interest earned from savings",Transaction.Type.INTEREST);
    }   }

    public double getInterestRate(){return interestRate;}

}
