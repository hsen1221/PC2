public class Main {

    // Vault class to store the password
    static class Vault {
        private final int password;

        public Vault() {
            int randomPassword = (int) (Math.random() * 10000); // 0 to 9999
            this.password = randomPassword;
            System.out.println("Vault created - Password: " + this.password);
        }

        // Check if guessed password is correct
        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5); // Simulate processing time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return guess == this.password;
        }
    }

    // Base class for hacker threads
    static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setPriority(Thread.MAX_PRIORITY); // High priority for hackers
        }

        @Override
        public void start() {
            System.out.println("Starting " + this.getName());
            super.start();
        }
    }

    // Hacker that guesses from 0 to 9999
    static class AscendingHackerThread extends HackerThread {
        public AscendingHackerThread(Vault vault) {
            super(vault);
            this.setName("AscendingHackerThread");
        }

        @Override
        public void run() {
            for (int guess = 0; guess <= 9999; guess++) {
                if (vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + ": Hacked the vault! Password is " + guess);
                    System.exit(0);
                }
            }
        }
    }

    // Hacker that guesses from 9999 to 0
    static class DescendingHackerThread extends HackerThread {
        public DescendingHackerThread(Vault vault) {
            super(vault);
            this.setName("DescendingHackerThread");
        }

        @Override
        public void run() {
            for (int guess = 9999; guess >= 0; guess--) {
                if (vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + ": Hacked the vault! Password is " + guess);
                    System.exit(0);
                }
            }
        }
    }

    // Police thread that counts down
    static class PoliceThread extends Thread {
        @Override
        public void run() {
            for (int i = 10; i > 0; i--) {
                try {
                    Thread.sleep(1000); // 1 second delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Police arriving in: " + i + " seconds");
            }

            System.out.println("Game over for you hackers");
            System.exit(0); // End program if police arrive
        }
    }

    // Main method - program entry point
    public static void main(String[] args) {
        System.out.println("Starting vault hacking race...");

        Vault vault = new Vault();

        // Create threads
        AscendingHackerThread ascendingHacker = new AscendingHackerThread(vault);
        DescendingHackerThread descendingHacker = new DescendingHackerThread(vault);
        PoliceThread police = new PoliceThread();

        // Start all threads
        ascendingHacker.start();
        descendingHacker.start();
        police.start();
    }
}