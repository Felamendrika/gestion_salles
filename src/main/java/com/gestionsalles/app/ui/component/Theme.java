package com.gestionsalles.app.ui.component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class Theme {

    public static final Color CARD_BG = new Color(0xF7, 0xF8, 0xFA);
    public static final Color CARD_BORDER = new Color(0xE3, 0xE5, 0xE8);
    public static final Color TEXT_MUTED = new Color(0x70, 0x76, 0x7D);
    public static final Color ROW_ALT = new Color(0xF4, 0xF6, 0xF8);
    public static final Color SELECTION_BG = new Color(0xDD, 0xE8, 0xFC);
    public static final Color ACCENT = new Color(0x2F, 0x6F, 0xED);
    public static final Color DANGER = new Color(0xC0, 0x3B, 0x3B);
    public static final int CARD_ARC = 14;
    public static final int CARD_PADDING = 14;

    private Theme() {}

    // Appelée UNE SEULE FOIS, tout au début de main(), AVANT app.run(args).
    // C'est essentiel : Spring va créer tous les composants Swing dès app.run(args),
    // donc FlatLaf doit déjà être configuré avant, sinon ces réglages sont ignorés.
    public static void applyGlobalDefaults() {
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13)); // police légèrement plus grande -> champs plus hauts, partout

        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.focusWidth", 1);

        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.width", 12);

        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 26);

        UIManager.put("TabbedPane.tabHeight", 38);
        UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
        UIManager.put("TabbedPane.underlineColor", ACCENT);
        UIManager.put("TabbedPane.hoverColor", CARD_BG);

        UIManager.put("Button.margin", new Insets(8, 12, 8, 12));
        UIManager.put("TextField.margin", new Insets(6, 10, 6, 10));
        UIManager.put("Component.margin", new Insets(8, 10, 8, 10)); // combos, spinners
        UIManager.put("ComboBox.padding", new Insets(4, 8, 4, 8));
        UIManager.put("Spinner.padding", new Insets(4, 8, 4, 8));
    }

    public static void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        header.setBackground(CARD_BG);
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));

        table.setShowGrid(false);
        table.setRowHeight(28);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(Color.BLACK);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                return c;
            }
        });
    }

    public static void styleDangerButton(JButton button) {
        button.setForeground(DANGER);
    }

    // Génère une petite icône de fenêtre "GS" dessinée directement en code (pas besoin de fichier image)
    public static Image buildAppIcon() {
        int size = 32;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ACCENT);
        g2.fillRoundRect(0, 0, size, size, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GS";
        int textX = (size - fm.stringWidth(text)) / 2;
        int textY = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
        g2.dispose();
        return img;
    }
}