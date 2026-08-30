package com.gestionsalles.app.ui;

import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.service.ProfService;
import com.gestionsalles.app.ui.component.RoundedPanel;
import com.gestionsalles.app.ui.component.Theme;
import org.springframework.stereotype.Component;
import com.gestionsalles.app.ui.component.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class ProfPanel extends JPanel {

    private final ProfService profService;

    private JTextField searchField;
    private JComboBox<String> gradeFilterCombo;

    private JTable table;
    private DefaultTableModel tableModel;
    private List<Prof> currentProfs;

    private JTextField codeField, nomField, prenomField, gradeField;

    public ProfPanel(ProfService profService) {
        this.profService = profService;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildSearchBar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        add(buildFormCard(), BorderLayout.SOUTH);

        refreshTable(profService.getAllProfs());
        refreshGradeFilter();
    }

    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Rechercher");
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                refreshTable(profService.getAllProfs());
            } else {
                refreshTable(profService.searchProfs(query));
            }
        });

        gradeFilterCombo = new JComboBox<>();
        gradeFilterCombo.addItem("Tous les grades");
        gradeFilterCombo.addActionListener(e -> {
            String selected = (String) gradeFilterCombo.getSelectedItem();
            if (selected == null || selected.equals("Tous les grades")) {
                refreshTable(profService.getAllProfs());
            } else {
                refreshTable(profService.filterByGrade(selected));
            }
        });

        JButton refreshBtn = new JButton("Actualiser");
        refreshBtn.addActionListener(e -> {
            refreshTable(profService.getAllProfs());
            refreshGradeFilter();
        });

        panel.add(new JLabel("Recherche (code ou nom) :"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(new JLabel("Filtre grade :"));
        panel.add(gradeFilterCombo);
        panel.add(refreshBtn);
        return panel;
    }

    private RoundedPanel buildTableCard() {
        String[] columns = {"Code", "Nom", "Prenom", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setShowGrid(false);
        Theme.styleTable(table);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                Prof selected = currentProfs.get(row);
                codeField.setText(selected.getCodeprof());
                codeField.setEditable(false);
                nomField.setText(selected.getNom());
                prenomField.setText(selected.getPrenom());
                gradeField.setText(selected.getGrade());
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
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        formPanel.setOpaque(false);

        codeField = new JTextField();
        nomField = new JTextField();
        prenomField = new JTextField();
        gradeField = new JTextField();

        formPanel.add(new JLabel("Code prof :"));
        formPanel.add(new JLabel("Nom :"));
        formPanel.add(new JLabel("Prenom :"));
        formPanel.add(new JLabel("Grade :"));
        formPanel.add(codeField);
        formPanel.add(nomField);
        formPanel.add(prenomField);
        formPanel.add(gradeField);

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
        saveBtn.addActionListener(e -> saveProf());

        JButton newBtn = new JButton("Nouveau");
        newBtn.addActionListener(e -> clearForm());

        JButton deleteBtn = new JButton("Supprimer");
        Theme.styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteSelectedProf());

        panel.add(saveBtn);
        panel.add(newBtn);
        panel.add(deleteBtn);
        return panel;
    }

    private void saveProf() {
        String code = codeField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String grade = gradeField.getText().trim();

        if (code.isEmpty() || nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le code et le nom sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isNew = codeField.isEditable();

        try {
            if (isNew) {
                profService.createProf(new Prof(code, nom, prenom, grade));
            } else {
                profService.updateProf(code, new Prof(code, nom, prenom, grade));
            }
            refreshTable(profService.getAllProfs());
            refreshGradeFilter();
            clearForm();
            JOptionPane.showMessageDialog(this,
                    isNew ? "Professeur ajouté avec succès." : "Professeur modifié avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedProf() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne d'abord un professeur dans le tableau.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Prof selected = currentProfs.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer " + selected.getNom() + " " + selected.getPrenom() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                profService.deleteProf(selected.getCodeprof());
                refreshTable(profService.getAllProfs());
                refreshGradeFilter();
                clearForm();
                JOptionPane.showMessageDialog(this, "Professeur supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        codeField.setText("");
        codeField.setEditable(true);
        nomField.setText("");
        prenomField.setText("");
        gradeField.setText("");
        table.clearSelection();
    }

    private void refreshTable(List<Prof> profs) {
        this.currentProfs = profs;
        tableModel.setRowCount(0);
        for (Prof p : profs) {
            tableModel.addRow(new Object[]{p.getCodeprof(), p.getNom(), p.getPrenom(), p.getGrade()});
        }
    }

    private void refreshGradeFilter() {
        String currentSelection = (String) gradeFilterCombo.getSelectedItem();
        gradeFilterCombo.removeAllItems();
        gradeFilterCombo.addItem("Tous les grades");
        for (String grade : profService.getAllGrades()) {
            gradeFilterCombo.addItem(grade);
        }
        if (currentSelection != null) {
            gradeFilterCombo.setSelectedItem(currentSelection);
        }
    }
}