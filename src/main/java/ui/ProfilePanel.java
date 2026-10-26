package ui;

import model.User;
import service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Profile management view matching the Thrift prototype's card and cover design.
 */
public class ProfilePanel extends JPanel {

    private final User currentUser;
    private final AuthenticationService authService;
    private final Runnable onProfileUpdated;

    private JTextField nameField;
    private JTextField regField;
    private JTextField emailField;
    private JTextField deptField;
    private JComboBox<String> yearCombo;
    private JTextField phoneField;
    private JButton editBtn;
    private JButton saveBtn;
    private boolean isEditing = false;

    public ProfilePanel(User currentUser, Runnable onProfileUpdated) {
        this.currentUser = currentUser;
        this.onProfileUpdated = onProfileUpdated;
        this.authService = new AuthenticationService();

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

        JLabel kicker = new JLabel("A LITTLE ABOUT YOU ✿");
        kicker.setFont(UITheme.FONT_KICKER);
        kicker.setForeground(UITheme.COLOR_RASPBERRY);

        JLabel title = new JLabel("Your campus identity");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel sub = new JLabel("Keep your contact details up to date for smooth student handoffs.");
        sub.setFont(UITheme.FONT_SUBTITLE);
        sub.setForeground(UITheme.COLOR_MUTED);

        head.add(kicker);
        head.add(title);
        head.add(sub);
        scrollContent.add(head);
        scrollContent.add(Box.createVerticalStrut(16));

        // 2-Column layout: Profile Cover on Left, Form on Right
        JPanel layout = new JPanel(new BorderLayout(18, 0));
        layout.setOpaque(false);
        layout.setAlignmentX(Component.LEFT_ALIGNMENT);
        layout.setMaximumSize(new Dimension(840, 380));

        // Left: Cover Card
        JPanel cover = new JPanel() {
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
        cover.setPreferredSize(new Dimension(240, 360));
        cover.setLayout(new BoxLayout(cover, BoxLayout.Y_AXIS));
        cover.setBorder(new EmptyBorder(30, 20, 20, 20));
        cover.setOpaque(false);

        // Circular Avatar Initial
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_BLUSH);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.COLOR_ROSE);
                g2.setFont(new Font("Georgia", Font.BOLD, 32));
                String initial = currentUser.getName() != null && !currentUser.getName().isEmpty()
                        ? currentUser.getName().substring(0, 1).toUpperCase() : "U";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(72, 72));
        avatar.setMaximumSize(new Dimension(72, 72));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatar.setOpaque(false);

        JLabel covName = new JLabel(currentUser.getName(), SwingConstants.CENTER);
        covName.setFont(new Font("Georgia", Font.BOLD, 18));
        covName.setForeground(Color.WHITE);
        covName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel covReg = new JLabel(currentUser.getRegistrationNumber(), SwingConstants.CENTER);
        covReg.setFont(UITheme.FONT_SUBTITLE);
        covReg.setForeground(UITheme.COLOR_BLUSH);
        covReg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel covTag = new JLabel("✿ MEMBER OF THRIFT ✿", SwingConstants.CENTER);
        covTag.setFont(UITheme.FONT_KICKER);
        covTag.setForeground(UITheme.COLOR_GOLD);
        covTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel covQuote = new JLabel("<html><center><i>“Reuse today.<br>Make room for tomorrow.”</i></center></html>", SwingConstants.CENTER);
        covQuote.setFont(new Font("Georgia", Font.ITALIC, 14));
        covQuote.setForeground(UITheme.COLOR_BLUSH);
        covQuote.setAlignmentX(Component.CENTER_ALIGNMENT);

        cover.add(avatar);
        cover.add(Box.createVerticalStrut(14));
        cover.add(covName);
        cover.add(Box.createVerticalStrut(4));
        cover.add(covReg);
        cover.add(Box.createVerticalStrut(8));
        cover.add(covTag);
        cover.add(Box.createVerticalGlue());
        cover.add(covQuote);

        layout.add(cover, BorderLayout.WEST);

        // Right: Form Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel formGrid = new JPanel(new GridLayout(3, 2, 14, 12));
        formGrid.setOpaque(false);

        nameField = UITheme.createTextField(15);
        nameField.setText(currentUser.getName());
        nameField.setEnabled(false);

        regField = UITheme.createTextField(15);
        regField.setText(currentUser.getRegistrationNumber());
        regField.setEnabled(false); // Fixed ID

        emailField = UITheme.createTextField(15);
        emailField.setText(currentUser.getEmail());
        emailField.setEnabled(false);

        deptField = UITheme.createTextField(15);
        deptField.setText(currentUser.getDepartment());
        deptField.setEnabled(false);

        String[] years = {"1st Year", "2nd Year", "3rd Year", "4th Year", "All Years"};
        yearCombo = new JComboBox<>(years);
        yearCombo.setFont(UITheme.FONT_BODY);
        yearCombo.setBackground(Color.WHITE);
        yearCombo.setSelectedItem(currentUser.getYearOfStudy());
        yearCombo.setEnabled(false);

        phoneField = UITheme.createTextField(15);
        phoneField.setText(currentUser.getPhone());
        phoneField.setEnabled(false);

        formGrid.add(createFieldGroup("FULL NAME", nameField));
        formGrid.add(createFieldGroup("REGISTRATION NO (FIXED)", regField));
        formGrid.add(createFieldGroup("COLLEGE EMAIL", emailField));
        formGrid.add(createFieldGroup("DEPARTMENT", deptField));
        formGrid.add(createFieldGroup("YEAR OF STUDY", yearCombo));
        formGrid.add(createFieldGroup("PHONE NUMBER", phoneField));

        card.add(formGrid);
        card.add(Box.createVerticalStrut(20));

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        editBtn = UITheme.createSecondaryButton("✎ Edit Profile");
        editBtn.addActionListener(e -> toggleEdit(true));

        saveBtn = UITheme.createPrimaryButton("Save Changes");
        saveBtn.setVisible(false);
        saveBtn.addActionListener(e -> handleSaveProfile());

        btnRow.add(editBtn);
        btnRow.add(saveBtn);
        card.add(btnRow);

        layout.add(card, BorderLayout.CENTER);
        scrollContent.add(layout);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void toggleEdit(boolean enable) {
        isEditing = enable;
        nameField.setEnabled(enable);
        deptField.setEnabled(enable);
        yearCombo.setEnabled(enable);
        phoneField.setEnabled(enable);

        editBtn.setVisible(!enable);
        saveBtn.setVisible(enable);
    }

    private void handleSaveProfile() {
        String newName = nameField.getText().trim();
        String newDept = deptField.getText().trim();
        String newYear = (String) yearCombo.getSelectedItem();
        String newPhone = phoneField.getText().trim();

        if (newName.length() < 2) {
            JOptionPane.showMessageDialog(this, "Name must have at least 2 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.setName(newName);
        currentUser.setDepartment(newDept);
        currentUser.setYearOfStudy(newYear);
        currentUser.setPhone(newPhone);

        try {
            boolean ok = authService.updateProfile(currentUser);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully! ✿", "Saved", JOptionPane.INFORMATION_MESSAGE);
                toggleEdit(false);
                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_KICKER);
        lbl.setForeground(UITheme.COLOR_ROSE);

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }
}
