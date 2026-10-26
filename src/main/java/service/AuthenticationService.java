package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

import java.sql.SQLException;

public class AuthenticationService {

    private final UserDAO userDAO;
    private static User currentUser = null;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    public User login(String identifier, String password) throws Exception {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter your registration number or email address.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter your password.");
        }

        User user = userDAO.findByEmailOrRegNo(identifier.trim());
        if (user == null) {
            throw new IllegalArgumentException("No student account found with this email or registration number.");
        }

        boolean verified = PasswordUtil.verifyPassword(password, user.getPasswordHash());
        if (!verified) {
            throw new IllegalArgumentException("Invalid password. Please check your credentials and try again.");
        }

        currentUser = user;
        return user;
    }

    public User register(String name, String regNo, String email, String password,
                         String confirmPassword, String department, String yearOfStudy, String phone) throws Exception {

        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Please enter a valid student name (at least 2 characters).");
        }
        if (!ValidationUtil.isValidRegNo(regNo)) {
            throw new IllegalArgumentException("Please enter a valid college registration number (minimum 3 characters).");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address (e.g., student@campus.edu).");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match. Please verify both password fields.");
        }

        if (userDAO.regNoExists(regNo)) {
            throw new IllegalArgumentException("Registration number '" + regNo.trim() + "' is already registered. Please log in or use your own ID.");
        }
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email address '" + email.trim() + "' is already in use.");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User newUser = new User(
                name.trim(),
                regNo.trim(),
                email.trim(),
                hashedPassword,
                department != null ? department.trim() : "General",
                yearOfStudy != null ? yearOfStudy.trim() : "1st Year",
                phone != null ? phone.trim() : ""
        );

        User created = userDAO.createUser(newUser);
        return created;
    }

    public boolean updateProfile(User user) throws SQLException {
        boolean updated = userDAO.updateProfile(user);
        if (updated && currentUser != null && currentUser.getId() == user.getId()) {
            currentUser = user;
        }
        return updated;
    }
}
