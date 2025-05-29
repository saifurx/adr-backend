package com.kasa.adr.util;

import java.util.Random;

public class PasswordGenerator {

    // Define the character sets to use in the password
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#&~";

    // Combine all character sets into one
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + DIGITS + SPECIAL_CHARS;

    // Random object to generate random indices
    private static final Random random = new Random();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(8);

        // Ensure at least one character from each character set
        password.append(getRandomChar(UPPER_CASE));
        password.append(getRandomChar(LOWER_CASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARS));

        // Fill the remaining 4 characters with random characters from all sets
        for (int i = 4; i < 8; i++) {
            password.append(getRandomChar(ALL_CHARS));
        }

        // Shuffle the password to ensure randomness
        return shuffleString(password.toString());
    }

    // Helper method to get a random character from a given string
    private static char getRandomChar(String charSet) {
        int randomIndex = random.nextInt(charSet.length());
        return charSet.charAt(randomIndex);
    }

    // Helper method to shuffle the characters in the password
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

    public static void main(String[] args) {
        // Example usage
        String password = generatePassword();
        System.out.println("Generated Password: " + password);
    }
}