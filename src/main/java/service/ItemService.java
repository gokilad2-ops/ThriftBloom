package service;

import dao.ItemDAO;
import model.Item;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class ItemService {

    private final ItemDAO itemDAO;

    public ItemService() {
        this.itemDAO = new ItemDAO();
    }

    public Item addItem(String name, String description, String category, String condition,
                        String listingType, double price, String exchangeDetails,
                        String imagePath, User owner) throws Exception {

        if (owner == null) {
            throw new IllegalStateException("You must be logged in to post an item.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name / title is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Please provide a description of the item.");
        }
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("Choose a shelf")) {
            throw new IllegalArgumentException("Please select a category for your item.");
        }
        if (condition == null || condition.trim().isEmpty()) {
            condition = "Good";
        }
        if (listingType == null || listingType.trim().isEmpty()) {
            listingType = "SELL";
        }

        String typeUpper = listingType.trim().toUpperCase();

        if ("SELL".equals(typeUpper)) {
            if (price <= 0) {
                throw new IllegalArgumentException("Items for sale must have a positive price (> ₹0). For free items, choose 'DONATE'.");
            }
        } else if ("DONATE".equals(typeUpper)) {
            price = 0.0;
        } else if ("EXCHANGE".equals(typeUpper)) {
            if (exchangeDetails == null || exchangeDetails.trim().isEmpty()) {
                throw new IllegalArgumentException("Please specify what you would like in exchange (e.g., textbook name or equipment).");
            }
        }

        Item item = new Item();
        item.setOwnerId(owner.getId());
        item.setName(name.trim());
        item.setDescription(description.trim());
        item.setCategory(category.trim());
        item.setItemCondition(condition.trim());
        item.setListingType(typeUpper);
        item.setPrice(price);
        item.setExchangeDetails(exchangeDetails != null ? exchangeDetails.trim() : null);
        item.setImagePath(imagePath != null && !imagePath.trim().isEmpty() ? imagePath.trim() : null);
        item.setStatus("AVAILABLE");

        return itemDAO.createItem(item);
    }

    public List<Item> searchAvailableItems(String query, String category, String listingType, String sortBy) throws SQLException {
        return itemDAO.searchAvailableItems(query, category, listingType, sortBy);
    }

    public List<Item> getMyListings(int ownerId) throws SQLException {
        return itemDAO.getItemsByOwnerId(ownerId);
    }

    public Item getItemById(int id) throws SQLException {
        return itemDAO.findById(id);
    }

    public boolean updateItem(Item item, int currentUserId) throws Exception {
        if (item.getOwnerId() != currentUserId) {
            throw new SecurityException("Unauthorized: You cannot modify another student's listing.");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required.");
        }
        if ("SELL".equalsIgnoreCase(item.getListingType()) && item.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero for items listed for sale.");
        }
        return itemDAO.updateItem(item);
    }

    public boolean updateStatus(int itemId, int currentUserId, String newStatus) throws SQLException {
        return itemDAO.updateItemStatusByOwner(itemId, currentUserId, newStatus);
    }

    public boolean deleteItem(int itemId, int currentUserId) throws SQLException {
        return itemDAO.deleteItem(itemId, currentUserId);
    }

    public int getAvailableCount() throws SQLException {
        return itemDAO.getAvailableCount();
    }

    public int getFreeCount() throws SQLException {
        return itemDAO.getFreeCount();
    }

    public int getOwnerListingCount(int ownerId) throws SQLException {
        return itemDAO.getOwnerListingCount(ownerId);
    }
}
