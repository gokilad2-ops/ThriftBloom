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
import java.util.function.Consumer;

/**
 * Overview Home dashboard with welcome hero banner, quick campus thrift statistics,
 * category jump buttons, and freshly shared items.
 */
public class HomePanel extends JPanel {

    private final User currentUser;
    private final ItemService itemService;
    private final RequestService requestService;
    private final Consumer<String> pageNavigator;
    private final Consumer<String> categoryNavigator;

    private JLabel statTreasuresVal;
    private JLabel statFreeVal;
    private JLabel statMyListingsVal;
    private JLabel statMyRequestsVal;
    private JPanel freshItemsContainer;

    public HomePanel(User currentUser, Consumer<String> pageNavigator, Consumer<String> categoryNavigator) {
        this.currentUser = currentUser;
        this.pageNavigator = pageNavigator;
        this.categoryNavigator = categoryNavigator;
        this.itemService = new ItemService();
        this.requestService = new RequestService();

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_PAPER);

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UITheme.COLOR_PAPER);
        scrollContent.setBorder(new EmptyBorder(16, 24, 30, 24));

        // 1. Welcome Hero Banner
        scrollContent.add(createHeroBanner());
        scrollContent.add(Box.createVerticalStrut(20));

        // 2. Categories Section
        scrollContent.add(createCategoriesSection());
        scrollContent.add(Box.createVerticalStrut(24));

        // 3. Freshly Shared Items Header
        JPanel freshHeader = new JPanel(new BorderLayout());
        freshHeader.setOpaque(false);
        freshHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JPanel freshTitleGroup = new JPanel();
        freshTitleGroup.setLayout(new BoxLayout(freshTitleGroup, BoxLayout.Y_AXIS));
        freshTitleGroup.setOpaque(false);

        JLabel freshKicker = new JLabel("FRESHLY SHARED");
        freshKicker.setFont(UITheme.FONT_KICKER);
        freshKicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel freshTitle = new JLabel("Little treasures ✨");
        freshTitle.setFont(UITheme.FONT_HEADER_2);
        freshTitle.setForeground(UITheme.COLOR_ROSE);

        freshTitleGroup.add(freshKicker);
        freshTitleGroup.add(freshTitle);

        JButton viewAllBtn = UITheme.createSoftButton("View all →");
        viewAllBtn.addActionListener(e -> pageNavigator.accept("MARKETPLACE"));

        freshHeader.add(freshTitleGroup, BorderLayout.WEST);
        freshHeader.add(viewAllBtn, BorderLayout.EAST);
        scrollContent.add(freshHeader);
        scrollContent.add(Box.createVerticalStrut(12));

        // Fresh items container
        freshItemsContainer = new JPanel(new GridLayout(1, 3, 14, 0));
        freshItemsContainer.setOpaque(false);
        freshItemsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        scrollContent.add(freshItemsContainer);
        scrollContent.add(Box.createVerticalStrut(24));

        // 4. Promo Banner
        scrollContent.add(createPromoBanner());

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_BLUSH);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(getWidth() - 150, -40, 200, 200);
                g2.dispose();
            }
        };

        hero.setLayout(new BorderLayout());
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(22, 24, 22, 24));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel kicker = new JLabel("A COMMUNITY THAT CARES ✿");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("<html>Good finds. <em>New stories.</em></html>");
        title.setFont(new Font("Georgia", Font.BOLD, 28));
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel desc = new JLabel("<html>Welcome to your campus corner — where useful textbooks and kits find new homes,<br>student budgets go further, and small acts of sharing make a lovely difference.</html>");
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(new Color(0x79, 0x52, 0x60));
        desc.setBorder(new EmptyBorder(6, 0, 14, 0));

        // Hero action buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);

        JButton btnExplore = UITheme.createPrimaryButton("⌕ Find treasures");
        btnExplore.addActionListener(e -> pageNavigator.accept("MARKETPLACE"));

        JButton btnDonate = UITheme.createSecondaryButton("＋ Give an item");
        btnDonate.addActionListener(e -> pageNavigator.accept("ADD_ITEM"));

        btns.add(btnExplore);
        btns.add(btnDonate);

        left.add(kicker);
        left.add(title);
        left.add(desc);
        left.add(btns);
        left.add(Box.createVerticalStrut(14));

        // Quick Stats row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statsRow.setOpaque(false);

        statTreasuresVal = new JLabel("0");
        statFreeVal = new JLabel("0");
        statMyListingsVal = new JLabel("0");
        statMyRequestsVal = new JLabel("0");

        statsRow.add(createStatChip("little treasures", statTreasuresVal));
        statsRow.add(createStatChip("free finds", statFreeVal));
        statsRow.add(createStatChip("your listings", statMyListingsVal));
        statsRow.add(createStatChip("your requests", statMyRequestsVal));

        left.add(statsRow);
        hero.add(left, BorderLayout.CENTER);

        return hero;
    }

    private JPanel createStatChip(String label, JLabel valueLabel) {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBackground(new Color(255, 255, 255, 180));
        chip.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 220), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        valueLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        valueLabel.setForeground(UITheme.COLOR_ROSE);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(UITheme.COLOR_MUTED);

        chip.add(valueLabel);
        chip.add(lbl);
        return chip;
    }

    private JPanel createCategoriesSection() {
        JPanel catSection = new JPanel();
        catSection.setLayout(new BoxLayout(catSection, BoxLayout.Y_AXIS));
        catSection.setOpaque(false);
        catSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JLabel kicker = new JLabel("WANDER A LITTLE");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("What are you looking for?");
        title.setFont(UITheme.FONT_HEADER_3);
        title.setForeground(UITheme.COLOR_ROSE);

        JPanel tg = new JPanel();
        tg.setLayout(new BoxLayout(tg, BoxLayout.Y_AXIS));
        tg.setOpaque(false);
        tg.add(kicker);
        tg.add(title);
        head.add(tg, BorderLayout.WEST);

        catSection.add(head);
        catSection.add(Box.createVerticalStrut(10));

        // 6 Category buttons
        JPanel strip = new JPanel(new GridLayout(1, 6, 10, 0));
        strip.setOpaque(false);

        String[][] cats = {
                {"Books", "📚"},
                {"Electronics", "💻"},
                {"Study Materials", "📐"},
                {"Lab Equipment", "🥼"},
                {"Stationery", "✏️"},
                {"Other", "✿"}
        };

        for (String[] c : cats) {
            JButton catBtn = new JButton("<html><center style='font-size:18px'>" + c[1] + "</center><br><b>" + c[0] + "</b></html>");
            catBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            catBtn.setForeground(UITheme.COLOR_ROSE);
            catBtn.setBackground(UITheme.COLOR_CREAM);
            catBtn.setFocusPainted(false);
            catBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            catBtn.setBorder(new CompoundBorder(
                    new LineBorder(UITheme.COLOR_LINE, 1, true),
                    new EmptyBorder(8, 4, 8, 4)
            ));
            catBtn.addActionListener(e -> categoryNavigator.accept(c[0]));
            strip.add(catBtn);
        }

        catSection.add(strip);
        return catSection;
    }

    private JPanel createPromoBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_ROSE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };

        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(18, 22, 18, 22));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel h = new JLabel("Have something good to pass on?");
        h.setFont(new Font("Georgia", Font.BOLD, 17));
        h.setForeground(Color.WHITE);

        JLabel p = new JLabel("Someone on campus might be looking for exactly what you no longer need.");
        p.setFont(UITheme.FONT_SUBTITLE);
        p.setForeground(UITheme.COLOR_BLUSH);

        left.add(h);
        left.add(Box.createVerticalStrut(3));
        left.add(p);

        JButton shareBtn = UITheme.createSoftButton("Share the love ✿");
        shareBtn.addActionListener(e -> pageNavigator.accept("ADD_ITEM"));

        banner.add(left, BorderLayout.WEST);
        banner.add(shareBtn, BorderLayout.EAST);
        return banner;
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                int totalAvail = itemService.getAvailableCount();
                int totalFree = itemService.getFreeCount();
                int myListings = itemService.getOwnerListingCount(currentUser.getId());
                int myReqs = requestService.getSentRequestCount(currentUser.getId());

                statTreasuresVal.setText(String.valueOf(totalAvail));
                statFreeVal.setText(String.valueOf(totalFree));
                statMyListingsVal.setText(String.valueOf(myListings));
                statMyRequestsVal.setText(String.valueOf(myReqs));

                // Load top 3 fresh items
                List<Item> fresh = itemService.searchAvailableItems("", "", "", "NEWEST");
                freshItemsContainer.removeAll();

                int count = Math.min(3, fresh.size());
                if (count == 0) {
                    JLabel emptyLbl = new JLabel("No treasures available right now. Be the first to share one!", SwingConstants.CENTER);
                    emptyLbl.setFont(UITheme.FONT_BODY);
                    emptyLbl.setForeground(UITheme.COLOR_MUTED);
                    freshItemsContainer.setLayout(new BorderLayout());
                    freshItemsContainer.add(emptyLbl, BorderLayout.CENTER);
                } else {
                    freshItemsContainer.setLayout(new GridLayout(1, 3, 14, 0));
                    for (int i = 0; i < count; i++) {
                        Item item = fresh.get(i);
                        freshItemsContainer.add(createMiniCard(item));
                    }
                }
                freshItemsContainer.revalidate();
                freshItemsContainer.repaint();

            } catch (SQLException e) {
                System.err.println("Error refreshing home data: " + e.getMessage());
            }
        });
    }

    private JPanel createMiniCard(Item item) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Badges row
        JPanel bRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        bRow.setOpaque(false);
        bRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bRow.add(UITheme.createBadge(item.getCategory(), UITheme.COLOR_BLUSH, UITheme.COLOR_ROSE));
        bRow.add(UITheme.createBadge(item.getListingType(), UITheme.COLOR_ROSE, Color.WHITE));
        card.add(bRow);
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
        JLabel owner = new JLabel("Shared by " + (item.getOwnerName() != null ? item.getOwnerName() : "Student"));
        owner.setFont(UITheme.FONT_SMALL);
        owner.setForeground(UITheme.COLOR_MUTED);
        owner.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(owner);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(8));

        // View Item button
        JButton btnView = UITheme.createPrimaryButton("View Item");
        btnView.setFont(UITheme.FONT_SMALL);
        btnView.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnView.addActionListener(e -> {
            ItemDetailDialog dialog = new ItemDetailDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    item,
                    currentUser,
                    this::refreshData
            );
            dialog.setVisible(true);
        });
        card.add(btnView);

        return card;
    }
}
