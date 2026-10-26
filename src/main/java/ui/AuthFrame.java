package ui;

import model.User;
import service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Authentication window with Login and Signup tabs,
 * capturing the exact visual styling of the Thrift Campus Exchange prototype.
 */
public class AuthFrame extends JFrame {

    private final AuthenticationService authService;

    // Login components
    private JTextField loginIdentifierField;
    private JPasswordField loginPasswordField;
    private JLabel loginErrorLabel;

    // Signup components
    private JTextField signupNameField;
    private JTextField signupRegField;
    private JTextField signupEmailField;
    private JTextField signupDeptField;
    private JComboBox<String> signupYearCombo;
    private JTextField signupPhoneField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmField;
    private JLabel signupErrorLabel;

    // Card Layout to switch tabs
    private CardLayout formCardLayout;
    private JPanel formCardsPanel;
    private JButton tabLoginBtn;
    private JButton tabSignupBtn;

    public AuthFrame() {
        this.authService = new AuthenticationService();

        setTitle("thrift — The Campus Exchange (Authentication)");
        setSize(920, 640);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_PAPER);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel container = new JPanel(new GridLayout(1, 2, 0, 0));
        container.setBackground(UITheme.COLOR_PAPER);

        // Left Pane: Editorial Campus Art & Brand
        JPanel leftPane = createBrandArtPane();

        // Right Pane: Form with Login / Signup switch
        JPanel rightPane = createFormPane();

        container.add(leftPane);
        container.add(rightPane);

        add(container, BorderLayout.CENTER);
    }

    private JPanel createBrandArtPane() {
        JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Rose Gradient
                GradientPaint gp = new GradientPaint(
                        0, 0, UITheme.COLOR_ROSE_DARK,
                        getWidth(), getHeight(), UITheme.COLOR_ROSE
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative floral accents
                g2.setColor(new Color(255, 255, 255, 12));
                g2.setFont(new Font("Georgia", Font.PLAIN, 180));
                g2.drawString("✿", getWidth() - 140, getHeight() - 20);
                g2.dispose();
            }
        };

        pane.setLayout(new BorderLayout());
        pane.setBorder(new EmptyBorder(35, 35, 35, 35));

        // Brand header
        JPanel brandHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brandHeader.setOpaque(false);

        JLabel brandIcon = new JLabel("↗  ");
        brandIcon.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandIcon.setForeground(UITheme.COLOR_BLUSH);

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);

        JLabel brandTitle = new JLabel("thrift");
        brandTitle.setFont(new Font("Georgia", Font.BOLD, 28));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("THE CAMPUS EXCHANGE");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        brandSub.setForeground(UITheme.COLOR_BLUSH);

        brandText.add(brandTitle);
        brandText.add(brandSub);

        brandHeader.add(brandIcon);
        brandHeader.add(brandText);
        pane.add(brandHeader, BorderLayout.NORTH);

        // Center: Editorial message & visual badges
        JPanel centerCopy = new JPanel();
        centerCopy.setOpaque(false);
        centerCopy.setLayout(new BoxLayout(centerCopy, BoxLayout.Y_AXIS));
        centerCopy.setBorder(new EmptyBorder(25, 0, 0, 0));

        JLabel kicker = new JLabel("STUDENT-TO-STUDENT EXCHANGE");
        kicker.setFont(new Font("Segoe UI", Font.BOLD, 10));
        kicker.setForeground(UITheme.COLOR_GOLD);

        JLabel headline = new JLabel("<html>Make room for<br>something useful.</html>");
        headline.setFont(new Font("Georgia", Font.BOLD, 30));
        headline.setForeground(Color.WHITE);
        headline.setBorder(new EmptyBorder(12, 0, 14, 0));

        JLabel desc = new JLabel("<html>Pass on textbooks, drawing kits, calculators and lab coats to fellow students.<br><br>Save money, reduce waste, and keep campus circular.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(UITheme.COLOR_BLUSH);

        // Visual category tiles
        JPanel tilesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        tilesPanel.setOpaque(false);
        tilesPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        tilesPanel.add(createTile("📚", "PASS IT ON"));
        tilesPanel.add(createTile("💻", "FIND ESSENTIALS"));
        tilesPanel.add(createTile("🥼", "SAVE MORE"));

        centerCopy.add(kicker);
        centerCopy.add(headline);
        centerCopy.add(desc);
        centerCopy.add(tilesPanel);

        pane.add(centerCopy, BorderLayout.CENTER);

        // Bottom tagline
        JLabel bottomTag = new JLabel("BUY · SELL · DONATE · REUSE ✿ CAMPUS THRIFT");
        bottomTag.setFont(new Font("Segoe UI", Font.BOLD, 9));
        bottomTag.setForeground(UITheme.COLOR_BLUSH);
        pane.add(bottomTag, BorderLayout.SOUTH);

        return pane;
    }

    private JPanel createTile(String emoji, String text) {
        JPanel tile = new JPanel();
        tile.setBackground(new Color(255, 255, 255, 24));
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBorder(new EmptyBorder(12, 8, 12, 8));

        JLabel lblEmoji = new JLabel(emoji, SwingConstants.CENTER);
        lblEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblEmoji.setForeground(Color.WHITE);

        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 8));
        lblText.setForeground(UITheme.COLOR_BLUSH);
        lblText.setBorder(new EmptyBorder(5, 0, 0, 0));

        tile.add(lblEmoji);
        tile.add(lblText);
        return tile;
    }

    private JPanel createFormPane() {
        JPanel rightPane = new JPanel(new BorderLayout());
        rightPane.setBackground(UITheme.COLOR_CREAM);
        rightPane.setBorder(new EmptyBorder(25, 35, 25, 35));

        // Top tab switcher
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setOpaque(false);

        JLabel subKicker = new JLabel("CAMPUS COMMUNITY");
        subKicker.setFont(UITheme.FONT_KICKER);
        subKicker.setForeground(UITheme.COLOR_RASPBERRY);

        JPanel tabButtons = new JPanel(new GridLayout(1, 2, 8, 0));
        tabButtons.setBackground(UITheme.COLOR_BLUSH);
        tabButtons.setBorder(new EmptyBorder(4, 4, 4, 4));

        tabLoginBtn = new JButton("Log In");
        tabLoginBtn.setFont(UITheme.FONT_BODY_BOLD);
        tabLoginBtn.setFocusPainted(false);
        tabLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabSignupBtn = new JButton("Create Account");
        tabSignupBtn.setFont(UITheme.FONT_BODY_BOLD);
        tabSignupBtn.setFocusPainted(false);
        tabSignupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabLoginBtn.addActionListener(e -> showTab("LOGIN"));
        tabSignupBtn.addActionListener(e -> showTab("SIGNUP"));

        tabButtons.add(tabLoginBtn);
        tabButtons.add(tabSignupBtn);

        tabContainer.add(subKicker, BorderLayout.NORTH);
        tabContainer.add(tabButtons, BorderLayout.SOUTH);
        tabButtons.setBorder(new CompoundBorder(new EmptyBorder(8, 0, 10, 0), tabButtons.getBorder()));

        rightPane.add(tabContainer, BorderLayout.NORTH);

        // Center card with Login and Signup views
        formCardLayout = new CardLayout();
        formCardsPanel = new JPanel(formCardLayout);
        formCardsPanel.setOpaque(false);

        formCardsPanel.add(createLoginCard(), "LOGIN");
        formCardsPanel.add(createSignupCard(), "SIGNUP");

        rightPane.add(formCardsPanel, BorderLayout.CENTER);

        // Activate default tab: Login
        showTab("LOGIN");

        return rightPane;
    }

    private void showTab(String tabName) {
        if ("LOGIN".equals(tabName)) {
            tabLoginBtn.setBackground(UITheme.COLOR_ROSE);
            tabLoginBtn.setForeground(Color.WHITE);
            tabLoginBtn.setOpaque(true);

            tabSignupBtn.setBackground(UITheme.COLOR_BLUSH);
            tabSignupBtn.setForeground(UITheme.COLOR_ROSE);
            tabSignupBtn.setOpaque(true);

            formCardLayout.show(formCardsPanel, "LOGIN");
            if (loginErrorLabel != null) loginErrorLabel.setText("");
        } else {
            tabSignupBtn.setBackground(UITheme.COLOR_ROSE);
            tabSignupBtn.setForeground(Color.WHITE);
            tabSignupBtn.setOpaque(true);

            tabLoginBtn.setBackground(UITheme.COLOR_BLUSH);
            tabLoginBtn.setForeground(UITheme.COLOR_ROSE);
            tabLoginBtn.setOpaque(true);

            formCardLayout.show(formCardsPanel, "SIGNUP");
            if (signupErrorLabel != null) signupErrorLabel.setText("");
        }
    }

    private JPanel createLoginCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel title = new JLabel("Welcome back to thrift");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel subtitle = new JLabel("Enter your registration number or college email to continue.");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.COLOR_MUTED);
        subtitle.setBorder(new EmptyBorder(4, 0, 15, 0));

        panel.add(title);
        panel.add(subtitle);

        // Identifier field
        JLabel lblId = new JLabel("REGISTRATION NUMBER OR EMAIL");
        lblId.setFont(UITheme.FONT_KICKER);
        lblId.setForeground(UITheme.COLOR_ROSE);
        loginIdentifierField = UITheme.createTextField(20);
        loginIdentifierField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        panel.add(lblId);
        panel.add(Box.createVerticalStrut(4));
        panel.add(loginIdentifierField);
        panel.add(Box.createVerticalStrut(14));

        // Password field
        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setFont(UITheme.FONT_KICKER);
        lblPass.setForeground(UITheme.COLOR_ROSE);
        loginPasswordField = UITheme.createPasswordField(20);
        loginPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(4));
        panel.add(loginPasswordField);
        panel.add(Box.createVerticalStrut(8));

        // Error message label
        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setFont(UITheme.FONT_SMALL);
        loginErrorLabel.setForeground(new Color(0xD3, 0x2F, 0x2F));
        panel.add(loginErrorLabel);
        panel.add(Box.createVerticalStrut(10));

        // Submit button
        JButton btnLogin = UITheme.createPrimaryButton("Continue to thrift →");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(this::handleLogin);
        panel.add(btnLogin);

        // Demo hint note
        panel.add(Box.createVerticalStrut(16));
        JPanel note = new JPanel(new BorderLayout());
        note.setBackground(UITheme.COLOR_PAPER);
        note.setBorder(new CompoundBorder(
                new LineBorder(UITheme.COLOR_LINE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        note.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel noteText = new JLabel("<html><b>Demo account:</b> aadhya.raman@campus.edu or 2025501042<br><b>Password:</b> student123</html>");
        noteText.setFont(UITheme.FONT_SMALL);
        noteText.setForeground(UITheme.COLOR_MUTED);
        note.add(noteText, BorderLayout.CENTER);
        panel.add(note);

        // Enter key listeners
        loginPasswordField.addActionListener(this::handleLogin);
        loginIdentifierField.addActionListener(e -> loginPasswordField.requestFocusInWindow());

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createSignupCard() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel title = new JLabel("Join thrift");
        title.setFont(UITheme.FONT_HEADER_2);
        title.setForeground(UITheme.COLOR_ROSE);

        JLabel subtitle = new JLabel("Create your student profile to start exchanging campus items.");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.COLOR_MUTED);
        subtitle.setBorder(new EmptyBorder(4, 0, 12, 0));

        scrollContent.add(title);
        scrollContent.add(subtitle);

        // Form fields grid (2 columns for neat layout)
        JPanel grid = new JPanel(new GridLayout(4, 2, 10, 8));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // Full Name
        JPanel pName = createFieldGroup("FULL NAME *", signupNameField = UITheme.createTextField(15));
        // Registration Number
        JPanel pReg = createFieldGroup("REGISTRATION NO *", signupRegField = UITheme.createTextField(15));
        // Email
        JPanel pEmail = createFieldGroup("COLLEGE EMAIL *", signupEmailField = UITheme.createTextField(15));
        // Department
        JPanel pDept = createFieldGroup("DEPARTMENT", signupDeptField = UITheme.createTextField(15));
        signupDeptField.setText("CSE");

        // Year of Study
        signupYearCombo = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year", "All Years"});
        signupYearCombo.setFont(UITheme.FONT_BODY);
        signupYearCombo.setBackground(Color.WHITE);
        JPanel pYear = createFieldGroup("YEAR OF STUDY", signupYearCombo);

        // Phone
        JPanel pPhone = createFieldGroup("PHONE NUMBER", signupPhoneField = UITheme.createTextField(15));

        // Password
        JPanel pPass = createFieldGroup("PASSWORD *", signupPasswordField = UITheme.createPasswordField(15));
        // Confirm Password
        JPanel pConf = createFieldGroup("CONFIRM PASSWORD *", signupConfirmField = UITheme.createPasswordField(15));

        grid.add(pName);
        grid.add(pReg);
        grid.add(pEmail);
        grid.add(pDept);
        grid.add(pYear);
        grid.add(pPhone);
        grid.add(pPass);
        grid.add(pConf);

        scrollContent.add(grid);
        scrollContent.add(Box.createVerticalStrut(8));

        // Error Label
        signupErrorLabel = new JLabel(" ");
        signupErrorLabel.setFont(UITheme.FONT_SMALL);
        signupErrorLabel.setForeground(new Color(0xD3, 0x2F, 0x2F));
        scrollContent.add(signupErrorLabel);
        scrollContent.add(Box.createVerticalStrut(6));

        // Submit Button
        JButton btnSignup = UITheme.createPrimaryButton("Create Account →");
        btnSignup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSignup.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSignup.addActionListener(this::handleSignup);
        scrollContent.add(btnSignup);

        JScrollPane sp = new JScrollPane(scrollContent);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

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

    private void handleLogin(ActionEvent e) {
        String identifier = loginIdentifierField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        loginErrorLabel.setText("Authenticating with MySQL database...");
        loginErrorLabel.setForeground(UITheme.COLOR_MUTED);

        SwingUtilities.invokeLater(() -> {
            try {
                User user = authService.login(identifier, password);
                loginErrorLabel.setText("");

                // Successful login -> open MainFrame
                openMainFrame(user);
            } catch (Exception ex) {
                loginErrorLabel.setForeground(new Color(0xD3, 0x2F, 0x2F));
                loginErrorLabel.setText("<html>" + ex.getMessage() + "</html>");
            }
        });
    }

    private void handleSignup(ActionEvent e) {
        String name = signupNameField.getText().trim();
        String regNo = signupRegField.getText().trim();
        String email = signupEmailField.getText().trim();
        String dept = signupDeptField.getText().trim();
        String year = (String) signupYearCombo.getSelectedItem();
        String phone = signupPhoneField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirm = new String(signupConfirmField.getPassword());

        signupErrorLabel.setText("Creating account in database...");
        signupErrorLabel.setForeground(UITheme.COLOR_MUTED);

        SwingUtilities.invokeLater(() -> {
            try {
                User user = authService.register(name, regNo, email, password, confirm, dept, year, phone);
                signupErrorLabel.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Welcome to thrift, " + user.getName() + "!\nYour account has been created successfully.",
                        "Account Created",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Auto login after signup
                AuthenticationService.setCurrentUser(user);
                openMainFrame(user);

            } catch (Exception ex) {
                signupErrorLabel.setForeground(new Color(0xD3, 0x2F, 0x2F));
                signupErrorLabel.setText("<html>" + ex.getMessage() + "</html>");
            }
        });
    }

    private void openMainFrame(User user) {
        dispose();
        MainFrame mainFrame = new MainFrame(user);
        mainFrame.setVisible(true);
    }
}
