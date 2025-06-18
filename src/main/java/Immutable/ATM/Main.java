package Immutable.ATM;

import Immutable.Bank.Bank;
import Immutable.Bank.BankAccount;
import Immutable.Bank.BankCustomer;

//class MyAccount extends BankAccount {     //(1) err
//
//}

public class Main {
    public static void main(String[] args) {

//        BankCustomer joe = new BankCustomer("Joe", 500.00, 10000.00);     //(1)
//        System.out.println(joe);

        Bank bank = new Bank(3214567);
        bank.addCustomer("Joe", 500.00, 10000.00);

        BankCustomer joe = bank.getCustomer("000000010000000");
        System.out.println(joe);

//        List<BankAccount> accounts = joe.getAccounts();
//        accounts.clear();   //(2) cannot over-write
//        System.out.println(joe);

//        accounts.add(new BankAccount(BankAccount.AccountType.CHECKING, 150000));    //(1) err
//        System.out.println(joe);

        //  deposit
        if (bank.doTransaction(joe.getCustomerId(), BankAccount.AccountType.CHECKING, 35)) {
            System.out.println(joe);
        }

        //  withdraw - unsuccessful
        if (bank.doTransaction(joe.getCustomerId(), BankAccount.AccountType.CHECKING, -535.10)) {
            System.out.println(joe);
        }

        //  withdraw
        if (bank.doTransaction(joe.getCustomerId(), BankAccount.AccountType.CHECKING, -535)) {
            System.out.println(joe);
        }

        BankAccount checking = joe.getAccount(BankAccount.AccountType.CHECKING);
        var transactions = checking.getTransactions();
        transactions.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));
        //value = routingNumber : customerId : transactionId : amount

//        //  (2) unable to mod transactions
//        transactions.put(3L, new Transaction(1, 1, Integer.parseInt(joe.getCustomerId()), 500));

//        //  (3) still can mod transactions
//        System.out.println("--------------------------------------------");
//        for (var tx : transactions.values()) {
//            tx.setCustomerId(2);
//            tx.setAmount(10000.00);
//        }
//        transactions.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));

        //  (3) unable to mod transactions
        joe.getAccount(BankAccount.AccountType.CHECKING)
                .getTransactions().clear();

        System.out.println("--------------------------------------------");
        joe.getAccount(BankAccount.AccountType.CHECKING)
                .getTransactions()
                .forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));
    }
}
