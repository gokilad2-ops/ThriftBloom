package service;

import dao.ItemDAO;
import dao.ItemRequestDAO;
import model.Item;
import model.ItemRequest;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class RequestService {

    private final ItemRequestDAO requestDAO;
    private final ItemDAO itemDAO;

    public RequestService() {
        this.requestDAO = new ItemRequestDAO();
        this.itemDAO = new ItemDAO();
    }

    public ItemRequest requestItem(int itemId, User requester, String notes) throws Exception {
        if (requester == null) {
            throw new IllegalStateException("You must be logged in to request an item.");
        }

        Item item = itemDAO.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found or has been removed.");
        }

        if (item.getOwnerId() == requester.getId()) {
            throw new IllegalArgumentException("You cannot request your own listing.");
        }

        if (!"AVAILABLE".equalsIgnoreCase(item.getStatus())) {
            throw new IllegalArgumentException("This item is currently " + item.getStatus() + " and cannot be requested.");
        }

        if (requestDAO.hasActiveRequest(itemId, requester.getId())) {
            throw new IllegalArgumentException("You already have an active request for this item.");
        }

        ItemRequest req = new ItemRequest();
        req.setItemId(itemId);
        req.setRequesterId(requester.getId());
        req.setStatus("PENDING");
        req.setNotes(notes != null ? notes.trim() : "");

        return requestDAO.createRequest(req);
    }

    public List<ItemRequest> getSentRequests(int requesterId) throws SQLException {
        return requestDAO.getSentRequests(requesterId);
    }

    public List<ItemRequest> getIncomingRequests(int ownerId) throws SQLException {
        return requestDAO.getIncomingRequests(ownerId);
    }

    public boolean approveRequest(int requestId, int itemId, int ownerId) throws SQLException {
        return requestDAO.approveRequestTransaction(requestId, itemId, ownerId);
    }

    public boolean rejectRequest(int requestId, int ownerId) throws SQLException {
        return requestDAO.rejectRequest(requestId, ownerId);
    }

    public boolean cancelSentRequest(int requestId, int requesterId) throws SQLException {
        return requestDAO.cancelSentRequest(requestId, requesterId);
    }

    public boolean completeRequest(int requestId, int itemId, int ownerId, String finalStatus) throws SQLException {
        return requestDAO.completeRequestTransaction(requestId, itemId, ownerId, finalStatus);
    }

    public int getPendingIncomingCount(int ownerId) throws SQLException {
        return requestDAO.getPendingIncomingCount(ownerId);
    }

    public int getSentRequestCount(int requesterId) throws SQLException {
        return requestDAO.getSentRequestCount(requesterId);
    }
}
