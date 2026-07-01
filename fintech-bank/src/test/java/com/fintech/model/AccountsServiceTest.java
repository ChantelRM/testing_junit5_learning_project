package com.fintech.model;

import com.fintech.exception.AccountCreationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;

public class AccountsServiceTest {
    private BankAccount acc1, acc2;
    private SavingsAccount acc3;
    private AccountsService service;

    @BeforeEach
    void setUp() throws AccountCreationException {
        acc1 = new BankAccount("acc-001",
                "owner1",550.000);
        acc2 = new BankAccount("acc-002",
                "owner2",0.10);
        acc3 = new SavingsAccount("acc-003",
                "owwner3",10356.00,0.5);
        service = new AccountsService();

        service.addAccount(acc1);
        service.addAccount(acc2);
        service.addAccount(acc3);
    }

    @Test
    void addExistingAccountThrowsException(){
        //add a new saving accpunt with same id as acc2
        SavingsAccount dupeAcc = new SavingsAccount(
                "acc-003","owner4",90.00,0.01);

        //assert account creation exception is thrown
        assertThrows(AccountCreationException.class, ()-> service.addAccount(dupeAcc));
        //assert size doesnt grow
        assertEquals(3,service.getAccounts().size());
    }

    @Test
    void addAccountIncreasesSize() throws AccountCreationException {
        //assert the map is empty first
        assertEquals(3,service.getAccounts().size());
        // act: add a new acount to accounts
        SavingsAccount sa1 = new SavingsAccount(
                "sa-004","owen",60.00,0.02);
        service.addAccount(sa1);
        //assert: the map size is 4,account map structure changes
        assertEquals(4,service.getAccounts().size());
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
