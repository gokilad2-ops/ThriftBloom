import database.DatabaseInitializer;
import ui.AuthFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Application Entry Point for thrift — The Campus Exchange.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  thrift — The Campus Exchange Desktop App");
        System.out.println("  Starting up...");
        System.out.println("=================================================");

        // Setup Look and Feel (FlatLaf if present, or System Look & Feel)
        setupLookAndFeel();

        // Initialize Database & Tables
        boolean dbReady = DatabaseInitializer.initialize();
        if (!dbReady) {
            System.err.println("WARNING: Could not connect to MySQL database.");
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Could not connect to MySQL server at localhost:3306.\n\n" +
                        "Please make sure:\n" +
                        "1. MySQL Server 8.0 service is running.\n" +
                        "2. Credentials in db.properties are correct.\n\n" +
                        "Would you like to try launching the application anyway?",
                        "Database Connection Notice — thrift",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    launchAuth();
                } else {
                    System.exit(1);
                }
            });
            return;
        }

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(Main::launchAuth);
    }

    private static void launchAuth() {
        AuthFrame authFrame = new AuthFrame();
        authFrame.setVisible(true);
    }

    private static void setupLookAndFeel() {
        try {
            // Attempt to load FlatLaf Light Look and Feel
            Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            Object flatLafInstance = flatLafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel((LookAndFeel) flatLafInstance);
            System.out.println("✓ Modern FlatLaf theme initialized.");
        } catch (Throwable t) {
            // Fallback to System Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("✓ System Look & Feel initialized.");
            } catch (Exception ignored) {}
        }

        // Global font and UI styling tweaks
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 8);
    }
}
