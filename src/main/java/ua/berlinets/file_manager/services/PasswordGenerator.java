package ua.berlinets.file_manager.services;

public class PasswordGenerator {
    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append((char) (Math.random() * 26 + 97));
        }
        return password.toString();
    }
}
