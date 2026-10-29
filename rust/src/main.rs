use std::io;
use std::sync::atomic::{AtomicU64, Ordering};
use std::time::{SystemTime, UNIX_EPOCH};

static RANDOM_STATE: AtomicU64 = AtomicU64::new(0);

fn random_index(max: u32) -> u32 {
    let timestamp = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default()
        .as_nanos() as u64;
    let state = RANDOM_STATE.fetch_add(1, Ordering::Relaxed) ^ timestamp;
    state.wrapping_mul(6364136223846793005).wrapping_add(1) as u32 % max
}

fn main() {
    let running = true;
 	println!("========================================");
	println!("     CLI PASSWORD GENERATOR (STARTER)   ");
	println!("========================================");

    while running {
        println!("\nMenu Options:");
		println!("1. Generate Password");
		println!("2. Generate Batch of 5 Passwords");
		println!("3. Exit");
		print!("Select an option (1-3): ");

        let mut choice = String::new();

        io::stdin().read_line(&mut choice).expect("failed to readline");

        match choice.trim() {
            "1" => {
                handle_generate_single();
            }
            "2" => {
                handle_generate_batch();
            }
            "3" => {
                println!("Exiting the program. Take Care!");
                break;
            }
            _ => {
                println!("Invalid option. Please select a valid option.");
            }
        }

    }
}


fn handle_generate_single(){
    let mut length_input = String::new();
    println!("Enter the desired password length: ");
    io::stdin().read_line(&mut length_input).expect("Failed to read line");
    let length: i32 = match length_input.trim().parse() {
        Ok(num) => num,
        Err(_) => {
            println!("Invalid input. Please enter a valid number.");
            return;
        }
    };

    let password = generate_password(length);
    let strength = eval_strength(&password);
    println!("Generated Password: {} (Strength: {})", password, strength);
}


fn handle_generate_batch(){
    let mut length_input = String::new();
    println!("Enter the desired password length: ");
    io::stdin().read_line(&mut length_input).expect("Failed to read line");
    let length: i32 = match length_input.trim().parse() {
        Ok(num) => num,
        Err(_) => {
            println!("Invalid input. Please enter a valid number.");
            return;
        }
    };

    for _ in 0..5 {
        let password = generate_password(length);
        let strength = eval_strength(&password);
        println!("Generated Password: {} (Strength: {})", password, strength);
    }
}

fn generate_password(length:i32) -> String {
    
    let charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    let mut password = String::new();
    let charset_len = charset.len() as u32;

    for _ in 0..length {
        let index = random_index(charset_len);
        password.push(charset.chars().nth(index as usize).unwrap());
    }

    password
}

fn eval_strength(password: &str) -> String {
    if password == "" {
        "weak".to_string()
    } else{
        let mut has_uppercase = false;
        let mut has_lowercase = false;
        let mut has_digit = false;
        let mut has_special = false;

        for c in password.chars() {
            if c.is_uppercase() {
                has_uppercase = true;
            } else if c.is_lowercase() {
                has_lowercase = true;
            } else if c.is_digit(10) {
                has_digit = true;
            } else if !c.is_alphanumeric() {
                has_special = true;
            }
        }

        if password.len() >= 8 && has_uppercase && has_lowercase && has_digit && has_special {
            "strong".to_string()
        } else if password.len() >= 6 && (has_uppercase || has_lowercase) && (has_digit || has_special) {
            "medium".to_string()
        } else {
            "weak".to_string()
        }
    }
}

