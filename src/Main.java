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
                Thread.sleep(5); // Simulate processing time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return guess == this.password;
        }
    }}