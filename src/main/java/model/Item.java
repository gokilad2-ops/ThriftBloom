package model;

import java.sql.Timestamp;

public class Item {
    private int id;
    private int ownerId;
    private String name;
    private String description;
    private String category;
    private String itemCondition;
    private String listingType; // SELL, DONATE, EXCHANGE
    private double price;
    private String imagePath;
    private String exchangeDetails;
    private String status; // AVAILABLE, RESERVED, SOLD, DONATED
    private Timestamp createdAt;

    // Transient fields populated from JOIN queries for UI convenience
    private String ownerName;
    private String ownerReg;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerDepartment;

    public Item() {
        this.status = "AVAILABLE";
        this.listingType = "SELL";
        this.price = 0.0;
    }

    public Item(int id, int ownerId, String name, String description, String category,
                String itemCondition, String listingType, double price, String imagePath,
                String exchangeDetails, String status, Timestamp createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.itemCondition = itemCondition;
        this.listingType = listingType;
        this.price = price;
        this.imagePath = imagePath;
        this.exchangeDetails = exchangeDetails;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getItemCondition() { return itemCondition; }
    public void setItemCondition(String itemCondition) { this.itemCondition = itemCondition; }

    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getExchangeDetails() { return exchangeDetails; }
    public void setExchangeDetails(String exchangeDetails) { this.exchangeDetails = exchangeDetails; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerReg() { return ownerReg; }
    public void setOwnerReg(String ownerReg) { this.ownerReg = ownerReg; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getOwnerDepartment() { return ownerDepartment; }
    public void setOwnerDepartment(String ownerDepartment) { this.ownerDepartment = ownerDepartment; }

    public String getPriceFormatted() {
        if ("DONATE".equalsIgnoreCase(listingType) || price == 0) {
            return "FREE";
        } else if ("EXCHANGE".equalsIgnoreCase(listingType)) {
            return "EXCHANGE";
        } else {
            return String.format("₹%.2f", price);
        }
    }

    @Override
    public String toString() {
        return name + " (" + getPriceFormatted() + ")";
    }
}
