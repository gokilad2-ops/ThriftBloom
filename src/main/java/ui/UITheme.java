package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Styling and design system inspired by the Thrift Campus Exchange prototype.
 * Uses the signature Rose (#7B1E3A), Blush (#F0D9E4), Raspberry (#A83258) and Cream palette.
 */
public class UITheme {

    // Signature Palette
    public static final Color COLOR_ROSE       = new Color(0x7B, 0x1E, 0x3A); // #7B1E3A - Primary Rose
    public static final Color COLOR_ROSE_DARK  = new Color(0x4B, 0x13, 0x2C); // #4B132C - Deep Rose / Header
    public static final Color COLOR_ROSE_HOVER = new Color(0x8F, 0x25, 0x44);
    public static final Color COLOR_RASPBERRY  = new Color(0xA8, 0x32, 0x58); // #A83258 - Accent
    public static final Color COLOR_BLUSH      = new Color(0xF0, 0xD9, 0xE4); // #F0D9E4 - Soft Blush
    public static final Color COLOR_PAPER      = new Color(0xF8, 0xEA, 0xF0); // #F8EAF0 - App Background
    public static final Color COLOR_CREAM      = new Color(0xFF, 0xF5, 0xF7); // #FFF5F7 - Light panels
    public static final Color COLOR_CARD_BG    = new Color(0xFF, 0xF8, 0xF9); // #FFF8F9 - Cards
    public static final Color COLOR_LINE       = new Color(0xE4, 0xC0, 0xCE); // #E4C0CE - Subtle border
    public static final Color COLOR_INK        = new Color(0x26, 0x15, 0x1F); // #26151F - Text
    public static final Color COLOR_MUTED      = new Color(0x80, 0x6C, 0x79); // #806C79 - Subtitle text
    public static final Color COLOR_GOLD       = new Color(0xC5, 0xA0, 0x6A); // #C5A06A - Warm Accent
    public static final Color COLOR_AVAILABLE  = new Color(0x2E, 0x7D, 0x32); // Available green
    public static final Color COLOR_RESERVED   = new Color(0xC7, 0x77, 0x00); // Reserved orange/gold

    // Typography
    public static final Font FONT_BRAND_LARGE = new Font("Georgia", Font.BOLD, 22);
    public static final Font FONT_HEADER_1    = new Font("Georgia", Font.BOLD, 26);
    public static final Font FONT_HEADER_2    = new Font("Georgia", Font.BOLD, 20);
    public static final Font FONT_HEADER_3    = new Font("Georgia", Font.BOLD, 16);
    public static final Font FONT_TITLE       = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BODY        = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL       = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_KICKER      = new Font("Segoe UI", Font.BOLD, 10);
    public static final Font FONT_PRICE       = new Font("Georgia", Font.BOLD, 18);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_ROSE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(COLOR_ROSE, 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_ROSE_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_ROSE);
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(COLOR_ROSE);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(COLOR_LINE, 1, true),
                new EmptyBorder(9, 16, 9, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_BLUSH);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    public static JButton createSoftButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(COLOR_ROSE);
        btn.setBackground(COLOR_BLUSH);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_LINE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_BLUSH);
            }
        });
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(COLOR_INK);
        tf.setBackground(Color.WHITE);
        tf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_LINE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setForeground(COLOR_INK);
        pf.setBackground(Color.WHITE);
        pf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_LINE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return pf;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_LINE, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(FONT_KICKER);
        label.setForeground(fg);
        label.setBackground(bg);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(4, 8, 4, 8));
        return label;
    }

    public static JLabel createStatusBadge(String status) {
        String s = status != null ? status.toUpperCase() : "AVAILABLE";
        Color bg, fg;
        switch (s) {
            case "AVAILABLE":
                bg = new Color(0xE8, 0xF5, 0xE9);
                fg = COLOR_AVAILABLE;
                break;
            case "RESERVED":
                bg = new Color(0xFF, 0xF8, 0xE1);
                fg = COLOR_RESERVED;
                break;
            case "SOLD":
            case "DONATED":
                bg = new Color(0xEE, 0xEE, 0xEE);
                fg = new Color(0x61, 0x61, 0x61);
                break;
            case "PENDING":
                bg = new Color(0xFF, 0xEB, 0xEE);
                fg = COLOR_RASPBERRY;
                break;
            case "APPROVED":
                bg = new Color(0xE8, 0xF5, 0xE9);
                fg = COLOR_AVAILABLE;
                break;
            case "REJECTED":
                bg = new Color(0xFF, 0xEB, 0xEE);
                fg = new Color(0xC6, 0x28, 0x28);
                break;
            default:
                bg = COLOR_BLUSH;
                fg = COLOR_ROSE;
                break;
        }
        return createBadge(s, bg, fg);
    }
}
