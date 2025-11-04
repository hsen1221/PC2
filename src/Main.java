import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    // Static counters for each hacker
    static AtomicInteger ascendingAttempts = new AtomicInteger(0);
    static AtomicInteger descendingAttempts = new AtomicInteger(0);
    static AtomicInteger binaryAttempts = new AtomicInteger(0);
    static volatile boolean gameOver = false;
    static volatile String winner = null;

    // GUI Components
    static JFrame frame;
    static JProgressBar ascendingProgress;
    static JProgressBar descendingProgress;
    static JProgressBar binaryProgress;
    static JLabel ascendingLabel;
    static JLabel descendingLabel;
    static JLabel binaryLabel;
    static JTextArea logArea;
    static JLabel winnerLabel;
    static JLabel timerLabel;

    static class Vault {
        private final int password;

        public Vault() {
            int randomPassword = (int) (Math.random() * 10000); // 0 to 9999
            this.password = randomPassword;
            logMessage("Vault created - Password: " + this.password);
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
            logMessage("Starting " + this.getName());
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
            for (int guess = 0; guess <= 9999 && !gameOver; guess++) {
                ascendingAttempts.incrementAndGet();

                // Calculate percentage of range covered
                double percentage = (guess / 10000.0) * 100;
                updateAscendingProgress(guess, percentage);

                if (vault.isCorrectPassword(guess)) {
                    winner = this.getName();
                    logMessage(this.getName() + ": Hacked the vault! Password is " + guess);
                    endGame();
                    return;
                }
            }
            if (!gameOver) {
                logMessage(this.getName() + " failed after " + ascendingAttempts + " attempts");
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
            for (int guess = 9999; guess >= 0 && !gameOver; guess--) {
                descendingAttempts.incrementAndGet();

                // Calculate percentage of range covered (inverse of ascending)
                double percentage = ((9999 - guess) / 10000.0) * 100;
                updateDescendingProgress(guess, percentage);

                if (vault.isCorrectPassword(guess)) {
                    winner = this.getName();
                    logMessage(this.getName() + ": Hacked the vault! Password is " + guess);
                    endGame();
                    return;
                }
            }
            if (!gameOver) {
                logMessage(this.getName() + " failed after " + descendingAttempts + " attempts");
            }
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
            int attempts = 0;

            while (low <= high && !gameOver) {
                attempts++;
                binaryAttempts.set(attempts);
                int guess = low + (high - low) / 2; // Middle point

                // Calculate percentage based on search space reduction
                // Binary search halves the search space each time
                double searchSpace = high - low + 1; // Current range size
                double percentage = (1 - (searchSpace / 10000.0)) * 100;
                updateBinaryProgress(attempts, low, high, percentage);

                String message = this.getName() + " guessing: " + guess + " (range: " + low + "-" + high + ")";
                logMessage(message);

                int result = vault.checkPasswordWithFeedback(guess);

                if (result == 0) {
                    // Correct password found!
                    winner = this.getName();
                    message = this.getName() + ": Hacked the vault! Password is " + guess + " (found in " + attempts + " attempts)";
                    logMessage(message);
                    endGame();
                    return;
                } else if (result == -1) {
                    // Password is higher than guess
                    logMessage(this.getName() + ": Password is higher than " + guess);
                    low = guess + 1;
                } else {
                    // Password is lower than guess
                    logMessage(this.getName() + ": Password is lower than " + guess);
                    high = guess - 1;
                }
            }

            if (!gameOver) {
                logMessage(this.getName() + ": Failed to find password after " + attempts + " attempts");
            }
        }
    }

    // Police thread that counts down
    static class PoliceThread extends Thread {
        @Override
        public void run() {
            for (int i = 10; i > 0 && !gameOver; i--) {
                try {
                    updateTimerLabel(i);
                    Thread.sleep(1000); // 1 second delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!gameOver) {
                winner = "Police";
                logMessage("Game over for you hackers");
                endGame();
            }
        }
    }

    // GUI Methods
    public static void createAndShowGUI() {
        frame = new JFrame("Vault Hacking Race - Progress Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Progress Panel
        JPanel progressPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        progressPanel.setBorder(BorderFactory.createTitledBorder("Hacker Progress - Percentage of Range Covered"));

        // Ascending Hacker
        ascendingLabel = new JLabel("Ascending Hacker: 0% of range covered");
        ascendingProgress = new JProgressBar(0, 100);
        ascendingProgress.setStringPainted(true);
        ascendingProgress.setForeground(Color.RED);
        ascendingProgress.setString("0%");

        // Descending Hacker
        descendingLabel = new JLabel("Descending Hacker: 0% of range covered");
        descendingProgress = new JProgressBar(0, 100);
        descendingProgress.setStringPainted(true);
        descendingProgress.setForeground(Color.BLUE);
        descendingProgress.setString("0%");

        // Binary Hacker
        binaryLabel = new JLabel("Binary Hacker: 0% of search space eliminated");
        binaryProgress = new JProgressBar(0, 100);
        binaryProgress.setStringPainted(true);
        binaryProgress.setForeground(Color.GREEN);
        binaryProgress.setString("0%");

        progressPanel.add(ascendingLabel);
        progressPanel.add(ascendingProgress);
        progressPanel.add(descendingLabel);
        progressPanel.add(descendingProgress);
        progressPanel.add(binaryLabel);
        progressPanel.add(binaryProgress);

        // Winner Label and Timer
        JPanel topPanel = new JPanel(new BorderLayout());
        winnerLabel = new JLabel("Vault Hacking Race - Monitoring Progress...", JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        winnerLabel.setForeground(Color.BLACK);

        timerLabel = new JLabel("Police arriving in: 10 seconds", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(Color.DARK_GRAY);

        topPanel.add(winnerLabel, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.SOUTH);

        // Log Area
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Hacking Activity Log"));

        // Add components to frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(progressPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Update methods for GUI (thread-safe using SwingUtilities)
    public static void updateAscendingProgress(int guess, double percentage) {
        SwingUtilities.invokeLater(() -> {
            int progressValue = (int) percentage;
            ascendingProgress.setValue(progressValue);
            ascendingProgress.setString(String.format("%.1f%%", percentage));
            ascendingLabel.setText(String.format("Ascending: %.1f%% of 0-9999 (guess: %d)", percentage, guess));
        });
    }

    public static void updateDescendingProgress(int guess, double percentage) {
        SwingUtilities.invokeLater(() -> {
            int progressValue = (int) percentage;
            descendingProgress.setValue(progressValue);
            descendingProgress.setString(String.format("%.1f%%", percentage));
            descendingLabel.setText(String.format("Descending: %.1f%% of 9999-0 (guess: %d)", percentage, guess));
        });
    }

    public static void updateBinaryProgress(int attempts, int low, int high, double percentage) {
        SwingUtilities.invokeLater(() -> {
            int progressValue = (int) percentage;
            binaryProgress.setValue(progressValue);
            binaryProgress.setString(String.format("%.1f%%", percentage));
            binaryLabel.setText(String.format("Binary: %.1f%% search space eliminated (attempt: %d)", percentage, attempts));
        });
    }

    public static void updateTimerLabel(int secondsLeft) {
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Police arriving in: " + secondsLeft + " seconds");
        });
    }

    public static void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            System.out.println(message); // Also print to console
        });
    }

    public static void endGame() {
        gameOver = true;
        printFinalStats();

        SwingUtilities.invokeLater(() -> {
            if (winner.contains("Police")) {
                winnerLabel.setText("TIME'S UP! POLICE WIN!");
                winnerLabel.setForeground(Color.ORANGE);
                timerLabel.setText("Game Over - Time expired");
            } else {
                winnerLabel.setText(winner.toUpperCase() + " WINS!");
                winnerLabel.setForeground(Color.GREEN);
                timerLabel.setText("Game Over - Vault breached!");
            }

            // Update final percentages
            if (winner.contains("Ascending")) {
                double finalPercentage = (Integer.parseInt(winner.split(" ")[2]) / 10000.0) * 100;
                ascendingProgress.setString(String.format("%.1f%% - WINNER!", finalPercentage));
            } else if (winner.contains("Descending")) {
                double finalPercentage = ((9999 - Integer.parseInt(winner.split(" ")[2])) / 10000.0) * 100;
                descendingProgress.setString(String.format("%.1f%% - WINNER!", finalPercentage));
            } else if (winner.contains("Binary")) {
                binaryProgress.setString("100% - WINNER!");
            }

            // Disable further progress updates
            ascendingProgress.setEnabled(false);
            descendingProgress.setEnabled(false);
            binaryProgress.setEnabled(false);
        });
    }

    // Method to print final statistics
    public static void printFinalStats() {
        // Calculate final percentages
        double ascendingPercentage = (ascendingAttempts.get() / 10000.0) * 100;
        double descendingPercentage = (descendingAttempts.get() / 10000.0) * 100;

        String stats = "\n=== FINAL STATISTICS ===" +
                "\nWinner: " + winner +
                "\nAscending Hacker: " + ascendingAttempts + " attempts (" + String.format("%.1f", ascendingPercentage) + "% of range)" +
                "\nDescending Hacker: " + descendingAttempts + " attempts (" + String.format("%.1f", descendingPercentage) + "% of range)" +
                "\nBinary Search Hacker: " + binaryAttempts + " attempts" +
                "\nTotal attempts by all hackers: " + (ascendingAttempts.get() + descendingAttempts.get() + binaryAttempts.get()) +
                "\n=======================\n";

        logMessage(stats);
    }

    // Main method - program entry point
    public static void main(String[] args) {
        // Create GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> createAndShowGUI());

        // Small delay to ensure GUI is ready
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logMessage("=== VAULT HACKING RACE ===");
        logMessage("Monitoring hacker progress by percentage of range covered...");
        logMessage("Starting race with 3 hacking strategies...");

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