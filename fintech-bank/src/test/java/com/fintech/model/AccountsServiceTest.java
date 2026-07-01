package com.fintech.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;

public class AccountsServiceTest {
    private BankAccount acc1, acc2;
    private SavingsAccount acc3;

    @BeforeEach
    void setUp(){
        acc1 = new BankAccount("acc-001","owner1",550.000);
        acc2 = new BankAccount("acc-002","owner2",0.10);
        acc3 = new SavingsAccount("acc-003","owwner3",10356.00,0.5);
    }

    @Test
    void addExistingAccountThrowsException(){
        //add the accounts

        //add a new saving accpunt with same id as acc2

        //assert account creation exception is thrown

        //assert size doesnt grow
    }

    @Test
    void addAccountIncreasesSize(){
        //assert the map is empty first

        // act: add the three accounts

        //assert: the map size is 3
    }

    @Test
    void findBankReturnsBankAccount(){}

    @Test
    void findNonexistentBankReturnNull(){}

    @Test
    void getAccountsReturnChildTypes(){}

    @Test
    void getSavingsRetunsOnlySavingChildTypes(){}

    @Test
    void removeAccountRemoveAndsDEceasesSize(){}

    @Test
    void accountExistsReturnsTrueIfFound(){}

    @Test
    void accountExistReturnsFalseIfNotFound(){}


}
