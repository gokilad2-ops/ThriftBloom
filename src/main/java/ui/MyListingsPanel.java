package ui;

import model.Item;
import model.User;
import service.ItemService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * My Listings view allowing students to manage their own posted academic materials.
 * Supports viewing, editing, updating status, and deleting items.
 */
public class MyListingsPanel extends JPanel {

    private final User currentUser;
    private final ItemService itemService;
    private final Consumer<String> pageNavigator;

    private JTable listingsTable;
    private DefaultTableModel tableModel;
    private List<Item> userItems;
    private JLabel countLabel;

    public MyListingsPanel(User currentUser, Consumer<String> pageNavigator) {
        this.currentUser = currentUser;
        this.pageNavigator = pageNavigator;
        this.itemService = new ItemService();

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_PAPER);

        initComponents();
        loadMyListings();
    }

    private void initComponents() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(16, 24, 10, 24));

        // Header Row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel kicker = new JLabel("YOUR POSTED ITEMS ✿");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("My Listings");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel sub = new JLabel("Manage your campus listings, update availability, or edit details.");
        sub.setFont(UITheme.FONT_SUBTITLE);
        sub.setForeground(UITheme.COLOR_MUTED);

        titleGroup.add(kicker);
        titleGroup.add(title);
        titleGroup.add(sub);

        countLabel = UITheme.createStatusBadge("0 LISTINGS");

        headerRow.add(titleGroup, BorderLayout.WEST);
        headerRow.add(countLabel, BorderLayout.EAST);
        topContainer.add(headerRow);
        topContainer.add(Box.createVerticalStrut(14));

        // Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAddNew = UITheme.createPrimaryButton("＋ Add New Listing");
        btnAddNew.addActionListener(e -> pageNavigator.accept("ADD_ITEM"));

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadMyListings());

        JButton btnEdit = UITheme.createSoftButton("✎ Edit Selected");
        btnEdit.addActionListener(e -> editSelectedItem());

        JButton btnStatus = UITheme.createSoftButton("⟳ Change Status");
        btnStatus.addActionListener(e -> changeStatusSelectedItem());

        JButton btnDelete = UITheme.createSoftButton("🗑 Delete Selected");
        btnDelete.setForeground(new Color(0xC6, 0x28, 0x28));
        btnDelete.addActionListener(e -> deleteSelectedItem());

        toolbar.add(btnAddNew);
        toolbar.add(btnRefresh);
        toolbar.add(btnEdit);
        toolbar.add(btnStatus);
        toolbar.add(btnDelete);

        topContainer.add(toolbar);
        add(topContainer, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Item Name", "Category", "Condition", "Type", "Price", "Status", "Date Posted"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        listingsTable = new JTable(tableModel);
        listingsTable.setFont(UITheme.FONT_BODY);
        listingsTable.setRowHeight(34);
        listingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listingsTable.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        listingsTable.getTableHeader().setBackground(UITheme.COLOR_BLUSH);
        listingsTable.getTableHeader().setForeground(UITheme.COLOR_ROSE);
        listingsTable.setGridColor(UITheme.COLOR_LINE);
        listingsTable.setShowVerticalLines(false);

        // Center renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listingsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        listingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        listingsTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        listingsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        listingsTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        listingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        listingsTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        listingsTable.getColumnModel().getColumn(7).setPreferredWidth(110);

        listingsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                String status = value != null ? value.toString() : "";
                if ("AVAILABLE".equalsIgnoreCase(status)) {
                    setForeground(UITheme.COLOR_AVAILABLE);
                } else if ("RESERVED".equalsIgnoreCase(status)) {
                    setForeground(UITheme.COLOR_RESERVED);
                } else {
                    setForeground(Color.GRAY);
                }
                setFont(UITheme.FONT_BODY_BOLD);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(listingsTable);
        scrollPane.setBorder(new CompoundBorder(
                new EmptyBorder(10, 24, 24, 24),
                new LineBorder(UITheme.COLOR_LINE, 1, true)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadMyListings() {
        SwingUtilities.invokeLater(() -> {
            try {
                userItems = itemService.getMyListings(currentUser.getId());
                tableModel.setRowCount(0);

                for (Item item : userItems) {
                    tableModel.addRow(new Object[]{
                            item.getId(),
                            item.getName(),
                            item.getCategory(),
                            item.getItemCondition(),
                            item.getListingType(),
                            item.getPriceFormatted(),
                            item.getStatus(),
                            item.getCreatedAt() != null ? item.getCreatedAt().toString().substring(0, 10) : "-"
                    });
                }

                countLabel.setText(userItems.size() + " LISTINGS");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load your listings: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private Item getSelectedItem() {
        int row = listingsTable.getSelectedRow();
        if (row < 0 || userItems == null || row >= userItems.size()) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table first.", "No Item Selected", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return userItems.get(row);
    }

    private void editSelectedItem() {
        Item item = getSelectedItem();
        if (item == null) return;

        // Create an edit dialog
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Listing — " + item.getName(), true);
        editDialog.setSize(520, 500);
        editDialog.setLocationRelativeTo(this);
        editDialog.getContentPane().setBackground(UITheme.COLOR_CREAM);
        editDialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.COLOR_CREAM);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField nameField = UITheme.createTextField(20);
        nameField.setText(item.getName());

        String[] cats = {"Books", "Electronics", "Study Materials", "Lab Equipment", "Stationery", "Clothing", "Furniture", "Other"};
        JComboBox<String> catCombo = new JComboBox<>(cats);
        catCombo.setSelectedItem(item.getCategory());

        String[] conds = {"Like new", "Very good", "Good", "Fair"};
        JComboBox<String> condCombo = new JComboBox<>(conds);
        condCombo.setSelectedItem(item.getItemCondition());

        JTextField priceField = UITheme.createTextField(10);
        priceField.setText(String.valueOf(item.getPrice()));
        if (!"SELL".equalsIgnoreCase(item.getListingType())) {
            priceField.setEnabled(false);
        }

        JTextArea descArea = new JTextArea(item.getDescription(), 4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(new LineBorder(UITheme.COLOR_LINE, 1, true));

        form.add(createFieldGroup("ITEM NAME *", nameField));
        form.add(Box.createVerticalStrut(8));
        form.add(createFieldGroup("CATEGORY *", catCombo));
        form.add(Box.createVerticalStrut(8));
        form.add(createFieldGroup("CONDITION *", condCombo));
        form.add(Box.createVerticalStrut(8));
        form.add(createFieldGroup("PRICE (₹)", priceField));
        form.add(Box.createVerticalStrut(8));
        form.add(createFieldGroup("DESCRIPTION *", new JScrollPane(descArea)));

        editDialog.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.COLOR_CREAM);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> editDialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Save Changes");
        saveBtn.addActionListener(e -> {
            try {
                item.setName(nameField.getText().trim());
                item.setCategory((String) catCombo.getSelectedItem());
                item.setItemCondition((String) condCombo.getSelectedItem());
                item.setDescription(descArea.getText().trim());
                if ("SELL".equalsIgnoreCase(item.getListingType())) {
                    item.setPrice(Double.parseDouble(priceField.getText().trim()));
                }

                itemService.updateItem(item, currentUser.getId());
                JOptionPane.showMessageDialog(editDialog, "Listing updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
                loadMyListings();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        editDialog.add(btnPanel, BorderLayout.SOUTH);

        editDialog.setVisible(true);
    }

    private void changeStatusSelectedItem() {
        Item item = getSelectedItem();
        if (item == null) return;

        String[] statuses = {"AVAILABLE", "RESERVED", "SOLD", "DONATED"};
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select new availability status for \"" + item.getName() + "\":",
                "Update Item Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                item.getStatus()
        );

        if (selected != null && !selected.equals(item.getStatus())) {
            try {
                itemService.updateStatus(item.getId(), currentUser.getId(), selected);
                JOptionPane.showMessageDialog(this, "Status updated to " + selected + " ✿", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadMyListings();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedItem() {
        Item item = getSelectedItem();
        if (item == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently remove \"" + item.getName() + "\" from your listings?\n" +
                "Any related requests for this item will also be removed.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                itemService.deleteItem(item.getId(), currentUser.getId());
                JOptionPane.showMessageDialog(this, "Item deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadMyListings();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_KICKER);
        lbl.setForeground(UITheme.COLOR_ROSE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(3));
        p.add(field);
        return p;
    }
}
