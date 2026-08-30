package com.gestionsalles.app.ui;

import com.gestionsalles.app.service.DashboardStats;
import com.gestionsalles.app.service.StatsService;
import com.gestionsalles.app.ui.component.RoundedPanel;
import com.gestionsalles.app.ui.component.Theme;
import org.springframework.stereotype.Component;
import com.gestionsalles.app.ui.component.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

@Component
public class StatsPanel extends JPanel {

    private final StatsService statsService;

    private JLabel totalProfsLabel;
    private JLabel totalSallesLabel;
    private JLabel totalOccupationsLabel;
    private JLabel salleTopLabel;
    private JLabel profTopLabel;
    private DefaultTableModel gradeTableModel;

    public StatsPanel(StatsService statsService) {
        this.statsService = statsService;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        refreshStats();
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Actualiser");
        refreshBtn.addActionListener(e -> refreshStats());
        panel.add(refreshBtn);
        return panel;
    }

    private JPanel buildContent() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 12, 12));
        cardsRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);   // <-- corrigé
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        totalProfsLabel = new JLabel();
        totalSallesLabel = new JLabel();
        totalOccupationsLabel = new JLabel();
        cardsRow.add(buildStatCard("Professeurs", totalProfsLabel));
        cardsRow.add(buildStatCard("Salles", totalSallesLabel));
        cardsRow.add(buildStatCard("Occupations", totalOccupationsLabel));

        RoundedPanel classementCard = new RoundedPanel(new BorderLayout(4, 8), Theme.CARD_ARC, Theme.CARD_BG, Theme.CARD_BORDER);
        classementCard.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);   // <-- corrigé
        classementCard.setBorder(BorderFactory.createEmptyBorder(Theme.CARD_PADDING, 16, Theme.CARD_PADDING, 16));
        JPanel classementLines = new JPanel(new GridLayout(2, 1, 4, 4));
        classementLines.setOpaque(false);
        salleTopLabel = new JLabel();
        profTopLabel = new JLabel();
        classementLines.add(salleTopLabel);
        classementLines.add(profTopLabel);
        classementCard.add(sectionTitle("CLASSEMENT"), BorderLayout.NORTH);
        classementCard.add(classementLines, BorderLayout.CENTER);

        RoundedPanel gradeCard = new RoundedPanel(new BorderLayout(4, 8), Theme.CARD_ARC, Theme.CARD_BG, Theme.CARD_BORDER);
        gradeCard.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);   // <-- corrigé
        gradeCard.setBorder(BorderFactory.createEmptyBorder(Theme.CARD_PADDING, 16, Theme.CARD_PADDING, 16));
        String[] columns = {"Grade", "Nombre de professeurs"};
        gradeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable gradeTable = new JTable(gradeTableModel);
        gradeTable.setRowHeight(24);
        gradeTable.setShowGrid(false);
        Theme.styleTable(gradeTable);
        JScrollPane gradeScroll = new JScrollPane(gradeTable);
        gradeScroll.setBorder(null);
        gradeCard.add(sectionTitle("RÉPARTITION PAR GRADE"), BorderLayout.NORTH);
        gradeCard.add(gradeScroll, BorderLayout.CENTER);

        container.add(cardsRow);
        container.add(Box.createVerticalStrut(12));
        container.add(classementCard);
        container.add(Box.createVerticalStrut(12));
        container.add(gradeCard);

        return container;
    }

    private RoundedPanel buildStatCard(String title, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(4, 4), Theme.CARD_ARC, Theme.CARD_BG, Theme.CARD_BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(Theme.CARD_PADDING, 16, Theme.CARD_PADDING, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 12f));
        titleLabel.setForeground(Theme.TEXT_MUTED);

        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        label.setForeground(Theme.TEXT_MUTED);
        return label;
    }

    public void refreshStats() {
        DashboardStats stats = statsService.computeStats();

        totalProfsLabel.setText(String.valueOf(stats.getTotalProfs()));
        totalSallesLabel.setText(String.valueOf(stats.getTotalSalles()));
        totalOccupationsLabel.setText(String.valueOf(stats.getTotalOccupations()));
        salleTopLabel.setText("Salle la plus utilisée : " + stats.getSalleLaPlusUtilisee());
        profTopLabel.setText("Professeur le plus actif : " + stats.getProfLePlusActif());

        gradeTableModel.setRowCount(0);
        for (Map.Entry<String, Long> entry : stats.getRepartitionParGrade().entrySet()) {
            gradeTableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }
}