package com.gestionsalles.app.ui;

import com.gestionsalles.app.model.Salle;
import com.gestionsalles.app.service.SalleService;
import com.gestionsalles.app.ui.component.RoundedPanel;
import com.gestionsalles.app.ui.component.Theme;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class SallePanel extends JPanel {

    private final SalleService salleService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Salle> currentSalles;
    private JTextField codeField, designationField;

    public SallePanel(SalleService salleService) {
        this.salleService = salleService;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        add(buildFormCard(), BorderLayout.SOUTH);
        refreshTable();
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Actualiser");
        refreshBtn.addActionListener(e -> refreshTable());
        panel.add(refreshBtn);
        return panel;
    }

    private RoundedPanel buildTableCard() {
        String[] columns = {"Code", "Designation"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Theme.styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                Salle selected = currentSalles.get(row);
                codeField.setText(selected.getCodesal());
                codeField.setEditable(false);
                designationField.setText(selected.getDesignation());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        RoundedPanel card = new RoundedPanel(new BorderLayout(), Theme.CARD_ARC, Theme.CARD_BG, Theme.CARD_BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(Theme.CARD_PADDING, Theme.CARD_PADDING, Theme.CARD_PADDING, Theme.CARD_PADDING));
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel buildFormCard() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        formPanel.setOpaque(false);

        codeField = new JTextField();
        designationField = new JTextField();

        formPanel.add(new JLabel("Code salle :"));
        formPanel.add(new JLabel("Designation :"));
        formPanel.add(codeField);
        formPanel.add(designationField);

        RoundedPanel card = new RoundedPanel(new BorderLayout(8, 10), Theme.CARD_ARC, Theme.CARD_BG, Theme.CARD_BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(Theme.CARD_PADDING, 16, Theme.CARD_PADDING, 16));
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buildFormButtons(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildFormButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.addActionListener(e -> saveSalle());

        JButton newBtn = new JButton("Nouveau");
        newBtn.addActionListener(e -> clearForm());

        JButton deleteBtn = new JButton("Supprimer");
        Theme.styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteSelectedSalle());

        panel.add(saveBtn);
        panel.add(newBtn);
        panel.add(deleteBtn);
        return panel;
    }

    private void saveSalle() {
        String code = codeField.getText().trim();
        String designation = designationField.getText().trim();

        if (code.isEmpty() || designation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le code et la désignation sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isNew = codeField.isEditable();

        try {
            if (isNew) {
                salleService.createSalle(new Salle(code, designation));
            } else {
                salleService.updateSalle(code, new Salle(code, designation));
            }
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this,
                    isNew ? "Salle ajoutée avec succès." : "Salle modifiée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedSalle() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne d'abord une salle dans le tableau.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Salle selected = currentSalles.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer la salle " + selected.getDesignation() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                salleService.deleteSalle(selected.getCodesal());
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Salle supprimée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        codeField.setText("");
        codeField.setEditable(true);
        designationField.setText("");
        table.clearSelection();
    }

    private void refreshTable() {
        this.currentSalles = salleService.getAllSalles();
        tableModel.setRowCount(0);
        for (Salle s : currentSalles) {
            tableModel.addRow(new Object[]{s.getCodesal(), s.getDesignation()});
        }
    }
}