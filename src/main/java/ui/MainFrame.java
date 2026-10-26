package ui;

import model.User;
import service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application window for thrift (Campus Thrift), featuring the signature Rose sidebar,
 * topbar with greeting & user status, and CardLayout for switching pages.
 */
public class MainFrame extends JFrame {

    private final User currentUser;

    // Sidebar components
    private JLabel sideAvatar;
    private JLabel sideName;
    private JLabel sideReg;
    private final Map<String, JButton> navButtons = new HashMap<>();

    // Topbar components
    private JLabel topEyebrow;
    private JLabel topTitle;
    private JLabel topUserName;
    private JLabel topAvatar;

    // Content area
    private CardLayout cardLayout;
    private JPanel contentCards;

    // Sub-panels
    private HomePanel homePanel;
    private MarketplacePanel marketplacePanel;
    private AddItemPanel addItemPanel;
    private MyListingsPanel myListingsPanel;
    private MyRequestsPanel myRequestsPanel;
    private ProfilePanel profilePanel;

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;

        setTitle("thrift — Campus Finds, New Stories");
        setSize(1200, 780);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        navigateTo("HOME");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Sidebar (Left, 250px)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Right Main Section (Topbar + Card Content + Footer)
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.COLOR_PAPER);

        // Topbar
        JPanel topbar = createTopbar();
        mainArea.add(topbar, BorderLayout.NORTH);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(UITheme.COLOR_PAPER);

        homePanel = new HomePanel(
                currentUser,
                this::navigateTo,
                category -> {
                    marketplacePanel.setCategoryFilter(category);
                    navigateTo("MARKETPLACE");
                }
        );

        marketplacePanel = new MarketplacePanel(currentUser);
        addItemPanel = new AddItemPanel(currentUser, this::navigateTo);
        myListingsPanel = new MyListingsPanel(currentUser, this::navigateTo);
        myRequestsPanel = new MyRequestsPanel(currentUser);
        profilePanel = new ProfilePanel(currentUser, this::syncUserInfo);

        contentCards.add(homePanel, "HOME");
        contentCards.add(marketplacePanel, "MARKETPLACE");
        contentCards.add(addItemPanel, "ADD_ITEM");
        contentCards.add(myListingsPanel, "MY_LISTINGS");
        contentCards.add(myRequestsPanel, "REQUESTS");
        contentCards.add(profilePanel, "PROFILE");

        mainArea.add(contentCards, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        footer.setBackground(UITheme.COLOR_CREAM);
        footer.setBorder(new LineBorder(UITheme.COLOR_LINE, 1, false));
        JLabel footLbl = new JLabel("THRIFT · FIND GOOD THINGS. GIVE THEM NEW STORIES. · MADE FOR OUR CAMPUS ✿");
        footLbl.setFont(UITheme.FONT_KICKER);
        footLbl.setForeground(UITheme.COLOR_MUTED);
        footer.add(footLbl);
        mainArea.add(footer, BorderLayout.SOUTH);

        add(mainArea, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(UITheme.COLOR_ROSE);
        sidebar.setBorder(new EmptyBorder(22, 16, 20, 16));

        // Brand Logo
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandMark = new JLabel("↗");
        brandMark.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandMark.setForeground(UITheme.COLOR_BLUSH);

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);

        JLabel brandTitle = new JLabel("thrift");
        brandTitle.setFont(UITheme.FONT_BRAND_LARGE);
        brandTitle.setForeground(UITheme.COLOR_BLUSH);

        JLabel brandSub = new JLabel("THE CAMPUS EXCHANGE");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 8));
        brandSub.setForeground(new Color(0xD9, 0xB7, 0xC3));

        brandText.add(brandTitle);
        brandText.add(brandSub);
        brand.add(brandMark);
        brand.add(brandText);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(18));

        // User info box
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        userBox.setOpaque(false);
        userBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 40), 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
        userBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        userBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        sideAvatar = new JLabel(getInitial(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_BLUSH);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.COLOR_ROSE);
                g2.setFont(new Font("Georgia", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        sideAvatar.setPreferredSize(new Dimension(36, 36));
        sideAvatar.setOpaque(false);

        JPanel userTexts = new JPanel();
        userTexts.setLayout(new BoxLayout(userTexts, BoxLayout.Y_AXIS));
        userTexts.setOpaque(false);

        sideName = new JLabel(currentUser.getName());
        sideName.setFont(UITheme.FONT_BODY_BOLD);
        sideName.setForeground(Color.WHITE);

        sideReg = new JLabel(currentUser.getRegistrationNumber());
        sideReg.setFont(UITheme.FONT_SMALL);
        sideReg.setForeground(UITheme.COLOR_BLUSH);

        userTexts.add(sideName);
        userTexts.add(sideReg);
        userBox.add(sideAvatar);
        userBox.add(userTexts);

        sidebar.add(userBox);
        sidebar.add(Box.createVerticalStrut(18));

        // Section label
        JLabel sectionLabel = new JLabel("YOUR LITTLE CORNER");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        sectionLabel.setForeground(new Color(0xD6, 0xA9, 0xBA));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sectionLabel);
        sidebar.add(Box.createVerticalStrut(8));

        // Navigation links
        addNavLink(sidebar, "HOME", "⌂   Home");
        addNavLink(sidebar, "MARKETPLACE", "⌕   Find treasures");
        addNavLink(sidebar, "ADD_ITEM", "＋   Give an item");
        addNavLink(sidebar, "MY_LISTINGS", "▤   My listings");
        addNavLink(sidebar, "REQUESTS", "♡   Item requests");
        addNavLink(sidebar, "PROFILE", "👤   My profile");

        sidebar.add(Box.createVerticalGlue());

        // Note Card at bottom
        JPanel noteCard = new JPanel();
        noteCard.setLayout(new BoxLayout(noteCard, BoxLayout.Y_AXIS));
        noteCard.setBackground(new Color(255, 255, 255, 18));
        noteCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 45), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        noteCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel star = new JLabel("❀");
        star.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        star.setForeground(UITheme.COLOR_GOLD);

        JLabel quote = new JLabel("<html><i>“Good things deserve a second life. Great finds deserve a new owner.”</i></html>");
        quote.setFont(new Font("Georgia", Font.ITALIC, 11));
        quote.setForeground(UITheme.COLOR_BLUSH);
        quote.setBorder(new EmptyBorder(4, 0, 6, 0));

        JLabel reuse = new JLabel("REUSE MORE · SPEND LESS");
        reuse.setFont(new Font("Segoe UI", Font.BOLD, 8));
        reuse.setForeground(new Color(0xD6, 0xA9, 0xBA));

        noteCard.add(star);
        noteCard.add(quote);
        noteCard.add(reuse);
        sidebar.add(noteCard);
        sidebar.add(Box.createVerticalStrut(10));

        // Logout button
        JButton logoutBtn = new JButton("↪   Log out");
        logoutBtn.setFont(UITheme.FONT_BODY_BOLD);
        logoutBtn.setForeground(UITheme.COLOR_BLUSH);
        logoutBtn.setBackground(new Color(255, 255, 255, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(10, 12, 10, 12));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private void addNavLink(JPanel sidebar, String pageKey, String label) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setForeground(UITheme.COLOR_BLUSH);
        btn.setBackground(UITheme.COLOR_ROSE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 12, 10, 12));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addActionListener(e -> navigateTo(pageKey));
        navButtons.put(pageKey, btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(3));
    }

    private JPanel createTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1),
                new EmptyBorder(14, 24, 14, 24)
        ));

        // Left Titles
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        topEyebrow = new JLabel("A LITTLE GOOD GOES A LONG WAY");
        topEyebrow.setFont(UITheme.FONT_KICKER);
        topEyebrow.setForeground(UITheme.COLOR_RASPBERRY);

        String firstName = currentUser.getName() != null ? currentUser.getName().split(" ")[0] : "there";
        topTitle = new JLabel("Hello, " + firstName + " ✿");
        topTitle.setFont(UITheme.FONT_HEADER_1);
        topTitle.setForeground(UITheme.COLOR_ROSE);

        left.add(topEyebrow);
        left.add(topTitle);
        topbar.add(left, BorderLayout.WEST);

        // Right Actions (Profile chip + Refresh button)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton refreshAllBtn = UITheme.createSoftButton("⟳ Refresh");
        refreshAllBtn.addActionListener(e -> refreshCurrentPage());

        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        chip.setBackground(UITheme.COLOR_CARD_BG);
        chip.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(2, 6, 2, 8)
        ));
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topAvatar = new JLabel(getInitial(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_BLUSH);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.COLOR_ROSE);
                g2.setFont(new Font("Georgia", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        topAvatar.setPreferredSize(new Dimension(28, 28));

        JPanel chipTexts = new JPanel();
        chipTexts.setLayout(new BoxLayout(chipTexts, BoxLayout.Y_AXIS));
        chipTexts.setOpaque(false);

        topUserName = new JLabel(firstName);
        topUserName.setFont(UITheme.FONT_BODY_BOLD);
        topUserName.setForeground(UITheme.COLOR_ROSE);

        JLabel chipRole = new JLabel("Campus member ▾");
        chipRole.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chipRole.setForeground(UITheme.COLOR_MUTED);

        chipTexts.add(topUserName);
        chipTexts.add(chipRole);

        chip.add(topAvatar);
        chip.add(chipTexts);

        chip.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                navigateTo("PROFILE");
            }
        });

        right.add(refreshAllBtn);
        right.add(chip);
        topbar.add(right, BorderLayout.EAST);

        return topbar;
    }

    public void navigateTo(String pageKey) {
        // Highlight active nav button
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            if (entry.getKey().equals(pageKey)) {
                entry.getValue().setBackground(UITheme.COLOR_BLUSH);
                entry.getValue().setForeground(UITheme.COLOR_ROSE);
            } else {
                entry.getValue().setBackground(UITheme.COLOR_ROSE);
                entry.getValue().setForeground(UITheme.COLOR_BLUSH);
            }
        }

        // Update Topbar header texts
        String firstName = currentUser.getName() != null ? currentUser.getName().split(" ")[0] : "there";
        switch (pageKey) {
            case "HOME":
                topEyebrow.setText("A LITTLE GOOD GOES A LONG WAY");
                topTitle.setText("Hello, " + firstName + " ✿");
                homePanel.refreshData();
                break;
            case "MARKETPLACE":
                topEyebrow.setText("YOUR NEXT FAVOURITE IS HERE");
                topTitle.setText("Find your little treasure");
                marketplacePanel.loadItems();
                break;
            case "ADD_ITEM":
                topEyebrow.setText("GIVE IT A SECOND STORY");
                topTitle.setText("Pass on a good thing");
                break;
            case "MY_LISTINGS":
                topEyebrow.setText("YOUR CAMPUS SHELF");
                topTitle.setText("My listings");
                myListingsPanel.loadMyListings();
                break;
            case "REQUESTS":
                topEyebrow.setText("YOUR CAMPUS CIRCLE");
                topTitle.setText("Requests & Handover");
                myRequestsPanel.loadAllRequests();
                break;
            case "PROFILE":
                topEyebrow.setText("YOUR DETAILS, YOUR WAY");
                topTitle.setText("My profile");
                break;
        }

        cardLayout.show(contentCards, pageKey);
    }

    private void refreshCurrentPage() {
        homePanel.refreshData();
        marketplacePanel.loadItems();
        myListingsPanel.loadMyListings();
        myRequestsPanel.loadAllRequests();
    }

    private void syncUserInfo() {
        sideAvatar.setText(getInitial());
        sideName.setText(currentUser.getName());
        sideReg.setText(currentUser.getRegistrationNumber());

        String firstName = currentUser.getName() != null ? currentUser.getName().split(" ")[0] : "there";
        topUserName.setText(firstName);
        topAvatar.setText(getInitial());

        repaint();
    }

    private String getInitial() {
        if (currentUser.getName() != null && !currentUser.getName().trim().isEmpty()) {
            return currentUser.getName().trim().substring(0, 1).toUpperCase();
        }
        return "U";
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of thrift?",
                "Log Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            AuthenticationService.logout();
            dispose();
            AuthFrame authFrame = new AuthFrame();
            authFrame.setVisible(true);
        }
    }
}
