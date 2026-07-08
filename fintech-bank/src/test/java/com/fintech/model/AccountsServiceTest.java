package com.fintech.model;

import com.fintech.exception.AccountCreationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.ArrayList;
import java.util.List;

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
    void findBankReturnsBankAccount(){
        //act: find account
        BankAccount bankacc= service.findBankAccount("acc-001");
        //assert: return the same bank account ibject that was created in setUp
        assertSame(acc1,bankacc);
    }

    @Test
    void findBankAccountByIdReturnSavingsAccount(){
        BankAccount result = service.findBankAccount("acc-003");
        assertNotNull(result);
        assertInstanceOf(SavingsAccount.class, result);
    }

    @Test
    void findNonexistentBankReturnNull(){
        assertNull(service.findBankAccount("applebanana-001"));
    }

    @Test
    void getSavingsReturnsSavingAccounts(){
        //act
        List<SavingsAccount> sa2= service.getSavingsAccounts();

        //assert
        assertInstanceOf(SavingsAccount.class,sa2.get(0));
        assertSame(acc3,sa2.get(0));
    }

    @Test
    void getSavingsReturnEmptyList(){
        //act : remove saving acc
        service.removeAccount("acc-003");

        assertEquals(new ArrayList<>(),service.getSavingsAccounts());
    }

    @Test
    void removeAccountRemoveAndDeceasesSize(){
        //act: remove one bank account
        service.removeAccount("acc-001");

        //assert
        assertEquals(2,service.getAccounts().size());
    }

    @Test
    void removeAccountMakeAccountUnfinable(){
        //act
        service.removeAccount("acc-002");
        //assert
        assertNull(service.findBankAccount("acc=002"));

    }

    @Test
    void accountExistsReturnsTrueIfFound(){
        assertTrue(service.accountExists("acc-001"));
    }

    @Test
    void accountExistReturnsFalseIfNotFound(){
        assertFalse(service.accountExists("appleBanana-001"));
    }

    @Test
    void getAccountsReturnsSavings(){
        assertTrue(service.getAccounts().containsValue(acc3));
    }

    @Test
    void getAllTransactionsByType_shouldReturnDepositAcrossAllAccounts(){

    }

    @Test
    void  getAllTransactionsByType_whenNoAccountsHaveThatType_shouldReturnEmptyList(){

    }

    @Test
    void  getAllTransactionsByMonth_shouldReturnTransactionsFromAllAccountsForThatMonth(){

    }

    @Test
    void  getAllTransactionsByMonth_whenNoActivity_shouldReturnEmptyList(){

    }
}
