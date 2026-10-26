package dao;

import database.DatabaseConnection;
import model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public Item createItem(Item item) throws SQLException {
        String sql = "INSERT INTO items (owner_id, name, description, category, item_condition, listing_type, price, image_path, exchange_details, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, item.getOwnerId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getItemCondition());
            ps.setString(6, item.getListingType());
            ps.setDouble(7, item.getPrice());
            ps.setString(8, item.getImagePath());
            ps.setString(9, item.getExchangeDetails());
            ps.setString(10, item.getStatus() != null ? item.getStatus() : "AVAILABLE");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
            return item;
        }
    }

    public Item findById(int id) throws SQLException {
        String sql = "SELECT i.*, u.name AS owner_name, u.registration_number AS owner_reg, " +
                     "u.email AS owner_email, u.phone AS owner_phone, u.department AS owner_dept " +
                     "FROM items i " +
                     "JOIN users u ON i.owner_id = u.id " +
                     "WHERE i.id = ?";
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

    public List<Item> searchAvailableItems(String query, String category, String listingType, String sortBy) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT i.*, u.name AS owner_name, u.registration_number AS owner_reg, " +
                "u.email AS owner_email, u.phone AS owner_phone, u.department AS owner_dept " +
                "FROM items i " +
                "JOIN users u ON i.owner_id = u.id " +
                "WHERE i.status = 'AVAILABLE' "
        );

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(i.name) LIKE ? OR LOWER(i.description) LIKE ? OR LOWER(i.category) LIKE ?) ");
            String q = "%" + query.trim().toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("All categories")) {
            sql.append("AND i.category = ? ");
            params.add(category.trim());
        }

        if (listingType != null && !listingType.trim().isEmpty() && !listingType.equalsIgnoreCase("All") && !listingType.equalsIgnoreCase("Sell + donate")) {
            sql.append("AND i.listing_type = ? ");
            params.add(listingType.trim().toUpperCase());
        }

        if ("PRICE_LOW".equalsIgnoreCase(sortBy) || "priceLow".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY i.price ASC, i.id DESC");
        } else if ("PRICE_HIGH".equalsIgnoreCase(sortBy) || "priceHigh".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY i.price DESC, i.id DESC");
        } else {
            sql.append("ORDER BY i.id DESC");
        }

        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }
        return items;
    }

    public List<Item> getItemsByOwnerId(int ownerId) throws SQLException {
        String sql = "SELECT i.*, u.name AS owner_name, u.registration_number AS owner_reg, " +
                     "u.email AS owner_email, u.phone AS owner_phone, u.department AS owner_dept " +
                     "FROM items i " +
                     "JOIN users u ON i.owner_id = u.id " +
                     "WHERE i.owner_id = ? " +
                     "ORDER BY i.id DESC";
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }
        return items;
    }

    public boolean updateItem(Item item) throws SQLException {
        String sql = "UPDATE items SET name = ?, description = ?, category = ?, item_condition = ?, " +
                     "listing_type = ?, price = ?, exchange_details = ?, image_path = ? " +
                     "WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getItemCondition());
            ps.setString(5, item.getListingType());
            ps.setDouble(6, item.getPrice());
            ps.setString(7, item.getExchangeDetails());
            ps.setString(8, item.getImagePath());
            ps.setInt(9, item.getId());
            ps.setInt(10, item.getOwnerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateItemStatus(int itemId, String newStatus) throws SQLException {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateItemStatusByOwner(int itemId, int ownerId, String newStatus) throws SQLException {
        String sql = "UPDATE items SET status = ? WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, itemId);
            ps.setInt(3, ownerId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(int itemId, int ownerId) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, ownerId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getAvailableCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'AVAILABLE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getFreeCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'AVAILABLE' AND (price = 0 OR listing_type = 'DONATE')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getOwnerListingCount(int ownerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM items WHERE owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item(
                rs.getInt("id"),
                rs.getInt("owner_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("item_condition"),
                rs.getString("listing_type"),
                rs.getDouble("price"),
                rs.getString("image_path"),
                rs.getString("exchange_details"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
        // Map joined owner details
        try {
            item.setOwnerName(rs.getString("owner_name"));
            item.setOwnerReg(rs.getString("owner_reg"));
            item.setOwnerEmail(rs.getString("owner_email"));
            item.setOwnerPhone(rs.getString("owner_phone"));
            item.setOwnerDepartment(rs.getString("owner_dept"));
        } catch (SQLException ignored) {
            // In case columns were not projected
        }
        return item;
    }
}
