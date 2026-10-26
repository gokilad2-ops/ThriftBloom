package util;

import java.util.regex.Pattern;

/**
 * Validation utility for user input throughout the thrift application.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidRegNo(String regNo) {
        if (regNo == null) return false;
        String trimmed = regNo.trim();
        // Accepts diverse registration number formats (e.g. 2025501042, 23BCS045, CS-2024-01, etc.)
        return trimmed.length() >= 3 && trimmed.length() <= 50;
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        return name.trim().length() >= 2 && name.trim().length() <= 100;
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.length() >= 4;
    }

    public static boolean isNonNegativeDouble(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            double val = Double.parseDouble(str.trim());
            return val >= 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String clean(String input) {
        return input == null ? "" : input.trim();
    }
}
