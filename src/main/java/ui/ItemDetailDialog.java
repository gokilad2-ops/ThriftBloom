package ui;

import model.Item;
import model.User;
import service.RequestService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modal dialog to view complete details of a marketplace item
 * and initiate an item request.
 */
public class ItemDetailDialog extends JDialog {

    private final Item item;
    private final User currentUser;
    private final RequestService requestService;
    private final Runnable onRequestSuccess;

    public ItemDetailDialog(Frame parent, Item item, User currentUser, Runnable onRequestSuccess) {
        super(parent, "thrift — Item Details", true);
        this.item = item;
        this.currentUser = currentUser;
        this.requestService = new RequestService();
        this.onRequestSuccess = onRequestSuccess;

        setSize(560, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_CREAM);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.COLOR_CREAM);
        content.setBorder(new EmptyBorder(22, 24, 22, 24));

        // Top badges row (Status + Listing Type)
        JPanel topBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topBadges.setOpaque(false);
        topBadges.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusBadge = UITheme.createStatusBadge(item.getStatus());
        JLabel typeBadge = UITheme.createBadge(
                item.getListingType(),
                UITheme.COLOR_ROSE,
                Color.WHITE
        );
        topBadges.add(statusBadge);
        topBadges.add(typeBadge);
        content.add(topBadges);
        content.add(Box.createVerticalStrut(10));

        // Item Title
        JLabel titleLbl = new JLabel("<html>" + item.getName() + "</html>");
        titleLbl.setFont(UITheme.FONT_HEADER_2);
        titleLbl.setForeground(UITheme.COLOR_ROSE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLbl);
        content.add(Box.createVerticalStrut(6));

        // Price
        JLabel priceLbl = new JLabel(item.getPriceFormatted());
        priceLbl.setFont(new Font("Georgia", Font.BOLD, 22));
        priceLbl.setForeground(UITheme.COLOR_RASPBERRY);
        priceLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(priceLbl);
        content.add(Box.createVerticalStrut(12));

        // Facts Grid (Category, Condition, Listing Date)
        JPanel factsPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        factsPanel.setOpaque(false);
        factsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        factsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        factsPanel.add(createFactBox("CATEGORY", item.getCategory()));
        factsPanel.add(createFactBox("CONDITION", item.getItemCondition()));
        factsPanel.add(createFactBox("LISTING TYPE", item.getListingType()));
        content.add(factsPanel);
        content.add(Box.createVerticalStrut(14));

        // Description
        JLabel descHeader = new JLabel("DESCRIPTION");
        descHeader.setFont(UITheme.FONT_KICKER);
        descHeader.setForeground(UITheme.COLOR_MUTED);
        descHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(descHeader);
        content.add(Box.createVerticalStrut(4));

        JTextArea descArea = new JTextArea(item.getDescription());
        descArea.setFont(UITheme.FONT_BODY);
        descArea.setForeground(UITheme.COLOR_INK);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(UITheme.COLOR_CARD_BG);
        descArea.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setPreferredSize(new Dimension(500, 75));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        descScroll.setBorder(null);
        content.add(descScroll);
        content.add(Box.createVerticalStrut(12));

        // Exchange Details (if applicable)
        if ("EXCHANGE".equalsIgnoreCase(item.getListingType()) && item.getExchangeDetails() != null) {
            JLabel exHeader = new JLabel("EXCHANGE REQUIREMENTS");
            exHeader.setFont(UITheme.FONT_KICKER);
            exHeader.setForeground(UITheme.COLOR_GOLD);
            exHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(exHeader);
            content.add(Box.createVerticalStrut(4));

            JLabel exLabel = new JLabel("<html>" + item.getExchangeDetails() + "</html>");
            exLabel.setFont(UITheme.FONT_BODY);
            exLabel.setForeground(UITheme.COLOR_INK);
            exLabel.setBorder(new CompoundBorder(
                    new LineBorder(UITheme.COLOR_GOLD, 1, true),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            exLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(exLabel);
            content.add(Box.createVerticalStrut(12));
        }

        // Seller Contact Card
        JPanel sellerCard = new JPanel();
        sellerCard.setLayout(new BoxLayout(sellerCard, BoxLayout.Y_AXIS));
        sellerCard.setBackground(UITheme.COLOR_BLUSH);
        sellerCard.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        sellerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel sellerHeader = new JLabel("SELLER INFORMATION");
        sellerHeader.setFont(UITheme.FONT_KICKER);
        sellerHeader.setForeground(UITheme.COLOR_ROSE);

        String ownerName = item.getOwnerName() != null ? item.getOwnerName() : "Campus Student";
        String ownerReg = item.getOwnerReg() != null ? item.getOwnerReg() : "N/A";
        String ownerEmail = item.getOwnerEmail() != null ? item.getOwnerEmail() : "Not provided";
        String ownerPhone = item.getOwnerPhone() != null && !item.getOwnerPhone().isEmpty() ? item.getOwnerPhone() : "Not provided";
        String ownerDept = item.getOwnerDepartment() != null ? item.getOwnerDepartment() : "General";

        JLabel sellerNameLbl = new JLabel("✿ " + ownerName + " (Reg: " + ownerReg + ")");
        sellerNameLbl.setFont(UITheme.FONT_BODY_BOLD);
        sellerNameLbl.setForeground(UITheme.COLOR_ROSE);

        JLabel sellerInfoLbl = new JLabel("<html>Dept: " + ownerDept + " &nbsp;|&nbsp; Email: " + ownerEmail + "<br>Phone: " + ownerPhone + "</html>");
        sellerInfoLbl.setFont(UITheme.FONT_SMALL);
        sellerInfoLbl.setForeground(UITheme.COLOR_INK);
        sellerInfoLbl.setBorder(new EmptyBorder(4, 0, 0, 0));

        sellerCard.add(sellerHeader);
        sellerCard.add(Box.createVerticalStrut(4));
        sellerCard.add(sellerNameLbl);
        sellerCard.add(sellerInfoLbl);
        content.add(sellerCard);

        add(new JScrollPane(content), BorderLayout.CENTER);

        // Action Buttons Bar
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionsBar.setBackground(UITheme.COLOR_CREAM);
        actionsBar.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1),
                new EmptyBorder(4, 16, 4, 16)
        ));

        JButton btnClose = UITheme.createSecondaryButton("Close");
        btnClose.addActionListener(e -> dispose());

        JButton btnRequest = UITheme.createPrimaryButton("♡ Request This Item");

        boolean isOwnItem = currentUser != null && currentUser.getId() == item.getOwnerId();
        boolean isAvailable = "AVAILABLE".equalsIgnoreCase(item.getStatus());

        if (isOwnItem) {
            btnRequest.setEnabled(false);
            btnRequest.setText("Your Own Listing");
            btnRequest.setToolTipText("You cannot request your own item.");
        } else if (!isAvailable) {
            btnRequest.setEnabled(false);
            btnRequest.setText("Item " + item.getStatus());
            btnRequest.setToolTipText("This item is not available for new requests.");
        } else {
            btnRequest.addActionListener(e -> handleRequest());
        }

        actionsBar.add(btnClose);
        actionsBar.add(btnRequest);
        add(actionsBar, BorderLayout.SOUTH);
    }

    private JPanel createFactBox(String label, String value) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(UITheme.COLOR_CARD_BG);
        box.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_KICKER);
        lbl.setForeground(UITheme.COLOR_MUTED);

        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(UITheme.FONT_BODY_BOLD);
        val.setForeground(UITheme.COLOR_ROSE);

        box.add(lbl);
        box.add(Box.createVerticalStrut(2));
        box.add(val);
        return box;
    }

    private void handleRequest() {
        String note = JOptionPane.showInputDialog(
                this,
                "Add an optional message or meet-up note for " + (item.getOwnerName() != null ? item.getOwnerName() : "the seller") + ":",
                "Request Item — " + item.getName(),
                JOptionPane.QUESTION_MESSAGE
        );

        if (note == null) {
            // User cancelled prompt
            return;
        }

        try {
            requestService.requestItem(item.getId(), currentUser, note);
            JOptionPane.showMessageDialog(
                    this,
                    "Your request for \"" + item.getName() + "\" has been sent to the owner!\nYou can track it in 'My Requests'.",
                    "Request Sent ✿",
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (onRequestSuccess != null) {
                onRequestSuccess.run();
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Could Not Request Item",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
