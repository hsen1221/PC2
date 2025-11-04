public class Main {


    static class Vault {
        private final int password;

        public Vault() {
            int randomPassword = (int) (Math.random() * 10000);
            this.password = randomPassword;
            System.out.println("Vault created - Password: " + this.password);
        }


        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return guess == this.password;
        }
    }

    static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting " + this.getName());
            super.start();
        }
    }

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

}
