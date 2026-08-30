package com.gestionsalles.app.ui;

import com.gestionsalles.app.model.Occuper;
import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.model.Salle;
import com.gestionsalles.app.service.OccuperService;
import com.gestionsalles.app.service.ProfService;
import com.gestionsalles.app.service.SalleService;
import com.gestionsalles.app.ui.component.RoundedPanel;
import com.gestionsalles.app.ui.component.Theme;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class OccuperPanel extends JPanel {

    private final OccuperService occuperService;
    private final ProfService profService;
    private final SalleService salleService;

    private JCheckBox filterDateEnabled;
    private JSpinner filterDateSpinner;
    private JComboBox<ComboItem> filterSalleCombo;

    private JTable table;
    private DefaultTableModel tableModel;
    private List<Occuper> currentOccupations;

    private JComboBox<ComboItem> profCombo;
    private JComboBox<ComboItem> salleCombo;
    private JSpinner dateSpinner;
    private Long selectedOccupationId;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public OccuperPanel(OccuperService occuperService, ProfService profService, SalleService salleService) {
        this.occuperService = occuperService;
        this.profService = profService;
        this.salleService = salleService;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildFilterBar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        add(buildFormCard(), BorderLayout.SOUTH);

        refreshCombos();
        refreshTable(occuperService.getAllOccupations());
    }

    private JPanel buildFilterBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterDateEnabled = new JCheckBox("Filtrer par date :");
        filterDateSpinner = new JSpinner(new SpinnerDateModel());
        filterDateSpinner.setEditor(new JSpinner.DateEditor(filterDateSpinner, "dd/MM/yyyy"));

        filterSalleCombo = new JComboBox<>();

        JButton applyBtn = new JButton("Filtrer");
        applyBtn.addActionListener(e -> applyFilters());

        JButton resetBtn = new JButton("Réinitialiser");
        resetBtn.addActionListener(e -> {
            filterDateEnabled.setSelected(false);
            filterSalleCombo.setSelectedIndex(0);
            refreshTable(occuperService.getAllOccupations());
        });

        panel.add(filterDateEnabled);
        panel.add(filterDateSpinner);
        panel.add(new JLabel("Salle :"));
        panel.add(filterSalleCombo);
        panel.add(applyBtn);
        panel.add(resetBtn);
        return panel;
    }

    private void applyFilters() {
        LocalDate date = filterDateEnabled.isSelected() ? toLocalDate((Date) filterDateSpinner.getValue()) : null;
        ComboItem selectedSalle = (ComboItem) filterSalleCombo.getSelectedItem();
        String codesal = (selectedSalle != null && !selectedSalle.value.equals("ALL")) ? selectedSalle.value : null;
        refreshTable(occuperService.filterOccupations(date, codesal));
    }

    private RoundedPanel buildTableCard() {
        String[] columns = {"Professeur", "Salle", "Date"};
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
                Occuper selected = currentOccupations.get(row);
                selectedOccupationId = selected.getId();
                selectComboByValue(profCombo, selected.getProf().getCodeprof());
                selectComboByValue(salleCombo, selected.getSalle().getCodesal());
                dateSpinner.setValue(toDate(selected.getDate()));
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
        JPanel formPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        formPanel.setOpaque(false);

        profCombo = new JComboBox<>();
        salleCombo = new JComboBox<>();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        formPanel.add(new JLabel("Professeur :"));
        formPanel.add(new JLabel("Salle :"));
        formPanel.add(new JLabel("Date :"));
        formPanel.add(profCombo);
        formPanel.add(salleCombo);
        formPanel.add(dateSpinner);

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
        saveBtn.addActionListener(e -> saveOccupation());

        JButton newBtn = new JButton("Nouveau");
        newBtn.addActionListener(e -> clearForm());

        JButton deleteBtn = new JButton("Supprimer");
        Theme.styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteSelectedOccupation());

        panel.add(saveBtn);
        panel.add(newBtn);
        panel.add(deleteBtn);
        return panel;
    }

    private void saveOccupation() {
        ComboItem selectedProf = (ComboItem) profCombo.getSelectedItem();
        ComboItem selectedSalle = (ComboItem) salleCombo.getSelectedItem();

        if (selectedProf == null || selectedSalle == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un professeur et une salle.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate date = toLocalDate((Date) dateSpinner.getValue());
        boolean isNew = selectedOccupationId == null;

        try {
            if (isNew) {
                occuperService.createOccupation(selectedProf.value, selectedSalle.value, date);
            } else {
                occuperService.updateOccupation(selectedOccupationId, selectedProf.value, selectedSalle.value, date);
            }
            refreshTable(occuperService.getAllOccupations());
            clearForm();
            JOptionPane.showMessageDialog(this,
                    isNew ? "Occupation ajoutée avec succès." : "Occupation modifiée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedOccupation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne d'abord une occupation dans le tableau.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Occuper selected = currentOccupations.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette occupation ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                occuperService.deleteOccupation(selected.getId());
                refreshTable(occuperService.getAllOccupations());
                clearForm();
                JOptionPane.showMessageDialog(this, "Occupation supprimée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedOccupationId = null;
        if (profCombo.getItemCount() > 0) profCombo.setSelectedIndex(0);
        if (salleCombo.getItemCount() > 0) salleCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        table.clearSelection();
    }

    private void refreshTable(List<Occuper> occupations) {
        this.currentOccupations = occupations;
        tableModel.setRowCount(0);
        for (Occuper o : occupations) {
            tableModel.addRow(new Object[]{
                    o.getProf().getNom() + " " + o.getProf().getPrenom() + " (" + o.getProf().getCodeprof() + ")",
                    o.getSalle().getDesignation() + " (" + o.getSalle().getCodesal() + ")",
                    o.getDate().format(DATE_FMT)
            });
        }
    }

    public void refreshCombos() {
        profCombo.removeAllItems();
        for (Prof p : profService.getAllProfs()) {
            profCombo.addItem(new ComboItem(p.getCodeprof(), p.getCodeprof() + " - " + p.getNom() + " " + p.getPrenom()));
        }

        salleCombo.removeAllItems();
        filterSalleCombo.removeAllItems();
        filterSalleCombo.addItem(new ComboItem("ALL", "Toutes les salles"));
        for (Salle s : salleService.getAllSalles()) {
            salleCombo.addItem(new ComboItem(s.getCodesal(), s.getCodesal() + " - " + s.getDesignation()));
            filterSalleCombo.addItem(new ComboItem(s.getCodesal(), s.getCodesal() + " - " + s.getDesignation()));
        }
    }

    public void onTabActivated() {
        refreshCombos();
        refreshTable(occuperService.getAllOccupations());
    }

    private void selectComboByValue(JComboBox<ComboItem> combo, String value) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).value.equals(value)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static class ComboItem {
        final String value;
        final String label;

        ComboItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}