/**
 * CLI Password Generator - C Port
 *
 * A lightweight command-line tool for generating random passwords ported from Java (Main.java).
 * Supports single and batch password generation, input validation, and strength evaluation.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdbool.h>

#if defined(_WIN32) || defined(_WIN64)
#include <windows.h>
#include <wincrypt.h>
#endif

static const char LOWERCASE[] = "abcdefghijklmnopqrstuvwxyz";

/**
 * Returns a cryptographically secure (or well-seeded fallback) random index in range [0, max - 1].
 */
static unsigned int get_random_index(unsigned int max) {
    if (max == 0) return 0;
#if defined(_WIN32) || defined(_WIN64)
    HCRYPTPROV hProv;
    unsigned int val = 0;
    if (CryptAcquireContext(&hProv, NULL, NULL, PROV_RSA_FULL, CRYPT_VERIFYCONTEXT | CRYPT_SILENT)) {
        if (CryptGenRandom(hProv, sizeof(val), (BYTE *)&val)) {
            CryptReleaseContext(hProv, 0);
            return val % max;
        }
        CryptReleaseContext(hProv, 0);
    }
#else
    FILE *f = fopen("/dev/urandom", "rb");
    if (f != NULL) {
        unsigned int val = 0;
        if (fread(&val, sizeof(val), 1, f) == 1) {
            fclose(f);
            return val % max;
        }
        fclose(f);
    }
#endif
    return (unsigned int)rand() % max;
}

/**
 * Generates a random password using lowercase letters.
 *
 * @param length Desired character count
 * @return Dynamically allocated password string (caller must free)
 */
char *generate_password(int length) {
    if (length <= 0) return NULL;

    char *password = (char *)malloc((size_t)length + 1);
    if (!password) {
        fprintf(stderr, "Memory allocation error.\n");
        return NULL;
    }

    size_t pool_len = strlen(LOWERCASE);
    for (int i = 0; i < length; i++) {
        unsigned int idx = get_random_index((unsigned int)pool_len);
        password[i] = LOWERCASE[idx];
    }
    password[length] = '\0';
    return password;
}

/**
 * Evaluates the strength of a password based on length and character categories.
 *
 * @param password The password string to evaluate
 * @return Strength level: "Weak", "Good", or "Strong"
 */
const char *evaluate_strength(const char *password) {
    if (password == NULL || password[0] == '\0') {
        return "Weak";
    }

    bool has_lower = false;
    bool has_upper = false;
    bool has_digit = false;
    bool has_special = false;

    for (int i = 0; password[i] != '\0'; i++) {
        unsigned char c = (unsigned char)password[i];
        if (islower(c)) has_lower = true;
        else if (isupper(c)) has_upper = true;
        else if (isdigit(c)) has_digit = true;
        else has_special = true;
    }

    int categories = (has_lower ? 1 : 0) + (has_upper ? 1 : 0) +
                     (has_digit ? 1 : 0) + (has_special ? 1 : 0);
    size_t len = strlen(password);

    if (len >= 12 && categories == 4) {
        return "Strong";
    } else if (len >= 8 && categories >= 2) {
        return "Good";
    } else {
        return "Weak";
    }
}

/**
 * Trims leading and trailing whitespace characters in-place.
 */
static void trim(char *str) {
    if (!str) return;
    int len = (int)strlen(str);
    while (len > 0 && (str[len - 1] == '\r' || str[len - 1] == '\n' || isspace((unsigned char)str[len - 1]))) {
        str[--len] = '\0';
    }
    char *start = str;
    while (*start && isspace((unsigned char)*start)) {
        start++;
    }
    if (start != str) {
        memmove(str, start, strlen(start) + 1);
    }
}

/**
 * Prompts the user for desired password length with validation.
 *
 * @return Validated password length, or -1 on error
 */
int prompt_for_length(void) {
    char buffer[128];
    printf("Enter desired password length (minimum 4): ");
    fflush(stdout);

    if (fgets(buffer, sizeof(buffer), stdin) == NULL) {
        return -1;
    }

    trim(buffer);
    if (buffer[0] == '\0') {
        printf("Invalid input! Please enter a valid integer number.\n");
        return -1;
    }

    char *endptr = NULL;
    long length = strtol(buffer, &endptr, 10);
    if (*endptr != '\0') {
        printf("Invalid input! Please enter a valid integer number.\n");
        return -1;
    }

    if (length < 4) {
        printf("Warning: Password length should be at least 4 characters.\n");
        return -1;
    }

    return (int)length;
}

/**
 * Handles user interaction for generating a single password.
 */
void handle_generate_single(void) {
    int length = prompt_for_length();
    if (length <= 0) {
        printf("Operation cancelled or invalid length.\n");
        return;
    }

    char *password = generate_password(length);
    if (!password) return;

    printf("\nGenerated Password: %s\n", password);
    printf("Password Strength: %s\n", evaluate_strength(password));

    free(password);
}

/**
 * Handles batch password generation (5 distinct passwords).
 */
void handle_generate_batch(void) {
    int length = prompt_for_length();
    if (length <= 0) {
        printf("Operation cancelled or invalid length.\n");
        return;
    }

    char *passwords[5] = {0};
    int count = 0;

    while (count < 5) {
        char *pwd = generate_password(length);
        if (!pwd) break;

        // Ensure uniqueness within the batch
        bool duplicate = false;
        for (int i = 0; i < count; i++) {
            if (strcmp(passwords[i], pwd) == 0) {
                duplicate = true;
                break;
            }
        }

        if (duplicate) {
            free(pwd);
        } else {
            passwords[count++] = pwd;
        }
    }

    printf("\nGenerated Batch of 5 Passwords:\n");
    for (int i = 0; i < count; i++) {
        printf("%d. %s (Strength: %s)\n", i + 1, passwords[i], evaluate_strength(passwords[i]));
        free(passwords[i]);
    }
}

int main(void) {
    // Seed fallback pseudo-random number generator
    srand((unsigned int)time(NULL) ^ (unsigned int)clock());

    char line[128];
    bool running = true;

    printf("========================================\n");
    printf("     CLI PASSWORD GENERATOR (STARTER)   \n");
    printf("========================================\n");

    while (running) {
        printf("\nMenu Options:\n");
        printf("1. Generate Password\n");
        printf("2. Generate Batch of 5 Passwords\n");
        printf("3. Exit\n");
        printf("Select an option (1-3): ");
        fflush(stdout);

        if (fgets(line, sizeof(line), stdin) == NULL) {
            break;
        }

        trim(line);

        if (strcmp(line, "1") == 0) {
            handle_generate_single();
        } else if (strcmp(line, "2") == 0) {
            handle_generate_batch();
        } else if (strcmp(line, "3") == 0) {
            printf("Exiting Password Generator. Stay safe online!\n");
            running = false;
        } else {
            printf("Invalid option. Please enter 1, 2, or 3.\n");
        }
    }

    return 0;
}
