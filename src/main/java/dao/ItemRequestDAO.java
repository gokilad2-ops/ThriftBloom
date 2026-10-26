package dao;

import database.DatabaseConnection;
import model.ItemRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestDAO {

    public ItemRequest createRequest(ItemRequest req) throws SQLException {
        String sql = "INSERT INTO item_requests (item_id, requester_id, status, notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, req.getItemId());
            ps.setInt(2, req.getRequesterId());
            ps.setString(3, req.getStatus() != null ? req.getStatus() : "PENDING");
            ps.setString(4, req.getNotes());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    req.setId(rs.getInt(1));
                }
            }
            return req;
        }
    }

    public boolean hasActiveRequest(int itemId, int requesterId) throws SQLException {
        String sql = "SELECT 1 FROM item_requests WHERE item_id = ? AND requester_id = ? AND status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, requesterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<ItemRequest> getSentRequests(int requesterId) throws SQLException {
        String sql = "SELECT r.*, i.name AS item_name, i.price AS item_price, i.category AS item_category, " +
                     "i.listing_type AS item_listing_type, i.owner_id, " +
                     "owner.name AS owner_name, owner.registration_number AS owner_reg " +
                     "FROM item_requests r " +
                     "JOIN items i ON r.item_id = i.id " +
                     "JOIN users owner ON i.owner_id = owner.id " +
                     "WHERE r.requester_id = ? " +
                     "ORDER BY r.id DESC";

        List<ItemRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemRequest req = new ItemRequest(
                            rs.getInt("id"),
                            rs.getInt("item_id"),
                            rs.getInt("requester_id"),
                            rs.getString("status"),
                            rs.getString("notes"),
                            rs.getTimestamp("created_at")
                    );
                    req.setItemName(rs.getString("item_name"));
                    req.setItemPrice(rs.getDouble("item_price"));
                    req.setItemCategory(rs.getString("item_category"));
                    req.setItemListingType(rs.getString("item_listing_type"));
                    req.setOwnerId(rs.getInt("owner_id"));
                    req.setOwnerName(rs.getString("owner_name"));
                    req.setOwnerReg(rs.getString("owner_reg"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    public List<ItemRequest> getIncomingRequests(int ownerId) throws SQLException {
        String sql = "SELECT r.*, i.name AS item_name, i.price AS item_price, i.category AS item_category, " +
                     "i.listing_type AS item_listing_type, i.owner_id, " +
                     "req_user.name AS requester_name, req_user.registration_number AS requester_reg, " +
                     "req_user.email AS requester_email, req_user.phone AS requester_phone " +
                     "FROM item_requests r " +
                     "JOIN items i ON r.item_id = i.id " +
                     "JOIN users req_user ON r.requester_id = req_user.id " +
                     "WHERE i.owner_id = ? " +
                     "ORDER BY r.id DESC";

        List<ItemRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemRequest req = new ItemRequest(
                            rs.getInt("id"),
                            rs.getInt("item_id"),
                            rs.getInt("requester_id"),
                            rs.getString("status"),
                            rs.getString("notes"),
                            rs.getTimestamp("created_at")
                    );
                    req.setItemName(rs.getString("item_name"));
                    req.setItemPrice(rs.getDouble("item_price"));
                    req.setItemCategory(rs.getString("item_category"));
                    req.setItemListingType(rs.getString("item_listing_type"));
                    req.setOwnerId(rs.getInt("owner_id"));
                    req.setRequesterName(rs.getString("requester_name"));
                    req.setRequesterReg(rs.getString("requester_reg"));
                    req.setRequesterEmail(rs.getString("requester_email"));
                    req.setRequesterPhone(rs.getString("requester_phone"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    /**
     * Approves a request and marks the item RESERVED inside a database transaction.
     */
    public boolean approveRequestTransaction(int requestId, int itemId, int ownerId) throws SQLException {
        String verifySql = "SELECT 1 FROM items WHERE id = ? AND owner_id = ?";
        String updateReqSql = "UPDATE item_requests SET status = 'APPROVED' WHERE id = ?";
        String updateItemSql = "UPDATE items SET status = 'RESERVED' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Verify ownership
                try (PreparedStatement psCheck = conn.prepareStatement(verifySql)) {
                    psCheck.setInt(1, itemId);
                    psCheck.setInt(2, ownerId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // Update request status to APPROVED
                try (PreparedStatement psReq = conn.prepareStatement(updateReqSql)) {
                    psReq.setInt(1, requestId);
                    psReq.executeUpdate();
                }

                // Update item status to RESERVED
                try (PreparedStatement psItem = conn.prepareStatement(updateItemSql)) {
                    psItem.setInt(1, itemId);
                    psItem.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Rejects an incoming request.
     */
    public boolean rejectRequest(int requestId, int ownerId) throws SQLException {
        String sql = "UPDATE item_requests r " +
                     "JOIN items i ON r.item_id = i.id " +
                     "SET r.status = 'REJECTED' " +
                     "WHERE r.id = ? AND i.owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, ownerId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Marks request completed (and can update item status to SOLD/DONATED).
     */
    public boolean completeRequestTransaction(int requestId, int itemId, int ownerId, String finalItemStatus) throws SQLException {
        String updateReqSql = "UPDATE item_requests SET status = 'COMPLETED' WHERE id = ?";
        String updateItemSql = "UPDATE items SET status = ? WHERE id = ? AND owner_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psReq = conn.prepareStatement(updateReqSql)) {
                    psReq.setInt(1, requestId);
                    psReq.executeUpdate();
                }
                try (PreparedStatement psItem = conn.prepareStatement(updateItemSql)) {
                    psItem.setString(1, finalItemStatus);
                    psItem.setInt(2, itemId);
                    psItem.setInt(3, ownerId);
                    psItem.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean cancelSentRequest(int requestId, int requesterId) throws SQLException {
        String sql = "DELETE FROM item_requests WHERE id = ? AND requester_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, requesterId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getPendingIncomingCount(int ownerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM item_requests r JOIN items i ON r.item_id = i.id WHERE i.owner_id = ? AND r.status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int getSentRequestCount(int requesterId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM item_requests WHERE requester_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requesterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
