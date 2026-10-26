package ui;

import model.ItemRequest;
import model.User;
import service.RequestService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Requests manager providing both Sent Requests (requests I made)
 * and Incoming Requests (requests by others for my items with Accept/Reject actions).
 */
public class MyRequestsPanel extends JPanel {

    private final User currentUser;
    private final RequestService requestService;

    // Sent requests table
    private JTable sentTable;
    private DefaultTableModel sentModel;
    private List<ItemRequest> sentList;

    // Incoming requests table
    private JTable incomingTable;
    private DefaultTableModel incomingModel;
    private List<ItemRequest> incomingList;

    public MyRequestsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.requestService = new RequestService();

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_PAPER);

        initComponents();
        loadAllRequests();
    }

    private void initComponents() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(16, 24, 10, 24));

        JLabel kicker = new JLabel("CAMPUS CIRCLE ✿");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("Item Requests");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel sub = new JLabel("Track your requested treasures and manage incoming requests from fellow students.");
        sub.setFont(UITheme.FONT_SUBTITLE);
        sub.setForeground(UITheme.COLOR_MUTED);

        topContainer.add(kicker);
        topContainer.add(title);
        topContainer.add(sub);
        add(topContainer, BorderLayout.NORTH);

        // Tabbed Pane for Sent vs Incoming
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(UITheme.COLOR_PAPER);
        tabbedPane.setForeground(UITheme.COLOR_ROSE);
        tabbedPane.setBorder(new EmptyBorder(6, 20, 20, 20));

        tabbedPane.addTab("♡ Sent Requests (My Inquiries)", createSentRequestsPanel());
        tabbedPane.addTab("📬 Incoming Requests (Manage Inquiries)", createIncomingRequestsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSentRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tb.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadSentRequests());

        JButton cancelBtn = UITheme.createSoftButton("Cancel Selected Request");
        cancelBtn.addActionListener(e -> handleCancelSent());

        tb.add(refreshBtn);
        tb.add(cancelBtn);
        panel.add(tb, BorderLayout.NORTH);

        String[] cols = {"Req ID", "Item Name", "Listing Type", "Price", "Item Owner", "Owner Reg", "Status", "Request Date"};
        sentModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        sentTable = new JTable(sentModel);
        setupTable(sentTable);
        panel.add(new JScrollPane(sentTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createIncomingRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tb.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadIncomingRequests());

        JButton acceptBtn = UITheme.createPrimaryButton("✓ Accept / Reserve Item");
        acceptBtn.addActionListener(e -> handleAcceptIncoming());

        JButton declineBtn = UITheme.createSoftButton("✕ Decline Request");
        declineBtn.setForeground(new Color(0xC6, 0x28, 0x28));
        declineBtn.addActionListener(e -> handleDeclineIncoming());

        JButton completeBtn = UITheme.createSecondaryButton("★ Handover Completed");
        completeBtn.addActionListener(e -> handleCompleteHandover());

        tb.add(refreshBtn);
        tb.add(acceptBtn);
        tb.add(declineBtn);
        tb.add(completeBtn);
        panel.add(tb, BorderLayout.NORTH);

        String[] cols = {"Req ID", "Requester Name", "Reg No", "Requester Email", "Item Name", "Message / Note", "Status", "Date"};
        incomingModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        incomingTable = new JTable(incomingModel);
        setupTable(incomingTable);
        panel.add(new JScrollPane(incomingTable), BorderLayout.CENTER);

        return panel;
    }

    private void setupTable(JTable table) {
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UITheme.COLOR_BLUSH);
        table.getTableHeader().setForeground(UITheme.COLOR_ROSE);
        table.setGridColor(UITheme.COLOR_LINE);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

        // Status column renderer
        int statusColIdx = table.getColumnCount() - 2;
        table.getColumnModel().getColumn(statusColIdx).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                String s = val != null ? val.toString().toUpperCase() : "";
                if ("APPROVED".equals(s) || "ACCEPTED".equals(s)) {
                    setForeground(UITheme.COLOR_AVAILABLE);
                } else if ("PENDING".equals(s)) {
                    setForeground(UITheme.COLOR_RASPBERRY);
                } else if ("REJECTED".equals(s) || "DECLINED".equals(s)) {
                    setForeground(new Color(0xD3, 0x2F, 0x2F));
                } else {
                    setForeground(Color.DARK_GRAY);
                }
                setFont(UITheme.FONT_BODY_BOLD);
                return c;
            }
        });
    }

    public void loadAllRequests() {
        loadSentRequests();
        loadIncomingRequests();
    }

    public void loadSentRequests() {
        SwingUtilities.invokeLater(() -> {
            try {
                sentList = requestService.getSentRequests(currentUser.getId());
                sentModel.setRowCount(0);

                for (ItemRequest r : sentList) {
                    sentModel.addRow(new Object[]{
                            r.getId(),
                            r.getItemName(),
                            r.getItemListingType(),
                            r.getItemPrice() == 0 ? "FREE" : String.format("₹%.2f", r.getItemPrice()),
                            r.getOwnerName() != null ? r.getOwnerName() : "Student",
                            r.getOwnerReg() != null ? r.getOwnerReg() : "-",
                            r.getStatus(),
                            r.getCreatedAt() != null ? r.getCreatedAt().toString().substring(0, 16) : "-"
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load sent requests: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void loadIncomingRequests() {
        SwingUtilities.invokeLater(() -> {
            try {
                incomingList = requestService.getIncomingRequests(currentUser.getId());
                incomingModel.setRowCount(0);

                for (ItemRequest r : incomingList) {
                    incomingModel.addRow(new Object[]{
                            r.getId(),
                            r.getRequesterName() != null ? r.getRequesterName() : "Student",
                            r.getRequesterReg() != null ? r.getRequesterReg() : "-",
                            r.getRequesterEmail() != null ? r.getRequesterEmail() : "-",
                            r.getItemName(),
                            r.getNotes() != null && !r.getNotes().isEmpty() ? r.getNotes() : "No note attached",
                            r.getStatus(),
                            r.getCreatedAt() != null ? r.getCreatedAt().toString().substring(0, 16) : "-"
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load incoming requests: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void handleCancelSent() {
        int row = sentTable.getSelectedRow();
        if (row < 0 || sentList == null || row >= sentList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a sent request to cancel.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ItemRequest r = sentList.get(row);
        if (!"PENDING".equalsIgnoreCase(r.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only PENDING requests can be cancelled.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this, "Cancel request for \"" + r.getItemName() + "\"?", "Cancel Request", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                requestService.cancelSentRequest(r.getId(), currentUser.getId());
                JOptionPane.showMessageDialog(this, "Request cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSentRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to cancel request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAcceptIncoming() {
        int row = incomingTable.getSelectedRow();
        if (row < 0 || incomingList == null || row >= incomingList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an incoming request to accept.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ItemRequest r = incomingList.get(row);
        if (!"PENDING".equalsIgnoreCase(r.getStatus())) {
            JOptionPane.showMessageDialog(this, "This request has already been " + r.getStatus() + ".", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(
                this,
                "Accept request from " + r.getRequesterName() + " (" + r.getRequesterReg() + ") for \"" + r.getItemName() + "\"?\n" +
                "This will mark the item status as RESERVED in the marketplace.",
                "Accept Request",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (conf == JOptionPane.YES_OPTION) {
            try {
                boolean ok = requestService.approveRequest(r.getId(), r.getItemId(), currentUser.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Request accepted! The item has been marked RESERVED.\nPlease coordinate pickup with " + r.getRequesterName() + " via " + r.getRequesterEmail() + ".",
                            "Request Approved ✿",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadIncomingRequests();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not approve request. Please check if the item is still available.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error approving request: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeclineIncoming() {
        int row = incomingTable.getSelectedRow();
        if (row < 0 || incomingList == null || row >= incomingList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an incoming request to decline.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ItemRequest r = incomingList.get(row);
        if (!"PENDING".equalsIgnoreCase(r.getStatus())) {
            JOptionPane.showMessageDialog(this, "This request is already " + r.getStatus() + ".", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(
                this,
                "Decline request from " + r.getRequesterName() + " for \"" + r.getItemName() + "\"?",
                "Decline Request",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (conf == JOptionPane.YES_OPTION) {
            try {
                requestService.rejectRequest(r.getId(), currentUser.getId());
                JOptionPane.showMessageDialog(this, "Request declined.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                loadIncomingRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error declining request: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCompleteHandover() {
        int row = incomingTable.getSelectedRow();
        if (row < 0 || incomingList == null || row >= incomingList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an accepted request to mark completed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ItemRequest r = incomingList.get(row);
        String[] options = {"SOLD", "DONATED"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "The handover for \"" + r.getItemName() + "\" is finished.\nUpdate the final item status to:",
                "Complete Handover",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "DONATE".equalsIgnoreCase(r.getItemListingType()) ? "DONATED" : "SOLD"
        );

        if (choice != null) {
            try {
                requestService.completeRequest(r.getId(), r.getItemId(), currentUser.getId(), choice);
                JOptionPane.showMessageDialog(this, "Handover completed! The item has been marked as " + choice + " ✿", "Completed", JOptionPane.INFORMATION_MESSAGE);
                loadIncomingRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error completing request: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
