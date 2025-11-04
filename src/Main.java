public class Main {

    // Static counters for each hacker
    static int ascendingAttempts = 0;
    static int descendingAttempts = 0;
    static int binaryAttempts = 0;

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

        // New method for binary search hacker - provides feedback
        public int checkPasswordWithFeedback(int guess) {
            try {
                Thread.sleep(5); // Simulate processing time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (guess == this.password) {
                return 0; // Correct
            } else if (guess < this.password) {
                return -1; // Too low
            } else {
                return 1; // Too high
            }
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
                ascendingAttempts++; // Count this attempt
                if (vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + ": Hacked the vault! Password is " + guess);
                    printFinalStats();
                    System.exit(0);
                }
            }
            // If we reach here, hacker failed to find password
            System.out.println(this.getName() + " failed after " + ascendingAttempts + " attempts");
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
                descendingAttempts++; // Count this attempt
                if (vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + ": Hacked the vault! Password is " + guess);
                    printFinalStats();
                    System.exit(0);
                }
            }
            // If we reach here, hacker failed to find password
            System.out.println(this.getName() + " failed after " + descendingAttempts + " attempts");
        }
    }

    // Binary Search Hacker
    static class BinarySearchHackerThread extends HackerThread {
        public BinarySearchHackerThread(Vault vault) {
            super(vault);
            this.setName("BinarySearchHackerThread");
        }

        @Override
        public void run() {
            int low = 0;
            int high = 9999;
            int localAttempts = 0;

            while (low <= high) {
                localAttempts++;
                binaryAttempts = localAttempts; // Update global counter
                int guess = low + (high - low) / 2; // Middle point

                System.out.println(this.getName() + " guessing: " + guess +
                        " (range: " + low + "-" + high + ")");

                int result = vault.checkPasswordWithFeedback(guess);

                if (result == 0) {
                    // Correct password found!
                    System.out.println(this.getName() + ": Hacked the vault! Password is " + guess +
                            " (found in " + localAttempts + " attempts)");
                    printFinalStats();
                    System.exit(0);
                } else if (result == -1) {
                    // Password is higher than guess
                    System.out.println(this.getName() + ": Password is higher than " + guess);
                    low = guess + 1;
                } else {
                    // Password is lower than guess
                    System.out.println(this.getName() + ": Password is lower than " + guess);
                    high = guess - 1;
                }
            }

            System.out.println(this.getName() + ": Failed to find password after " + localAttempts + " attempts");
            binaryAttempts = localAttempts;
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
            printFinalStats();
            System.exit(0); // End program if police arrive
        }
    }

    // Method to print final statistics
    public static void printFinalStats() {
        System.out.println("\n=== FINAL STATISTICS ===");
        System.out.println("Ascending Hacker attempts: " + ascendingAttempts);
        System.out.println("Descending Hacker attempts: " + descendingAttempts);
        System.out.println("Binary Search Hacker attempts: " + binaryAttempts);

        // Calculate efficiency
        int totalAttempts = ascendingAttempts + descendingAttempts + binaryAttempts;
        System.out.println("Total attempts by all hackers: " + totalAttempts);

        // Find most efficient hacker
        String mostEfficient = "Binary Search Hacker";
        int minAttempts = binaryAttempts;

        if (ascendingAttempts > 0 && ascendingAttempts < minAttempts) {
            mostEfficient = "Ascending Hacker";
            minAttempts = ascendingAttempts;
        }
        if (descendingAttempts > 0 && descendingAttempts < minAttempts) {
            mostEfficient = "Descending Hacker";
            minAttempts = descendingAttempts;
        }

        System.out.println("Most efficient hacker: " + mostEfficient + " (" + minAttempts + " attempts)");
        System.out.println("=======================\n");
    }

    // Main method - program entry point
    public static void main(String[] args) {
        System.out.println("Starting vault hacking race...");
        System.out.println("Now with 3 hackers: Ascending, Descending, and Binary Search!");

        Vault vault = new Vault();

        // Create threads
        AscendingHackerThread ascendingHacker = new AscendingHackerThread(vault);
        DescendingHackerThread descendingHacker = new DescendingHackerThread(vault);
        BinarySearchHackerThread binaryHacker = new BinarySearchHackerThread(vault);
        PoliceThread police = new PoliceThread();

        // Start all threads
        ascendingHacker.start();
        descendingHacker.start();
        binaryHacker.start();
        police.start();
    }
}