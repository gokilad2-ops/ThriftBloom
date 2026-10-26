package dao;

import database.DatabaseConnection;
import model.User;

import java.sql.*;

public class UserDAO {

    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, registration_number, email, password_hash, department, year_of_study, phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getRegistrationNumber());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getDepartment() != null ? user.getDepartment() : "General");
            ps.setString(6, user.getYearOfStudy() != null ? user.getYearOfStudy() : "1st Year");
            ps.setString(7, user.getPhone() != null ? user.getPhone() : "");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            return user;
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public User findByEmailOrRegNo(String identifier) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?) OR LOWER(registration_number) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier.trim());
            ps.setString(2, identifier.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean regNoExists(String regNo) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE LOWER(registration_number) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, department = ?, year_of_study = ?, phone = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getDepartment());
            ps.setString(3, user.getYearOfStudy());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("registration_number"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("department"),
                rs.getString("year_of_study"),
                rs.getString("phone"),
                rs.getTimestamp("created_at")
        );
    }
}
