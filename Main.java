import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * CLI Password Generator - Starter Project
 *
 * A starter command-line tool for generating random passwords.
 * Participants can extend this project by completing the open issues in README.md.
 */
public class Main {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("========================================");
        System.out.println("     CLI PASSWORD GENERATOR (STARTER)   ");
        System.out.println("========================================");

        while (running) {
            System.out.println("\nMenu Options:");
            System.out.println("1. Generate Password");
            System.out.println("2. Generate Batch of 5 Passwords");
            System.out.println("3. Exit");
            System.out.print("Select an option (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleGenerateSingle(scanner);
                    break;
                case "2":
                    handleGenerateBatch(scanner);
                    break;
                case "3":
                    System.out.println("Exiting Password Generator. Stay safe online!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }

        scanner.close();
    }

    /**
     * Handles user interaction for generating a single password.
     */
    private static void handleGenerateSingle(Scanner scanner) {
        int length = promptForLength(scanner);
        if (length <= 0) {
            System.out.println("Operation cancelled or invalid length.");
            return;
        }

        // TODO Issue 1: Prompt user whether to include numbers and special characters
        // boolean includeNumbers = ...
        // boolean includeSpecial = ...

        String password = generatePassword(length);
        System.out.println("\nGenerated Password: " + password);

        // TODO Issue 2: Evaluate and print password strength
        String strength = evaluateStrength(password);
        System.out.println("Password Strength: " + strength);

        promptAndExport(scanner, Collections.singletonList(password));
    }

    /**
     * Handles batch password generation.
     */
    private static void handleGenerateBatch(Scanner scanner) {
        int length = promptForLength(scanner);
        if (length <= 0) {
            System.out.println("Operation cancelled or invalid length.");
            return;
        }

        Set<String> passwords = new LinkedHashSet<>();
        while (passwords.size() < 5) {
            passwords.add(generatePassword(length));
        }

        System.out.println("\nGenerated Batch of 5 Passwords:");
        int count = 1;
        for (String pwd : passwords) {
            System.out.println(count++ + ". " + pwd + " (Strength: " + evaluateStrength(pwd) + ")");
        }

        promptAndExport(scanner, passwords);
    }

    /**
     * Prompts the user for desired password length with basic validation.
     */
    private static int promptForLength(Scanner scanner) {
        System.out.print("Enter desired password length (minimum 4): ");
        try {
            int length = Integer.parseInt(scanner.nextLine().trim());
            if (length < 4) {
                System.out.println("Warning: Password length should be at least 4 characters.");
                return -1;
            }
            return length;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid integer number.");
            return -1;
        }
    }

    /**
     * Generates a random password using lowercase letters.
     *
     * @param length Desired character count
     * @return Generated password string
     */
    public static String generatePassword(int length) {
        // Starter logic: Only lowercase characters
        StringBuilder sb = new StringBuilder(length);

        // TODO Issue 1: Extend character pool to include uppercase letters, numbers, and symbols based on user input
        String characterPool = LOWERCASE;

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterPool.length());
            sb.append(characterPool.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Evaluates the strength of a password.
     *
     * @param password The password string to evaluate
     * @return Strength level: "Weak", "Good", or "Strong"
     */
    public static String evaluateStrength(String password) {
        if (password == null || password.length() == 0) return "Weak";
        
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        int categories = (hasLower ? 1 : 0) + (hasUpper ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        
        if (password.length() >= 12 && categories == 4) {
            return "Strong";
        } else if (password.length() >= 8 && categories >= 2) {
            return "Good";
        } else {
            return "Weak";
        }
    }

    /**
     * Prompts the user whether to export generated passwords to a CSV file.
     *
     * @param scanner Scanner for user input
     * @param passwords Collection of passwords to export
     */
    private static void promptAndExport(Scanner scanner, Collection<String> passwords) {
        System.out.print("\nExport to CSV? (y/n): ");
        String response = scanner.nextLine().trim();

        if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
            System.out.print("Enter filename (default: passwords.csv): ");
            String filename = scanner.nextLine().trim();
            if (filename.isEmpty()) {
                filename = "passwords.csv";
            }
            if (!filename.toLowerCase().endsWith(".csv")) {
                filename += ".csv";
            }
            exportToCSV(new ArrayList<>(passwords), filename);
        }
    }

    /**
     * Exports a list of passwords to a CSV file, appending new entries and writing headers if new.
     *
     * @param passwords List of password strings
     * @param filename Target CSV filename
     */
    public static void exportToCSV(List<String> passwords, String filename) {
        File file = new File(filename);
        boolean writeHeader = !file.exists() || file.length() == 0;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (writeHeader) {
                writer.println("Timestamp,Password,Length,Strength");
            }
            for (String pwd : passwords) {
                writer.println(timestamp + "," + pwd + "," + pwd.length() + "," + evaluateStrength(pwd));
            }
            System.out.println("Successfully exported " + passwords.size() + " password(s) to " + file.getName());
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
