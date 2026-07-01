package com.fintech.exception;

import com.fintech.model.AccountsService;
import com.fintech.model.BankAccount;
import com.fintech.model.SavingsAccount;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AccountsCreationExceptionTest {
    /**
     * Tests fir AccountsCreationException
     *
     * Ensure that error messages and fields ae correct
     * so they're useful when things go wrong in production
     */
    private BankAccount bank;
    private SavingsAccount savings, savings2;
    private AccountsService service;

    @BeforeEach
    public void setUp(){
        bank= new BankAccount(
                "bank001","Tester1",50.00);
        savings = new SavingsAccount(
                "svngs001","Tester2",600.00,0.03);
        savings2= new SavingsAccount(
                "svngs001","Tester2",600.00,0.03);
    }

    @Test
    @DisplayName("Exception should store the account number")
    void exception_shouldStoreAccountNumber(){
        AccountCreationException exception = new AccountCreationException(
                savings2);

        assertEquals(savings2,exception.getAccount());
    }

    @Test
    @DisplayName("Exception must mention account ID")
    void exception_mustContainAccountId(){
        AccountCreationException exp = new AccountCreationException(savings2);
        String message= exp.getMessage();

        assertTrue(message.contains("svngs001"));
    }
}
