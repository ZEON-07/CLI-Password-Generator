/**
 * CLI Password Generator - C++ Implementation
 *
 * A modern, standalone C++ command-line tool for generating random passwords.
 * Features single & batch generation, custom character sets, strength evaluation,
 * and optional CSV exporting.
 */

#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <random>
#include <cctype>
#include <fstream>
#include <chrono>
#include <iomanip>
#include <sstream>

class PasswordGenerator {
private:
    static constexpr const char* LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    static constexpr const char* UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static constexpr const char* DIGITS = "0123456789";
    static constexpr const char* SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    std::mt19937 rng;

public:
    PasswordGenerator() {
        std::random_device rd;
        rng.seed(rd());
    }

    /**
     * Generates a random password with configurable character sets.
     * Guarantees at least one character from each selected category.
     */
    std::string generatePassword(int length, bool includeUpper, bool includeNumbers, bool includeSpecial) {
        if (length <= 0) return "";

        std::string pool = LOWERCASE;
        std::vector<char> chars;
        chars.reserve(length);

        // Guarantee at least one lowercase character
        std::uniform_int_distribution<size_t> lowerDist(0, std::string(LOWERCASE).size() - 1);
        chars.push_back(LOWERCASE[lowerDist(rng)]);

        if (includeUpper) {
            pool += UPPERCASE;
            std::uniform_int_distribution<size_t> upperDist(0, std::string(UPPERCASE).size() - 1);
            chars.push_back(UPPERCASE[upperDist(rng)]);
        }
        if (includeNumbers) {
            pool += DIGITS;
            std::uniform_int_distribution<size_t> digitDist(0, std::string(DIGITS).size() - 1);
            chars.push_back(DIGITS[digitDist(rng)]);
        }
        if (includeSpecial) {
            pool += SPECIAL;
            std::uniform_int_distribution<size_t> specialDist(0, std::string(SPECIAL).size() - 1);
            chars.push_back(SPECIAL[specialDist(rng)]);
        }

        std::uniform_int_distribution<size_t> poolDist(0, pool.size() - 1);
        while (chars.size() < static_cast<size_t>(length)) {
            chars.push_back(pool[poolDist(rng)]);
        }

        // Shuffle characters
        std::shuffle(chars.begin(), chars.end(), rng);

        return std::string(chars.begin(), chars.end());
    }

    /**
     * Overload for default lowercase password generation.
     */
    std::string generatePassword(int length) {
        return generatePassword(length, false, false, false);
    }

    /**
     * Evaluates password strength based on length and character diversity.
     */
    static std::string evaluateStrength(const std::string& password) {
        if (password.empty()) return "Weak";

        bool hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        for (unsigned char c : password) {
            if (std::islower(c)) hasLower = true;
            else if (std::isupper(c)) hasUpper = true;
            else if (std::isdigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        int categories = (hasLower ? 1 : 0) + (hasUpper ? 1 : 0) +
                         (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);

        if (password.length() >= 12 && categories == 4) {
            return "Strong";
        } else if (password.length() >= 8 && categories >= 2) {
            return "Good";
        } else {
            return "Weak";
        }
    }
};

/**
 * Trims leading and trailing whitespace.
 */
static std::string trim(const std::string& str) {
    size_t first = str.find_first_not_of(" \t\r\n");
    if (first == std::string::npos) return "";
    size_t last = str.find_last_not_of(" \t\r\n");
    return str.substr(first, (last - first + 1));
}

/**
 * Prompts user for a yes/no response.
 */
static bool promptYesNo(const std::string& prompt) {
    std::cout << prompt;
    std::string input;
    if (!std::getline(std::cin, input)) return false;
    input = trim(input);
    return (input == "y" || input == "Y" || input == "yes" || input == "YES");
}

/**
 * Prompts for desired password length with validation.
 */
static int promptForLength() {
    std::cout << "Enter desired password length (minimum 4): ";
    std::string input;
    if (!std::getline(std::cin, input)) return -1;
    input = trim(input);

    if (input.empty()) {
        std::cout << "Invalid input! Please enter a valid integer number.\n";
        return -1;
    }

    try {
        size_t pos = 0;
        int length = std::stoi(input, &pos);
        if (pos != input.length()) {
            std::cout << "Invalid input! Please enter a valid integer number.\n";
            return -1;
        }
        if (length < 4) {
            std::cout << "Warning: Password length should be at least 4 characters.\n";
            return -1;
        }
        return length;
    } catch (...) {
        std::cout << "Invalid input! Please enter a valid integer number.\n";
        return -1;
    }
}

/**
 * Returns the current local timestamp in ISO-8601 format.
 */
static std::string getCurrentTimestamp() {
    auto now = std::chrono::system_clock::now();
    std::time_t in_time_t = std::chrono::system_clock::to_time_t(now);
    std::tm buf{};
#if defined(_WIN32) || defined(_WIN64)
    localtime_s(&buf, &in_time_t);
#else
    localtime_r(&in_time_t, &buf);
#endif
    std::ostringstream ss;
    ss << std::put_time(&buf, "%Y-%m-%dT%H:%M:%S");
    return ss.str();
}

/**
 * Exports password records to a CSV file.
 */
static void exportToCSV(const std::vector<std::string>& passwords, const std::string& filename) {
    if (passwords.empty()) return;

    bool fileExists = false;
    {
        std::ifstream check(filename, std::ios::ate);
        if (check.is_open() && check.tellg() > 0) {
            fileExists = true;
        }
    }

    std::ofstream file(filename, std::ios::app);
    if (!file.is_open()) {
        std::cout << "Error writing to CSV file: Unable to open '" << filename << "'\n";
        return;
    }

    if (!fileExists) {
        file << "Timestamp,Password,Length,Strength\n";
    }

    std::string timestamp = getCurrentTimestamp();
    for (const auto& pwd : passwords) {
        file << timestamp << "," << pwd << "," << pwd.length() << ","
             << PasswordGenerator::evaluateStrength(pwd) << "\n";
    }

    std::cout << "Successfully exported " << passwords.size() << " password(s) to " << filename << "\n";
}

/**
 * Prompts user to optionally export generated passwords to a CSV file.
 */
static void promptAndExport(const std::vector<std::string>& passwords) {
    if (promptYesNo("\nExport to CSV? (y/n): ")) {
        std::cout << "Enter filename (default: passwords.csv): ";
        std::string filename;
        std::getline(std::cin, filename);
        filename = trim(filename);

        if (filename.empty()) {
            filename = "passwords.csv";
        }
        if (filename.length() < 4 || filename.substr(filename.length() - 4) != ".csv") {
            filename += ".csv";
        }

        exportToCSV(passwords, filename);
    }
}

/**
 * Handles single password generation.
 */
static void handleGenerateSingle(PasswordGenerator& generator) {
    int length = promptForLength();
    if (length <= 0) {
        std::cout << "Operation cancelled or invalid length.\n";
        return;
    }

    bool includeUpper = promptYesNo("Include uppercase letters? (y/n): ");
    bool includeNumbers = promptYesNo("Include numbers? (y/n): ");
    bool includeSpecial = promptYesNo("Include special characters? (y/n): ");

    std::string password = generator.generatePassword(length, includeUpper, includeNumbers, includeSpecial);
    std::cout << "\nGenerated Password: " << password << "\n";
    std::cout << "Password Strength: " << PasswordGenerator::evaluateStrength(password) << "\n";

    promptAndExport({password});
}

/**
 * Handles batch generation of 5 passwords.
 */
static void handleGenerateBatch(PasswordGenerator& generator) {
    int length = promptForLength();
    if (length <= 0) {
        std::cout << "Operation cancelled or invalid length.\n";
        return;
    }

    bool includeUpper = promptYesNo("Include uppercase letters? (y/n): ");
    bool includeNumbers = promptYesNo("Include numbers? (y/n): ");
    bool includeSpecial = promptYesNo("Include special characters? (y/n): ");

    std::vector<std::string> passwords;
    while (passwords.size() < 5) {
        std::string pwd = generator.generatePassword(length, includeUpper, includeNumbers, includeSpecial);
        if (std::find(passwords.begin(), passwords.end(), pwd) == passwords.end()) {
            passwords.push_back(pwd);
        }
    }

    std::cout << "\nGenerated Batch of 5 Passwords:\n";
    for (size_t i = 0; i < passwords.size(); i++) {
        std::cout << (i + 1) << ". " << passwords[i]
                  << " (Strength: " << PasswordGenerator::evaluateStrength(passwords[i]) << ")\n";
    }

    promptAndExport(passwords);
}

int main() {
    PasswordGenerator generator;
    bool running = true;

    std::cout << "========================================\n";
    std::cout << "     CLI PASSWORD GENERATOR (STARTER)   \n";
    std::cout << "========================================\n";

    while (running) {
        std::cout << "\nMenu Options:\n";
        std::cout << "1. Generate Password\n";
        std::cout << "2. Generate Batch of 5 Passwords\n";
        std::cout << "3. Exit\n";
        std::cout << "Select an option (1-3): ";

        std::string choice;
        if (!std::getline(std::cin, choice)) break;
        choice = trim(choice);

        if (choice == "1") {
            handleGenerateSingle(generator);
        } else if (choice == "2") {
            handleGenerateBatch(generator);
        } else if (choice == "3") {
            std::cout << "Exiting Password Generator. Stay safe online!\n";
            running = false;
        } else {
            std::cout << "Invalid option. Please enter 1, 2, or 3.\n";
        }
    }

    return 0;
}

