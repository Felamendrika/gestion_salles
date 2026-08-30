package com.gestionsalles.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.gestionsalles.app.ui.MainFrame;
import com.gestionsalles.app.ui.component.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class GestionSallesApplication {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        Theme.applyGlobalDefaults();

        SpringApplication app = new SpringApplication(GestionSallesApplication.class);
        app.setHeadless(false); // <-- la ligne qui corrige le problème

        ConfigurableApplicationContext context = app.run(args);

        // On lance l'interface graphique sur l'Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = context.getBean(MainFrame.class);
            frame.setVisible(true);
        });
//        SpringApplication.run(GestionSallesApplication.class, args);
    }

}
