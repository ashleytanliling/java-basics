package Immutable.Bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankCustomer {

    private static int lastCustomerId = 10_000_000;

    private final String name;
    private final int customerId;
    private final List<BankAccount> accounts = new ArrayList<>();

    //(1) package-private
    BankCustomer(String name, double checkingAmount, double savingsAmount) {
        this.name = name;
        this.customerId = lastCustomerId++;
        accounts.add(new BankAccount(BankAccount.AccountType.CHECKING, checkingAmount));
        accounts.add(new BankAccount(BankAccount.AccountType.SAVINGS, savingsAmount));
    }

    public String getName() {
        return name;
    }

    //int -> String
    public String getCustomerId() {
        //return customerId;
        return "%015d".formatted(customerId);
    }

    public List<BankAccount> getAccounts() {
        //(2) return accounts;
        return List.copyOf(accounts);   //rtn unmodifiable copy, initialized with accounts data
    }

    //assume each BankCustomer can only have 1 acc per AccountType
    public BankAccount getAccount(BankAccount.AccountType type) {
        for (var account : accounts) {
            if (account.getAccountType() == type) {
                return account;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String[] accountStrings = new String[accounts.size()];
        Arrays.setAll(accountStrings, i -> accounts.get(i).toString());
        return "Customer: %s (id:%015d)%n\t%s%n".formatted(name, customerId, String.join("\n\t", accountStrings));
    }
}
