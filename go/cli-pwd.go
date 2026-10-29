package main

import (
	"fmt"
	"math/rand"
)

func main() {
	var running bool = true
	fmt.Println("========================================")
	fmt.Println("     CLI PASSWORD GENERATOR (STARTER)   ")
	fmt.Println("========================================")

	for running {
		fmt.Println("\nMenu Options:")
		fmt.Println("1. Generate Password")
		fmt.Println("2. Generate Batch of 5 Passwords")
		fmt.Println("3. Exit")
		fmt.Print("Select an option (1-3): ")

		var choice int
		fmt.Scanln(&choice)

		switch choice {
		case 1:
			handlGenerateSingle()
		case 2:
			handleGenerateBatch()
		case 3:
			fmt.Println("Exiting Password Generator. Stay safe online!")
			running = false
		default:
			fmt.Println("Invalid option. Please enter 1, 2, or 3.")
		}
	}
}

func handlGenerateSingle() {
	var length int
	fmt.Println("Enter Password Length (minimum 1): ")
	fmt.Scanln(&length)

	if length <= 0 {
		fmt.Println("Operation cancelled or invalid length.")
		return
	}

	var password string = generatePassword(length)
	fmt.Printf("Generated Password: %s\n", password)

	var strength string = evalStrength(password)
	fmt.Printf("Password Strength: %s\n", strength)
}

func handleGenerateBatch() {
	var length int
	fmt.Println("Enter Password Length for Batch (minimum 1): ")
	fmt.Scanln(&length)

	if length <= 0 {
		fmt.Println("Operation cancelled or invalid length.")
		return
	}

	fmt.Println("Generated Passowords:")
	for i := 0; i < 5; i++ {
		var password string = generatePassword(length)
		var strength string = evalStrength(password)
		fmt.Printf("%d. %s (Strength: %s)\n", i+1, password, strength)
	}
}

func generatePassword(length int) string {
	var charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+"
	var password string

	for i := 0; i < length; i++ {
		randomIndex := rand.Intn(len(charset))
		password += string(charset[randomIndex])
	}
	return password
}

func evalStrength(password string) string {
	if password == "" || len(password) == 0 {
		return "weak"
	}
	var hasUpper, hasLower, hasDigit, hasSpecial bool
	for _, char := range password {
		switch {
		case char >= 'A' && char <= 'Z':
			hasUpper = true
		case char >= 'a' && char <= 'z':
			hasLower = true
		case char >= '0' && char <= '9':
			hasDigit = true
		case char >= '!' && char <= '/':
			hasSpecial = true
		}
	}
	categories := 0
	if hasUpper {
		categories++
	}
	if hasLower {
		categories++
	}
	if hasDigit {
		categories++
	}
	if hasSpecial {
		categories++
	}

	if categories == 4 && len(password) >= 12 {
		return "strong"
	} else if len(password) >= 8 && categories >= 3 {
		return "medium"
	} else {
		return "weak"
	}
}
