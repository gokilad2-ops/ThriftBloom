package ui;

import model.Item;
import model.User;
import service.ItemService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * Add Item form allowing students to list academic items for SELL, DONATE, or EXCHANGE.
 */
public class AddItemPanel extends JPanel {

    private final User currentUser;
    private final ItemService itemService;
    private final Consumer<String> pageNavigator;

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> conditionCombo;
    private JComboBox<String> typeCombo;
    private JTextField priceField;
    private JTextArea exchangeDetailsArea;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JLabel errorLabel;

    public AddItemPanel(User currentUser, Consumer<String> pageNavigator) {
        this.currentUser = currentUser;
        this.pageNavigator = pageNavigator;
        this.itemService = new ItemService();

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_PAPER);

        initComponents();
    }

    private void initComponents() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UITheme.COLOR_PAPER);
        scrollContent.setBorder(new EmptyBorder(16, 24, 24, 24));

        // Header
        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kicker = new JLabel("LIST WITH LOVE ✿");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("Give it a second story");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel sub = new JLabel("Add the details so your fellow college students can find your item.");
        sub.setFont(UITheme.FONT_SUBTITLE);
        sub.setForeground(UITheme.COLOR_MUTED);

        head.add(kicker);
        head.add(title);
        head.add(sub);
        scrollContent.add(head);
        scrollContent.add(Box.createVerticalStrut(16));

        // Form Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(800, 680));

        // Row 1: Item Name & Category
        JPanel row1 = new JPanel(new GridLayout(1, 2, 16, 0));
        row1.setOpaque(false);
        nameField = UITheme.createTextField(20);
        String[] cats = {"Books", "Electronics", "Study Materials", "Lab Equipment", "Stationery", "Clothing", "Furniture", "Other"};
        categoryCombo = new JComboBox<>(cats);
        categoryCombo.setFont(UITheme.FONT_BODY);
        categoryCombo.setBackground(Color.WHITE);

        row1.add(createFieldGroup("ITEM NAME / TITLE *", nameField));
        row1.add(createFieldGroup("CATEGORY *", categoryCombo));
        card.add(row1);
        card.add(Box.createVerticalStrut(12));

        // Row 2: Condition, Listing Type, Price
        JPanel row2 = new JPanel(new GridLayout(1, 3, 14, 0));
        row2.setOpaque(false);

        String[] conditions = {"Like new", "Very good", "Good", "Fair"};
        conditionCombo = new JComboBox<>(conditions);
        conditionCombo.setFont(UITheme.FONT_BODY);
        conditionCombo.setBackground(Color.WHITE);
        conditionCombo.setSelectedItem("Good");

        String[] types = {"SELL", "DONATE", "EXCHANGE"};
        typeCombo = new JComboBox<>(types);
        typeCombo.setFont(UITheme.FONT_BODY);
        typeCombo.setBackground(Color.WHITE);

        priceField = UITheme.createTextField(10);
        priceField.setText("100.00");

        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            if ("DONATE".equalsIgnoreCase(selected)) {
                priceField.setText("0.00");
                priceField.setEnabled(false);
                exchangeDetailsArea.setEnabled(false);
            } else if ("EXCHANGE".equalsIgnoreCase(selected)) {
                priceField.setText("0.00");
                priceField.setEnabled(false);
                exchangeDetailsArea.setEnabled(true);
            } else {
                priceField.setEnabled(true);
                if ("0.00".equals(priceField.getText())) priceField.setText("100.00");
                exchangeDetailsArea.setEnabled(false);
            }
        });

        row2.add(createFieldGroup("CONDITION *", conditionCombo));
        row2.add(createFieldGroup("LISTING TYPE *", typeCombo));
        row2.add(createFieldGroup("PRICE (₹) *", priceField));
        card.add(row2);
        card.add(Box.createVerticalStrut(12));

        // Row 3: Exchange Details
        exchangeDetailsArea = new JTextArea(2, 20);
        exchangeDetailsArea.setFont(UITheme.FONT_BODY);
        exchangeDetailsArea.setLineWrap(true);
        exchangeDetailsArea.setWrapStyleWord(true);
        exchangeDetailsArea.setEnabled(false);
        exchangeDetailsArea.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane exScroll = new JScrollPane(exchangeDetailsArea);
        exScroll.setPreferredSize(new Dimension(700, 50));
        exScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        card.add(createFieldGroup("EXCHANGE REQUIREMENTS (if listing type is EXCHANGE)", exScroll));
        card.add(Box.createVerticalStrut(12));

        // Row 4: Description
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(UITheme.FONT_BODY);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(700, 75));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.add(createFieldGroup("DESCRIPTION (condition notes, semester, reason for giving) *", descScroll));
        card.add(Box.createVerticalStrut(12));

        // Row 5: Image file path / browse
        JPanel imageRow = new JPanel(new BorderLayout(8, 0));
        imageRow.setOpaque(false);
        imagePathField = UITheme.createTextField(20);
        imagePathField.setToolTipText("Optional local photo path");

        JButton browseBtn = UITheme.createSecondaryButton("Browse...");
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Item Image");
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                imagePathField.setText(file.getAbsolutePath());
            }
        });
        imageRow.add(imagePathField, BorderLayout.CENTER);
        imageRow.add(browseBtn, BorderLayout.EAST);
        card.add(createFieldGroup("ITEM PHOTO PATH (Optional)", imageRow));
        card.add(Box.createVerticalStrut(14));

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(new Color(0xD3, 0x2F, 0x2F));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));

        // Submit & Cancel buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        JButton clearBtn = UITheme.createSecondaryButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());

        JButton submitBtn = UITheme.createPrimaryButton("✿ Publish My Listing");
        submitBtn.addActionListener(e -> handleSubmit());

        actionRow.add(clearBtn);
        actionRow.add(submitBtn);
        card.add(actionRow);

        scrollContent.add(card);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_KICKER);
        lbl.setForeground(UITheme.COLOR_ROSE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(component);
        return p;
    }

    private void clearForm() {
        nameField.setText("");
        categoryCombo.setSelectedIndex(0);
        conditionCombo.setSelectedItem("Good");
        typeCombo.setSelectedIndex(0);
        priceField.setText("100.00");
        priceField.setEnabled(true);
        exchangeDetailsArea.setText("");
        exchangeDetailsArea.setEnabled(false);
        descriptionArea.setText("");
        imagePathField.setText("");
        errorLabel.setText(" ");
    }

    private void handleSubmit() {
        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String condition = (String) conditionCombo.getSelectedItem();
        String type = (String) typeCombo.getSelectedItem();
        String priceText = priceField.getText().trim();
        String exchangeDetails = exchangeDetailsArea.getText().trim();
        String description = descriptionArea.getText().trim();
        String imagePath = imagePathField.getText().trim();

        double price = 0.0;
        if ("SELL".equalsIgnoreCase(type)) {
            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) {
                    errorLabel.setText("Please enter a positive price for items listed for sale.");
                    return;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Please enter a valid numeric price (e.g. 150.00).");
                return;
            }
        }

        try {
            Item created = itemService.addItem(
                    name, description, category, condition, type, price,
                    exchangeDetails, imagePath, currentUser
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Your listing \"" + created.getName() + "\" is live on thrift!\nMay it find a lovely new owner ✿",
                    "Item Published Successfully",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            if (pageNavigator != null) {
                pageNavigator.accept("MY_LISTINGS");
            }

        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}
