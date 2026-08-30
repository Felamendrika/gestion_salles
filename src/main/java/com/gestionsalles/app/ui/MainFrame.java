package com.gestionsalles.app.ui;

import com.gestionsalles.app.ui.component.Theme;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class MainFrame extends JFrame {

    public MainFrame(ProfPanel profPanel, SallePanel sallePanel, OccuperPanel occuperPanel, StatsPanel statsPanel) {
        setTitle("Gestion des Salles de Classe");
        setIconImage(Theme.buildAppIcon()); // remplace l'icône Java générique par défaut
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tableau de bord", statsPanel);
        tabbedPane.addTab("Professeurs", profPanel);
        tabbedPane.addTab("Salles", sallePanel);
        tabbedPane.addTab("Occupations", occuperPanel);

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == tabbedPane.indexOfComponent(occuperPanel)) {
                occuperPanel.onTabActivated();
            } else if (selectedIndex == tabbedPane.indexOfComponent(statsPanel)) {
                statsPanel.refreshStats();
            }
        });

        add(tabbedPane);
    }
}