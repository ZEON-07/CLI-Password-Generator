# CLI Password Generator (Starter Project)

A beginner-friendly Java command-line interface (CLI) application for generating random passwords. This starter repository provides core menu navigation, input handling, and a basic lowercase password generator with placeholders for participants to implement key features.

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher (JDK 17+ recommended).
- Check your installation:
  ```bash
  javac -version
  java -version
  ```

---

## How to Compile and Run

1. Open your terminal or command prompt in the project root directory:
   ```bash
   cd "CLI Password Generator"
   ```

2. Compile the Java source file:
   ```bash
   javac Main.java
   ```

3. Run the compiled application:
   ```bash
   java Main
   ```

*(Alternatively, on Java 11+, you can run the source file directly without manually compiling: `java Main.java`)*

---

## Current Functionality

- Interactive CLI menu loop.
- Input validation for password length (minimum 4 characters).
- Generates a random password using lowercase alphabetic characters (`a-z`) via `SecureRandom`.

---

## Open Issues for Participants

Pick up and resolve the following open tasks in [Main.java](file:///c:/Code%20Arena/CLI%20Password%20Generator/Main.java):

### 📌 Issue 1: Include Numbers and Special Characters Based on User Input
- **Description**: In the starter code, passwords only consist of lowercase characters. Expand the generation logic so users can customize their character sets.
- **Requirements**:
  - Prompt the user with interactive questions (e.g., `Include numbers? (y/n)` and `Include special characters? (y/n)`).
  - Update `generatePassword(...)` to accept these flags.
  - Dynamically build the character pool from:
    - Lowercase: `abcdefghijklmnopqrstuvwxyz`
    - Uppercase (optional/recommended): `ABCDEFGHIJKLMNOPQRSTUVWXYZ`
    - Digits: `0123456789`
    - Special characters: `!@#$%^&*()-_=+[]{}|;:,.<>?`
  - Ensure at least one character from each selected category is guaranteed in the generated password.

---

### 📌 Issue 2: Password Strength Evaluator
- **Description**: Implement the `evaluateStrength(String password)` function to inform users how resilient their password is.
- **Requirements**:
  - The function must return one of three strings: `"Weak"`, `"Good"`, or `"Strong"`.
  - Display the evaluated strength after a password is generated.
  - Suggested evaluation criteria:
    - **Weak**: Length $< 8$, or only one character category used.
    - **Good**: Length $\ge 8$ and includes at least two character categories (e.g., letters + numbers).
    - **Strong**: Length $\ge 12$ and includes a combination of uppercase, lowercase, numbers, and special characters.

---

### 📌 Issue 3: Batch Password Generation
- **Description**: Add support for generating 5 passwords in a single request.
- **Requirements**:
  - Implement menu option `2` (`Generate Batch of 5 Passwords`) inside `handleGenerateBatch(Scanner scanner)`.
  - Prompt for length and character options once, then output 5 distinct generated passwords.
  - Display the strength evaluation next to each generated password in the batch.

---

## Project Structure

```
CLI Password Generator/
├── Main.java     # Main program containing CLI menu, logic, and TODO markers
└── README.md     # Documentation, build instructions, and participant challenges
```
