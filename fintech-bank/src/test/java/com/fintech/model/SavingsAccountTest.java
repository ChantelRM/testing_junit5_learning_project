package com.fintech.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;


public class SavingsAccountTest {
    @ParameterizedTest
    @CsvSource({
            "1000.00, 0.05, 1050.00",
    "1000.00, 0.10, 1100",
    "0.00, 0.05, 0.00"})
    void applyValidInterest(double initialBalance, double rate, double expectedBalance){
        SavingsAccount acc = new SavingsAccount("acc-011","Tester",initialBalance,rate);
        acc.applyInterest();

        assertEquals(expectedBalance, acc.getBalance());
    }

    @Test
    void invalidAccountRaisesException(){
        assertThrows(IllegalArgumentException.class, () -> new SavingsAccount("testAcc-0098","Tester",650.06,-2));
    }

    @Test
    void invalidInterestRaisesException(){

        assertThrows(IllegalArgumentException.class, () -> new SavingsAccount("testAcc-0098","Tester",650.06,-2).applyInterest());
    }

    @Test
    void savingInheritsFromBank(){
        SavingsAccount acc = new SavingsAccount("acc582tep","Tester",707559747.00,0.011);
        assertInstanceOf(BankAccount.class,acc, "SavingsAccount must Inherit from BankAccount");
    }
}
