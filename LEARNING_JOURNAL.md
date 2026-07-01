# Learning Journal — Fintech Bank Project

A running log of design decisions, what I learned, and why I chose what I chose.
Not a tutorial — this is *my* reasoning, written down so I don't have to re-derive it later.

---

## How to use this file

Each entry follows roughly the same shape:
- **What I was trying to do**
- **The decision** — what I went with
- **Why** — the reasoning, including what I almost did instead
- **Test that proves it** — which test in the suite locks this decision in

New entries go at the bottom, in the order I actually hit them. This is a log, not a polished spec.

---

## Entry 1 — Should `interestRate` be a constructor parameter or a hardcoded constant?

**What I was trying to do:** Add an interest rate to `SavingsAccount`.

**The decision:** Constructor parameter, stored as a `private final` field.

**Why:** A hardcoded constant means every `SavingsAccount` ever created is stuck at one rate — no two customers could ever have different rates, which isn't realistic. Passing it into the constructor means each *instance* can have its own rate while staying the same class.

The alternative — making separate subclasses per rate (`FiveCentSavings`, `TenCentSavings`) — only makes sense when the *type itself* behaves differently (different rules, different fees), not when it's just one number changing. Rule I'm keeping: **if only a value changes, parameterize it. If a bundle of behaviors changes together, that's when a subclass earns its keep.**

**Test that proves it:** `applyInterest_variousRates_shouldProduceExpectedBalance` (parameterized, multiple rate/balance pairs) — this test would be impossible to write meaningfully if the rate were a fixed constant, since every row would need its own subclass.

---

## Entry 2 — How does `SavingsAccount` change `balance` without `BankAccount` exposing it?

**What I was trying to do:** Write `applyInterest()`, which needs to increase `balance` — but `balance` is `private` in the parent class.

**The decision:** Don't touch `balance` directly at all. Call the inherited `deposit()` method instead, since interest is, semantically, money being added to the account.

**Why:** I almost reached for making `balance` `protected` so subclasses could touch it directly. That's a trap — it means any subclass (now or in the future) could set `balance` to anything, bypassing the validation that `deposit()`/`withdraw()` already enforce. The whole point of keeping `balance` `private` is that the *only* way to change it is through methods that have rules attached.

Once I reframed "applying interest" as "a kind of deposit," the problem disappeared — I didn't need a new way to touch balance, I needed to reuse the one that already existed.

**Test that proves it:** `applyInterest_shouldAddTransactionToHistory` — if `applyInterest()` bypassed `deposit()` and touched balance directly, this test would catch it, since no transaction would be logged.

---

## Entry 3 — Should `applyInterest()` return the new balance?

**What I was trying to do:** Decide what `applyInterest()` should return, if anything.

**The decision:** `void`. No return value.

**Why:** Matches the existing pattern — `deposit()` and `withdraw()` are also `void`. If you want the result, call `getBalance()` after. Mixing "do a thing" with "report a value" in one return makes a method do two jobs, and would make `applyInterest()` inconsistent with every other state-changing method in the class.

(Open question I haven't resolved: a method could *separately* return how much interest was just added, for something like printing a receipt. Different from returning balance — that would be returning "what just happened," not "what's true now." Parked for later, not needed yet.)

**Test that proves it:** Every `applyInterest` test calls `getBalance()` *after* calling `applyInterest()` — never assigns the method call to a variable.

---

## Entry 4 — Validating `interestRate` in the constructor

**What I was trying to do:** Stop a `SavingsAccount` from being created with a nonsensical interest rate.

**The decision:** Throw `IllegalArgumentException` if `interestRate < 0`, same as `BankAccount`'s constructor already does for a negative initial balance.

**Why:** Negative interest doesn't make sense for this model — it's not a real product I'm trying to simulate, so there's no reason to allow it. Reused the exact same exception type and the exact same "check before assign" pattern already established in `BankAccount`, rather than inventing a new convention.

**Test that proves it:** `invalidInterestRaisesException` — `assertThrows(IllegalArgumentException.class, () -> new SavingsAccount(..., -2))`.

---

## Entry 5 — Bug: zero balance + interest = exception, not zero interest

**What happened:** A parameterized test row — `initialBalance = 0.00, rate = 0.05` — failed with an `IllegalArgumentException` I didn't expect.

**Root cause:** `0.00 * 0.05 = 0.00`, and `deposit()` rejects any amount `<= 0` (it was written assuming a human manually depositing R0 is a mistake worth blocking). `applyInterest()` was calling `deposit()` with that `0.00`, and inheriting a rule that wasn't designed with this case in mind.

**The decision:** *(Still open — this is the real unresolved design question in the project right now.)* Two live options:
1. Guard inside `applyInterest()` — skip calling `deposit()` entirely if the calculated interest is `0`, since there's nothing to apply.
2. Reconsider whether "interest on a zero balance" is a test case worth having at all, given how `deposit()` is written.

**Why this is worth a full entry on its own:** This is a good example of a bug that only shows up because two methods, each correct *on their own*, made different assumptions about what "zero" means once combined.

**Test that proves it (once resolved):** the `0.00, 0.05, 0.00` row in `applyValidInterest`.

---

## Entry 6 — `deposit()` needed to support a transaction type, without breaking existing callers

**What I was trying to do:** Make interest show up in transaction history as `INTEREST`, not `DEPOSIT`, so the two are distinguishable later.

**What I almost did:** Check the transaction *description* string to decide the type (e.g. `if (description.equals("Savings earned from savings")) type = INTEREST;`). Rejected this — it makes the type fragile and invisible: a single typo in the description silently produces the wrong type, with no error. The description's job is to be human-readable text, not to carry program logic.

**The decision:** Overload `deposit()`. Keep the original two-argument version working exactly as before (defaults to `DEPOSIT`), and add a three-argument version that takes the type explicitly. The two-argument version *delegates to* the three-argument one, rather than duplicating the validation and balance-update logic:

```java
public void deposit(double amount, String description) {
    deposit(amount, description, Transaction.Type.DEPOSIT);
}

public void deposit(double amount, String description, Transaction.Type type) {
    if (amount <= 0) {
        throw new IllegalArgumentException("Deposit amount must be positive.");
    }
    balance += amount;
    transactionHistory.add(new Transaction(type, amount, description));
}
```

**Mistake I made on the way here:** my first attempt at the second method hardcoded the description to `"Interest earned"` instead of keeping it as a parameter — which meant any *future* transaction type (refund, bonus, etc.) would incorrectly say "Interest earned" too. Fixed by keeping description as a real parameter, same as the type.

**Why delegation matters:** validation and balance-update logic now exist in exactly one place. If I ever need to change deposit rules (e.g. a max deposit amount), I change it once, and both entry points get the fix automatically.

**Test that proves it:** `deposit_shouldAddToTransactionHistory` (still passes, untouched, proving the 2-arg version's behavior didn't change) + a new test asserting an interest deposit's logged transaction has `Transaction.Type.INTEREST`.

---

## Entry 7 — One map or two, for `AccountsService` holding both `BankAccount` and `SavingsAccount`?

**What I was trying to do:** Store and look up accounts of different types (regular + savings, with student/business planned later).

**What I almost did:** Two separate maps — `Map<String, BankAccount>` and `Map<String, SavingsAccount>`.

**The decision:** One map — `Map<String, BankAccount>` — holding *both* kinds of account.

**Why:** Since `SavingsAccount extends BankAccount`, a `SavingsAccount` *is* a `BankAccount`. Two maps means two sources of truth for the same data — add an account to one map and forget the other, and now the data is inconsistent with itself, for no reason other than the maps not being kept in sync. One map removes that failure mode entirely.

For type-specific behavior (e.g. needing to call `applyInterest()`, which only exists on `SavingsAccount`), use `instanceof` pattern matching at the point of use, rather than maintaining a separate collection:

```java
if (account instanceof SavingsAccount savingsAccount) {
    savingsAccount.applyInterest();
}
```

For "give me all accounts of one type," filter the single map on demand rather than storing a redundant second copy:

```java
public List<SavingsAccount> getAllSavingsAccounts() {
    return accounts.values().stream()
        .filter(account -> account instanceof SavingsAccount)
        .map(account -> (SavingsAccount) account)
        .collect(Collectors.toList());
}
```

**Test that proves it:** any test that adds a mix of `BankAccount` and `SavingsAccount` objects to the same `AccountsService`, then confirms `findBankAccount()` can retrieve either type, and `getAllSavingsAccounts()` returns only the savings ones.

---

## Entry 8 — Enum field for account type, or rely on the class hierarchy?

**What I was trying to do:** Figure out how the system should know "what kind of account is this" — now that student/business accounts are planned for later.

**What I almost did:** Add an `AccountType` enum (`REGULAR`, `SAVINGS`, `STUDENT`, `BUSINESS`) as a field on `BankAccount`.

**The decision:** No enum. The class hierarchy itself *is* the type system (`instanceof SavingsAccount`, etc.).

**Why:** An enum field and the actual Java class would be two separate systems both claiming to answer "what type is this account" — and nothing forces them to agree. It's possible to construct a `SavingsAccount` object but mistakenly set its `accountType` field to `REGULAR`, and now different parts of the code disagree about what the object is, depending on which one they check. That's redundant state, and it's a bug waiting to happen as the codebase grows.

**Rule I'm keeping:** if a "type" changes *behavior* (like `applyInterest()` only existing on savings), it belongs in the class hierarchy. If it's purely a label with no behavior attached (e.g. a `riskTier` used only for a report), an enum field is fine for that.

**Test that proves it:** no dedicated test — this is a structural decision proven by the *absence* of an `AccountType` field anywhere in the codebase, and by `getAllSavingsAccounts()` working correctly off `instanceof` alone.

---

## Entry 9 — Custom exception for duplicate account numbers

**What I was trying to do:** Decide what happens when `addAccount()` is called with an account number that's already in use.

**The decision:** Throw a custom checked exception, `AccountCreationException`, rather than silently overwriting the existing account (which is what `Map.put()` would do by default).

**Why:** Silent overwrite would mean an existing customer's account could vanish from the map without any warning, replaced by a new one with the same number — a serious, silent data-loss bug in a banking context. Followed the same pattern already established by `InsufficientFundsException`: a checked exception that carries the relevant object as a field, so the catcher can inspect what went wrong.

**Design choice within the exception:** `AccountCreationException` stores the *rejected* (`newAccount`) account, not the one already occupying that number. Decided this because the purpose is to keep a log of rejected account-creation attempts — so the thing worth keeping a record of is what was rejected, not what already existed.

**Test that proves it:** `addAccount_duplicateNumber_shouldThrowException` — `assertThrows(AccountCreationException.class, ...)`, plain `@Test`, not parameterized, since it's a single well-defined scenario.

---

## Entry 10 — Parameterized tests don't mix success and failure in one method

**What I was trying to do:** Test `addAccount()` for both accounts that should succeed and a duplicate that should throw — wondered if this could be one `@ParameterizedTest`.

**The decision:** Split into two tests — one `@ParameterizedTest` covering only the successful cases, one plain `@Test` for the duplicate-throws case.

**Why:** `@ParameterizedTest` is for running *the same assertion logic* against different inputs. Success and failure aren't the same assertion logic — one needs `assertEquals`/`assertDoesNotThrow`, the other needs `assertThrows`. Forcing both into one test means the test body needs an `if` statement to decide which kind of assertion to run, which is a sign the test is actually two tests pretending to be one.

**Rule I'm keeping:** if a test needs an `if`, it's two tests wearing one coat.

**Test that proves it:** `addAccount_validAccounts_shouldSucceed` (parameterized) and `addAccount_duplicateNumber_shouldThrowException` (plain `@Test`), kept as separate methods.

---

## Open questions / not yet resolved

- [ ] Entry 5 — should `applyInterest()` guard against a zero-interest deposit, or should the test data be reconsidered?
- [ ] Should `findBankAccount()` return `null` for a missing account, or switch to `Optional<BankAccount>`?
- [ ] Should there be a single-lookup `findSavingsAccountByNumber(String)`, or is going through `findBankAccount()` + a manual `instanceof` check enough for this project's scope?
- [ ] Overdraft protection — not yet built.
- [ ] Transaction filtering by type/date on `BankAccount` — not yet built.
- [ ] Mockito — intentionally parked for later, not needed yet since there are no external dependencies to mock.
