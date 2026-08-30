package com.gestionsalles.app.ui.component;

import javax.swing.*;
import java.awt.*;

// Un JPanel qui se dessine lui-même avec des coins arrondis, au lieu du rectangle par défaut.
public class RoundedPanel extends JPanel {

    private final int arc;
    private final Color backgroundOverride;
    private final Color borderColor;

    public RoundedPanel(LayoutManager layout, int arc, Color backgroundOverride, Color borderColor) {
        super(layout);
        this.arc = arc;
        this.backgroundOverride = backgroundOverride;
        this.borderColor = borderColor;
        setOpaque(false); // important : on dessine nous-mêmes le fond, Swing ne doit pas le faire à notre place
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Anti-aliasing : lisse les bords arrondis, sinon ils apparaissent "en escalier" (crénelés)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundOverride != null ? backgroundOverride : getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        }

        g2.dispose();
        super.paintComponent(g); // laisse Swing dessiner les composants enfants (labels, tableaux...) par-dessus
    }
}
