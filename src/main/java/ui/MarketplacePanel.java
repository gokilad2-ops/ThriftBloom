package ui;

import model.Item;
import model.User;
import service.ItemService;
import service.RequestService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * The main student-to-student marketplace view.
 * Offers keyword search, category filtering, listing type filtering,
 * price sorting, and attractive item cards with View Details and Request actions.
 */
public class MarketplacePanel extends JPanel {

    private final User currentUser;
    private final ItemService itemService;
    private final RequestService requestService;

    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> typeCombo;
    private JComboBox<String> sortCombo;
    private JLabel resultsCountLabel;
    private JPanel gridPanel;

    public MarketplacePanel(User currentUser) {
        this.currentUser = currentUser;
        this.itemService = new ItemService();
        this.requestService = new RequestService();

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_PAPER);

        initComponents();
        loadItems();
    }

    private void initComponents() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(16, 24, 10, 24));

        // Section Title
        JPanel headRow = new JPanel(new BorderLayout());
        headRow.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel kicker = new JLabel("THE TREASURE SHELF");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("Get the good stuff ✿");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel subtitle = new JLabel("Search, filter and find affordable essentials shared by campus peers.");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.COLOR_MUTED);

        titles.add(kicker);
        titles.add(title);
        titles.add(subtitle);

        resultsCountLabel = UITheme.createStatusBadge("0 ITEMS");
        headRow.add(titles, BorderLayout.WEST);
        headRow.add(resultsCountLabel, BorderLayout.EAST);
        topContainer.add(headRow);
        topContainer.add(Box.createVerticalStrut(14));

        // Search & Filter Toolbar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.setBackground(UITheme.COLOR_CREAM);
        filterBar.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        // Search field
        JLabel lblSearch = new JLabel("⌕ Search:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        lblSearch.setForeground(UITheme.COLOR_ROSE);

        searchField = UITheme.createTextField(14);
        searchField.setPreferredSize(new Dimension(180, 34));
        searchField.addActionListener(e -> loadItems());

        // Category dropdown
        String[] categories = {
                "All categories", "Books", "Electronics", "Study Materials",
                "Lab Equipment", "Stationery", "Clothing", "Furniture", "Other"
        };
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(UITheme.FONT_BODY);
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setPreferredSize(new Dimension(145, 34));
        categoryCombo.addActionListener(e -> loadItems());

        // Type dropdown
        String[] types = {"All types", "SELL", "DONATE", "EXCHANGE"};
        typeCombo = new JComboBox<>(types);
        typeCombo.setFont(UITheme.FONT_BODY);
        typeCombo.setBackground(Color.WHITE);
        typeCombo.setPreferredSize(new Dimension(115, 34));
        typeCombo.addActionListener(e -> loadItems());

        // Sort dropdown
        String[] sorts = {"Sort: Featured", "Price: Low to High", "Price: High to Low"};
        sortCombo = new JComboBox<>(sorts);
        sortCombo.setFont(UITheme.FONT_BODY);
        sortCombo.setBackground(Color.WHITE);
        sortCombo.setPreferredSize(new Dimension(150, 34));
        sortCombo.addActionListener(e -> loadItems());

        // Filter & Refresh buttons
        JButton btnFilter = UITheme.createPrimaryButton("Search");
        btnFilter.addActionListener(e -> loadItems());

        JButton btnReset = UITheme.createSoftButton("Reset");
        btnReset.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            typeCombo.setSelectedIndex(0);
            sortCombo.setSelectedIndex(0);
            loadItems();
        });

        filterBar.add(lblSearch);
        filterBar.add(searchField);
        filterBar.add(categoryCombo);
        filterBar.add(typeCombo);
        filterBar.add(sortCombo);
        filterBar.add(btnFilter);
        filterBar.add(btnReset);

        topContainer.add(filterBar);
        add(topContainer, BorderLayout.NORTH);

        // Grid Panel for Cards
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridBagLayout());
        gridPanel.setBackground(UITheme.COLOR_PAPER);
        gridPanel.setBorder(new EmptyBorder(10, 24, 24, 24));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setCategoryFilter(String category) {
        if (category == null) return;
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).equalsIgnoreCase(category)) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
        loadItems();
    }

    public void loadItems() {
        String query = searchField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String listingType = (String) typeCombo.getSelectedItem();
        int sortIdx = sortCombo.getSelectedIndex();

        String sortOrder = "NEWEST";
        if (sortIdx == 1) sortOrder = "PRICE_LOW";
        if (sortIdx == 2) sortOrder = "PRICE_HIGH";
        final String finalSortBy = sortOrder;

        SwingUtilities.invokeLater(() -> {
            try {
                List<Item> items = itemService.searchAvailableItems(query, category, listingType, finalSortBy);
                resultsCountLabel.setText(items.size() + " ITEMS FOUND");

                gridPanel.removeAll();
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 0.0;

                if (items.isEmpty()) {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
                    emptyPanel.setBackground(UITheme.COLOR_PAPER);
                    emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));

                    JLabel icon = new JLabel("✿", SwingConstants.CENTER);
                    icon.setFont(new Font("Georgia", Font.PLAIN, 48));
                    icon.setForeground(UITheme.COLOR_LINE);
                    icon.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel h = new JLabel("No treasures found just yet", SwingConstants.CENTER);
                    h.setFont(UITheme.FONT_HEADER_2);
                    h.setForeground(UITheme.COLOR_ROSE);
                    h.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel sub = new JLabel("Try searching with different keywords, or check another category shelf.", SwingConstants.CENTER);
                    sub.setFont(UITheme.FONT_BODY);
                    sub.setForeground(UITheme.COLOR_MUTED);
                    sub.setAlignmentX(Component.CENTER_ALIGNMENT);

                    emptyPanel.add(icon);
                    emptyPanel.add(Box.createVerticalStrut(10));
                    emptyPanel.add(h);
                    emptyPanel.add(Box.createVerticalStrut(5));
                    emptyPanel.add(sub);

                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.gridwidth = 3;
                    gridPanel.add(emptyPanel, gbc);
                } else {
                    int columns = 3;
                    for (int i = 0; i < items.size(); i++) {
                        Item item = items.get(i);
                        gbc.gridx = i % columns;
                        gbc.gridy = i / columns;
                        gbc.gridwidth = 1;
                        gridPanel.add(createItemCard(item), gbc);
                    }
                }

                gridPanel.revalidate();
                gridPanel.repaint();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load marketplace items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JPanel createItemCard(Item item) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 240));

        // Badges: Category + Listing Type
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        badges.setOpaque(false);
        badges.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel catBadge = UITheme.createBadge(item.getCategory(), UITheme.COLOR_BLUSH, UITheme.COLOR_ROSE);
        JLabel typeBadge = UITheme.createBadge(
                item.getListingType(),
                "DONATE".equalsIgnoreCase(item.getListingType()) ? UITheme.COLOR_GOLD : UITheme.COLOR_ROSE,
                Color.WHITE
        );
        badges.add(catBadge);
        badges.add(typeBadge);
        card.add(badges);
        card.add(Box.createVerticalStrut(8));

        // Title
        JLabel title = new JLabel("<html><b>" + item.getName() + "</b></html>");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.COLOR_ROSE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(4));

        // Price
        JLabel price = new JLabel(item.getPriceFormatted());
        price.setFont(UITheme.FONT_PRICE);
        price.setForeground(UITheme.COLOR_RASPBERRY);
        price.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(price);
        card.add(Box.createVerticalStrut(4));

        // Condition & Owner
        JLabel condition = new JLabel("Condition: " + item.getItemCondition() + " · Shared by " +
                (item.getOwnerName() != null ? item.getOwnerName() : "Student"));
        condition.setFont(UITheme.FONT_SMALL);
        condition.setForeground(UITheme.COLOR_MUTED);
        condition.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(condition);

        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(10));

        // Action Buttons: View Details & Request
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnView = UITheme.createSecondaryButton("Details");
        btnView.setFont(UITheme.FONT_SMALL);
        btnView.addActionListener(e -> {
            ItemDetailDialog dialog = new ItemDetailDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    item,
                    currentUser,
                    this::loadItems
            );
            dialog.setVisible(true);
        });

        JButton btnRequest = UITheme.createPrimaryButton("Request");
        btnRequest.setFont(UITheme.FONT_SMALL);

        boolean isOwnItem = currentUser != null && currentUser.getId() == item.getOwnerId();
        if (isOwnItem) {
            btnRequest.setEnabled(false);
            btnRequest.setText("My Item");
        } else {
            btnRequest.addActionListener(e -> quickRequest(item));
        }

        actions.add(btnView);
        actions.add(btnRequest);
        card.add(actions);

        return card;
    }

    private void quickRequest(Item item) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Would you like to send a request for \"" + item.getName() + "\" to " +
                        (item.getOwnerName() != null ? item.getOwnerName() : "the seller") + "?",
                "Confirm Request",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                requestService.requestItem(item.getId(), currentUser, "Interested in your item listed on campus thrift.");
                JOptionPane.showMessageDialog(
                        this,
                        "Request sent successfully! Track it in 'My Requests'.",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadItems();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Request Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
