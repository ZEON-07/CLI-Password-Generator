import java.security.SecureRandom;
import java.util.ArrayList;
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
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
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

        boolean includeUpper = promptYesNo(scanner, "Include uppercase letters? (y/n): ");
        boolean includeNumbers = promptYesNo(scanner, "Include numbers? (y/n): ");
        boolean includeSpecial = promptYesNo(scanner, "Include special characters? (y/n): ");

        String password = generatePassword(length, includeUpper, includeNumbers, includeSpecial);
        System.out.println("\nGenerated Password: " + password);

        // TODO Issue 2: Evaluate and print password strength
        String strength = evaluateStrength(password);
        System.out.println("Password Strength: " + strength);
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

        boolean includeUpper = promptYesNo(scanner, "Include uppercase letters? (y/n): ");
        boolean includeNumbers = promptYesNo(scanner, "Include numbers? (y/n): ");
        boolean includeSpecial = promptYesNo(scanner, "Include special characters? (y/n): ");

        Set<String> passwords = new LinkedHashSet<>();
        while (passwords.size() < 5) {
            passwords.add(generatePassword(length, includeUpper, includeNumbers, includeSpecial));
        }

        System.out.println("\nGenerated Batch of 5 Passwords:");
        int count = 1;
        for (String pwd : passwords) {
            System.out.println(count++ + ". " + pwd + " (Strength: " + evaluateStrength(pwd) + ")");
        }
    }

    /**
     * Prompts user with a yes/no question.
     */
    private static boolean promptYesNo(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes");
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
        return generatePassword(length, false, false, false);
    }

    /**
     * Generates a random password with configurable character sets.
     * Guarantees at least one character from each selected category.
     *
     * @param length Desired character count
     * @param includeUpper Whether to include uppercase letters
     * @param includeNumbers Whether to include digits
     * @param includeSpecial Whether to include special symbols
     * @return Generated password string
     */
    public static String generatePassword(int length, boolean includeUpper, boolean includeNumbers, boolean includeSpecial) {
        StringBuilder poolBuilder = new StringBuilder(LOWERCASE);
        List<Character> chars = new ArrayList<>(length);

        // Guarantee at least one lowercase character
        chars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));

        if (includeUpper) {
            poolBuilder.append(UPPERCASE);
            chars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (includeNumbers) {
            poolBuilder.append(DIGITS);
            chars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        if (includeSpecial) {
            poolBuilder.append(SPECIAL);
            chars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        }

        String characterPool = poolBuilder.toString();

        while (chars.size() < length) {
            int index = random.nextInt(characterPool.length());
            chars.add(characterPool.charAt(index));
        }

        // Fisher-Yates shuffle to randomize positions
        for (int i = chars.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars.get(i);
            chars.set(i, chars.get(j));
            chars.set(j, temp);
        }

        StringBuilder sb = new StringBuilder(length);
        for (char c : chars) {
            sb.append(c);
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
}
