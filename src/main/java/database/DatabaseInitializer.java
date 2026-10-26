package database;

import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automates creation of database schema and populates sample college marketplace data.
 */
public class DatabaseInitializer {

    public static boolean initialize() {
        // Step 1: Ensure database exists
        try (Connection serverConn = DatabaseConnection.getServerConnection();
             Statement stmt = serverConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS campus_thrift CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("✓ Database 'campus_thrift' verified.");
        } catch (SQLException e) {
            System.err.println("Warning while checking database existence: " + e.getMessage());
            // Proceed to see if campus_thrift already exists and is directly connectable
        }

        // Step 2: Create tables and indexes
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Table: users
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "  id INT AUTO_INCREMENT PRIMARY KEY," +
                    "  name VARCHAR(100) NOT NULL," +
                    "  registration_number VARCHAR(50) NOT NULL UNIQUE," +
                    "  email VARCHAR(100) NOT NULL UNIQUE," +
                    "  password_hash VARCHAR(255) NOT NULL," +
                    "  department VARCHAR(100) DEFAULT 'General'," +
                    "  year_of_study VARCHAR(50) DEFAULT '1st Year'," +
                    "  phone VARCHAR(20) DEFAULT ''," +
                    "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  INDEX idx_email (email)," +
                    "  INDEX idx_regno (registration_number)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // Table: items
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS items (" +
                    "  id INT AUTO_INCREMENT PRIMARY KEY," +
                    "  owner_id INT NOT NULL," +
                    "  name VARCHAR(150) NOT NULL," +
                    "  description TEXT NOT NULL," +
                    "  category VARCHAR(50) NOT NULL," +
                    "  item_condition VARCHAR(50) NOT NULL," +
                    "  listing_type VARCHAR(20) NOT NULL DEFAULT 'SELL'," +
                    "  price DECIMAL(10,2) DEFAULT 0.00," +
                    "  image_path VARCHAR(255) DEFAULT NULL," +
                    "  exchange_details TEXT DEFAULT NULL," +
                    "  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'," +
                    "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  CONSTRAINT fk_items_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "  INDEX idx_owner (owner_id)," +
                    "  INDEX idx_status (status)," +
                    "  INDEX idx_category (category)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // Table: item_requests
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS item_requests (" +
                    "  id INT AUTO_INCREMENT PRIMARY KEY," +
                    "  item_id INT NOT NULL," +
                    "  requester_id INT NOT NULL," +
                    "  status VARCHAR(20) NOT NULL DEFAULT 'PENDING'," +
                    "  notes TEXT DEFAULT NULL," +
                    "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  CONSTRAINT fk_requests_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE," +
                    "  CONSTRAINT fk_requests_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "  INDEX idx_item_req (item_id)," +
                    "  INDEX idx_requester (requester_id)," +
                    "  INDEX idx_req_status (status)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            System.out.println("✓ Tables 'users', 'items', 'item_requests' verified.");

            // Step 3: Populate sample seed data if users table is empty
            seedInitialData(conn);

            return true;
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void seedInitialData(Connection conn) throws SQLException {
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                // Already seeded
                return;
            }
        }

        System.out.println("Seeding sample college marketplace data into campus_thrift...");

        // Common demo password: student123 (hashed securely with PBKDF2)
        String demoPasswordHash = PasswordUtil.hashPassword("student123");

        // Insert sample users
        String insertUserSql = "INSERT INTO users (name, registration_number, email, password_hash, department, year_of_study, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int aadhyaId = 0, ishaanId = 0, meeraId = 0, kavinId = 0;

        try (PreparedStatement ps = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
            // User 1: Aadhya Raman
            ps.setString(1, "Aadhya Raman");
            ps.setString(2, "2025501042");
            ps.setString(3, "aadhya.raman@campus.edu");
            ps.setString(4, demoPasswordHash);
            ps.setString(5, "ECE");
            ps.setString(6, "2nd Year");
            ps.setString(7, "+91 98765 43210");
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) aadhyaId = rk.getInt(1); }

            // User 2: Ishaan Kumar
            ps.setString(1, "Ishaan Kumar");
            ps.setString(2, "2025502186");
            ps.setString(3, "ishaan.k@campus.edu");
            ps.setString(4, demoPasswordHash);
            ps.setString(5, "CSE");
            ps.setString(6, "3rd Year");
            ps.setString(7, "+91 91234 56780");
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) ishaanId = rk.getInt(1); }

            // User 3: Meera S.
            ps.setString(1, "Meera S.");
            ps.setString(2, "2025503077");
            ps.setString(3, "meera.s@campus.edu");
            ps.setString(4, demoPasswordHash);
            ps.setString(5, "Mathematics");
            ps.setString(6, "All Years");
            ps.setString(7, "+91 90000 12345");
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) meeraId = rk.getInt(1); }

            // User 4: Kavin Raj
            ps.setString(1, "Kavin Raj");
            ps.setString(2, "2025500916");
            ps.setString(3, "kavin.r@campus.edu");
            ps.setString(4, demoPasswordHash);
            ps.setString(5, "Mechanical");
            ps.setString(6, "2nd Year");
            ps.setString(7, "+91 94444 33221");
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) kavinId = rk.getInt(1); }
        }

        // Insert sample items
        String insertItemSql = "INSERT INTO items (owner_id, name, description, category, item_condition, listing_type, price, exchange_details, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int drawingKitId = 0;

        try (PreparedStatement ps = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
            // Item 1: Engineering Drawing Kit (Sell - Rs 300)
            ps.setInt(1, aadhyaId);
            ps.setString(2, "Engineering Drawing Kit");
            ps.setString(3, "A complete drawing kit with compass, set squares, mini-drafter clips, and all essentials. In great shape!");
            ps.setString(4, "Study Materials");
            ps.setString(5, "Good");
            ps.setString(6, "SELL");
            ps.setDouble(7, 300.0);
            ps.setString(8, null);
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) drawingKitId = rk.getInt(1); }

            // Item 2: Casio fx-991EX Scientific Calculator (Sell - Rs 450)
            ps.setInt(1, kavinId);
            ps.setString(2, "Scientific Calculator (Casio fx-991EX)");
            ps.setString(3, "Reliable scientific calculator with high-resolution LCD. Perfect for math and engineering coursework.");
            ps.setString(4, "Electronics");
            ps.setString(5, "Very good");
            ps.setString(6, "SELL");
            ps.setDouble(7, 450.0);
            ps.setString(8, null);
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();

            // Item 3: Lab Coat — Freshly Washed (Donate - Free)
            ps.setInt(1, meeraId);
            ps.setString(2, "Lab Coat — Clean & Freshly Washed");
            ps.setString(3, "White cotton lab coat (Medium size), washed and pressed. Free for any junior who needs it for chemistry or bio lab.");
            ps.setString(4, "Lab Equipment");
            ps.setString(5, "Good");
            ps.setString(6, "DONATE");
            ps.setDouble(7, 0.0);
            ps.setString(8, null);
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();

            // Item 4: Data Structures & Algorithms in Java (Sell - Rs 280)
            ps.setInt(1, ishaanId);
            ps.setString(2, "DSA in Java — Textbook & Problem Sets");
            ps.setString(3, "Covers Trees, Graphs, Dynamic Programming, and sorting algorithms. Minimal pencil notes, clean pages.");
            ps.setString(4, "Books");
            ps.setString(5, "Like new");
            ps.setString(6, "SELL");
            ps.setDouble(7, 280.0);
            ps.setString(8, null);
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();

            // Item 5: Java Complete Reference Book (Exchange)
            ps.setInt(1, aadhyaId);
            ps.setString(2, "Java: The Complete Reference (12th Ed)");
            ps.setString(3, "In depth reference for Java SE fundamentals, Swing, streams, and concurrency.");
            ps.setString(4, "Books");
            ps.setString(5, "Very good");
            ps.setString(6, "EXCHANGE");
            ps.setDouble(7, 0.0);
            ps.setString(8, "Seeking Operating Systems (Galvin) or Computer Networks (Tanenbaum)");
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();

            // Item 6: Engineering Mechanics & Record Notebook (Donate - Free)
            ps.setInt(1, kavinId);
            ps.setString(2, "Record Notebook & Mechanics Study Notes");
            ps.setString(3, "Bound notebook with blank record sheets and solved assignment questions from previous semester.");
            ps.setString(4, "Stationery");
            ps.setString(5, "Good");
            ps.setString(6, "DONATE");
            ps.setDouble(7, 0.0);
            ps.setString(8, null);
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();

            // Item 7: Campus Study Lamp in Blush (Sell - Rs 350)
            ps.setInt(1, meeraId);
            ps.setString(2, "Warm LED Desk Lamp in Blush Pink");
            ps.setString(3, "Compact flexible desk lamp with warm 3-level dimming. Perfect for hostel dorm tables.");
            ps.setString(4, "Other");
            ps.setString(5, "Like new");
            ps.setString(6, "SELL");
            ps.setDouble(7, 350.0);
            ps.setString(8, null);
            ps.setString(9, "AVAILABLE");
            ps.executeUpdate();
        }

        // Insert sample request
        if (drawingKitId > 0 && ishaanId > 0) {
            String insertReqSql = "INSERT INTO item_requests (item_id, requester_id, status, notes) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertReqSql)) {
                ps.setInt(1, drawingKitId);
                ps.setInt(2, ishaanId);
                ps.setString(3, "PENDING");
                ps.setString(4, "Hi Aadhya, I need this drawing kit for my engineering graphics class this semester! Can we meet at the campus library?");
                ps.executeUpdate();
            }
        }

        System.out.println("✓ Sample college thrift data seeded successfully.");
    }
}
