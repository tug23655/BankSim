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

    //helper method to wait thread until shouldTest
    public synchronized void waitHelper() throws InterruptedException {
        while (flag) //
        {
            wait();
        }
        counter++;
    }

    //helper method to decrement counter
    //notifyAll to wake up threads
    public synchronized void decrementHelper() throws InterruptedException {
        counter--;
        notifyAll();
    }

    //increase counter for new transfer threads
    //decrement counter after transfer
    public void transfer(int from, int to, int amount) throws InterruptedException {
        waitHelper();
//        accounts[from].waitForAvailableFunds(amount);
        //synchronized (accounts) {
        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
            System.out.printf("%s %s%n",
                    Thread.currentThread().toString(), accounts[to].toString());
        }
        
        
        decrementHelper();

        if (shouldTest()) {
            flag = true;

            new TestThread(this).start();
        }
        //}
    }

    //use while loop for wait, keep checking(the boolean student) if should test
    //sumthread is waiting for counter and boolean
    //notifyall when conditions met
    public synchronized void test() throws InterruptedException {

        
        while (counter > 0) {
            wait();
        }

        int sum = 0;

        for (Account account : accounts) {

            sum += account.getBalance();
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

        notifyAll();

        flag = false;
    }

    public int size() {
        return accounts.length;
    }

    public boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}
