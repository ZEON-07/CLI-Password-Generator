import java.security.SecureRandom;
import java.util.Scanner;

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
            System.out.println("2. Generate Batch of 5 Passwords [TODO]");
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
        // String strength = evaluateStrength(password);
        // System.out.println("Password Strength: " + strength);
    }

    /**
     * Handles batch password generation.
     */
    private static void handleGenerateBatch(Scanner scanner) {
        // TODO Issue 3: Allow the user to generate a batch of 5 passwords at once.
        System.out.println("\n[Feature Not Implemented Yet]");
        System.out.println("Open Issue 3: Implement batch generation of 5 passwords.");
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
        // TODO Issue 2: Write logic to score password based on length and character diversity
        // Example criteria:
        // - "Weak": short (< 8 chars) or single character set
        // - "Good": moderate length (>= 8 chars) with mix of letters & numbers
        // - "Strong": long (>= 12 chars) with uppercase, lowercase, numbers, and special chars
        return "Not Evaluated";
    }
}
