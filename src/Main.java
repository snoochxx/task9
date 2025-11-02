import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Account account = new Account();
        DepositThread depositThread = new DepositThread(account);
        depositThread.start();

        account.withdraw(5000);

        depositThread.interrupt();
        depositThread.join();

        System.out.println("Окончательный баланс: " + account.getBalance());
    }
}

class Account {
    private int balance = 0;

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Пополнено: " + amount + ", новый баланс: " + balance);
        notifyAll();
    }

    public synchronized void withdraw(int amount) throws InterruptedException {
        while (balance < amount) {
            System.out.println("Ждем пополнения баланса для снятия " + amount);
            wait();
        }
        balance -= amount;
        System.out.println("Снято: " + amount + ", остаток: " + balance);
    }

    public synchronized int getBalance() {
        return balance;
    }
}

class DepositThread extends Thread {
    private final Account account;
    private final Random random = new Random();

    DepositThread(Account account) {
        this.account = account;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int amount = random.nextInt(1000) + 1;
                account.deposit(amount);
                Thread.sleep(200);
            }
        } catch (InterruptedException ignored) {
        }
    }
}
