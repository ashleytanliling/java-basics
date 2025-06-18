package Immutable.Bank;

import Immutable.Dto.Transaction;

import java.util.LinkedHashMap;
import java.util.Map;

public class BankAccount {

    public enum AccountType {CHECKING, SAVINGS}

    //enum is immutable type, so these 2 fields aren't subjected to side-effects
    private final AccountType accountType;
    private double balance;
    private final Map<Long, Transaction> transactions = new LinkedHashMap<>();  //by insertion order

    //(1) public -> package-private (Main class can't new nor extend)
    BankAccount(AccountType accountType, double balance) {
        this.accountType = accountType;
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

//    public Map<Long, Transaction> getTransactions() {
//        //(2) return transactions;
//        return Map.copyOf(transactions);    //rtn unmodifiable copy
//    }

    //(3) unmodifiable copy + immutable type (String)
    public Map<Long, String> getTransactions() {
        //(2) return transactions;
        //return Map.copyOf(transactions);    //rtn unmodifiable copy
        Map<Long, String> txMap = new LinkedHashMap<>();    //immutable type (String)
        for (var tx : transactions.entrySet()) {
            txMap.put(tx.getKey(), tx.getValue().toString());
        }
        return txMap;
    }

    //(1) package-private
    void commitTransaction(int routingNumber, long transactionId, String customerId, double amount) {
        balance += amount;
        transactions.put(transactionId,
                new Transaction(routingNumber, transactionId, Integer.parseInt(customerId), amount));
    }

    @Override
    public String toString() {
        return "%s $%.2f".formatted(accountType, balance);
    }
}
