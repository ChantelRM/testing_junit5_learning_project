# 🏦 Fintech Bank — JUnit 5 Learning Project

A simple Java banking library built as a hands-on introduction to **unit testing with JUnit 5**.

The focus is on learning testing concepts — not building a full app. The codebase is intentionally small so you can understand every line.

---

## 🎯 What This Project Covers

| Concept | Where to find it |
|---|---|
| `@Test`, `@BeforeEach`, `@AfterEach` | `BankAccountTest` — top of file |
| `assertEquals`, `assertTrue` | `DepositTests`, `TransactionHistoryTests` |
| `assertThrows` — testing exceptions | `WithdrawalTests`, `TransferTests` |
| Inspecting exception fields | `withdraw_moreThanBalance_shouldThrowInsufficientFunds` |
| `@ParameterizedTest` + `@ValueSource` | `DepositTests`, `WithdrawalTests` |
| `@Nested` for grouping tests | All sections in `BankAccountTest` |
| `@DisplayName` for readable output | Every test |
| Testing state doesn't change on failure | `transfer_onFailure_shouldNotChangeAnyBalance` |
| Testing immutability | `transactionHistory_shouldBeUnmodifiable` |

---

## 📁 Project Structure

```
fintech-bank/
├── src/
│   ├── main/java/com/fintech/
│   │   ├── exception/
│   │   │   └── InsufficientFundsException.java
│   │   └── model/
│   │       ├── BankAccount.java
│   │       └── Transaction.java
│   └── test/java/com/fintech/
│       ├── exception/
│       │   └── InsufficientFundsExceptionTest.java
│       └── model/
│           └── BankAccountTest.java        ← Start here
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+

### Run the tests

```bash
mvn test
```

### Run a single test class

```bash
mvn test -Dtest=BankAccountTest
```

### Run a single test method

```bash
mvn test -Dtest=BankAccountTest#deposit_validAmount_shouldIncreaseBalance
```

---

## 🔑 Key JUnit 5 Concepts — Quick Reference

### Test lifecycle annotations

```java
@BeforeEach   // runs before every @Test — use for fresh setup
@AfterEach    // runs after every @Test — use for cleanup
@BeforeAll    // runs once before all tests in the class (must be static)
@AfterAll     // runs once after all tests in the class (must be static)
```

### Common assertions

```java
assertEquals(expected, actual)         // values are equal
assertNotEquals(unexpected, actual)    // values are not equal
assertTrue(condition)                  // condition is true
assertFalse(condition)                 // condition is false
assertNull(object)                     // object is null
assertNotNull(object)                  // object is not null
assertThrows(Exception.class, () -> {  // code throws the right exception
    // code that should throw
});
```

### Parameterized tests

```java
// Run the same test with multiple inputs
@ParameterizedTest
@ValueSource(doubles = {0, -1, -999.99})
void myTest(double value) {
    // runs 3 times, once per value
}
```

### Grouping with @Nested

```java
@Nested
@DisplayName("Deposit tests")
class DepositTests {
    @Test void someTest() { ... }
}
```

---

## 💡 Extension Ideas

When you have time to continue, here are natural next steps:

- [ ] **SavingsAccount** — extends `BankAccount`, adds interest rate, test `applyInterest()`
- [ ] **AccountService** — handles multiple accounts, test `findByAccountNumber()`
- [ ] **Transaction filtering** — filter history by type or date range, test edge cases
- [ ] **Overdraft protection** — allow a configurable negative balance limit, update tests
- [ ] **`@ParameterizedTest` with `@CsvSource`** — test deposit + expected balance pairs
- [ ] **Mockito** — mock dependencies when you're ready to move past pure unit tests

---

## 📚 Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [JUnit 5 Annotations Cheat Sheet](https://www.baeldung.com/junit-5-annotations)
- [Parameterized Tests in JUnit 5](https://www.baeldung.com/parameterized-tests-junit-5)
