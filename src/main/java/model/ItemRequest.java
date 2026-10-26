package model;

import java.sql.Timestamp;

public class ItemRequest {
    private int id;
    private int itemId;
    private int requesterId;
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED
    private String notes;
    private Timestamp createdAt;

    // Transient fields for easy table and card display
    private String itemName;
    private double itemPrice;
    private String itemCategory;
    private String itemListingType;
    private int ownerId;
    private String ownerName;
    private String ownerReg;
    private String requesterName;
    private String requesterReg;
    private String requesterEmail;
    private String requesterPhone;

    public ItemRequest() {
        this.status = "PENDING";
    }

    public ItemRequest(int id, int itemId, int requesterId, String status, String notes, Timestamp createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.requesterId = requesterId;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }

    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public String getItemListingType() { return itemListingType; }
    public void setItemListingType(String itemListingType) { this.itemListingType = itemListingType; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerReg() { return ownerReg; }
    public void setOwnerReg(String ownerReg) { this.ownerReg = ownerReg; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterReg() { return requesterReg; }
    public void setRequesterReg(String requesterReg) { this.requesterReg = requesterReg; }

    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }

    public String getRequesterPhone() { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone) { this.requesterPhone = requesterPhone; }
}
