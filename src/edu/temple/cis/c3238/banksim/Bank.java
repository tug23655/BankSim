package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
//while(counter != 0 && status = false)
//{
//    wait();
//}
public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private int counter = 0;      //count transferThreads
    private boolean flag = false;           //check status

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
    }

    //increase counter for new transfer threads
    //decrement counter after transfer
    public void transfer(int from, int to, int amount) {
        counter++;
//        accounts[from].waitForAvailableFunds(amount);
        synchronized (accounts) {
            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
            }
            counter--;

            if (counter == 0 && flag == true) {
                synchronized (this) {
                   Thread thread = new Thread();
                   thread.start();
                }
                test();
            }
        }
    }

    //use while loop for wait, keep checking(the boolean student) if should test
    //sumthread is waiting for counter and boolean
    //notifyall when conditions met
    public void test() {
        int sum = 0;

        for (Account account : accounts) {
            System.out.printf("%s %s%n",
                    Thread.currentThread().toString(), account.toString());
        }
        System.out.println(Thread.currentThread().toString()
                + " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString()
                    + " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString()
                    + " The bank is in balance");
        }
    }

    public int size() {
        return accounts.length;
    }

    public boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}
