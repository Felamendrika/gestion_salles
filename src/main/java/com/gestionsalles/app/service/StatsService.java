package com.gestionsalles.app.service;

import com.gestionsalles.app.model.Occuper;
import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.repository.OccuperRepository;
import com.gestionsalles.app.repository.ProfRepository;
import com.gestionsalles.app.repository.SalleRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final ProfRepository profRepository;
    private final SalleRepository salleRepository;
    private final OccuperRepository occuperRepository;

    public StatsService(ProfRepository profRepository, SalleRepository salleRepository, OccuperRepository occuperRepository) {
        this.profRepository = profRepository;
        this.salleRepository = salleRepository;
        this.occuperRepository = occuperRepository;
    }

    public DashboardStats computeStats() {
        long totalProfs = profRepository.count();
        long totalSalles = salleRepository.count();
        List<Occuper> allOccupations = occuperRepository.findAll();

        String salleTop = topSalle(allOccupations);
        String profTop = topProf(allOccupations);
        Map<String, Long> repartitionGrade = repartitionParGrade();

        return new DashboardStats(totalProfs, totalSalles, allOccupations.size(), salleTop, profTop, repartitionGrade);
    }

    private String topSalle(List<Occuper> occupations) {
        return occupations.stream()
                // On regroupe chaque occupation par salle, et on compte combien il y en a par salle
                .collect(Collectors.groupingBy(
                        o -> o.getSalle().getDesignation() ,
                        Collectors.counting()))
                .entrySet().stream()
                // On cherche l'entrée dont le comptage (la valeur) est maximal
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " — " + entry.getValue() + " occupation(s)")
                .orElse("Aucune donnée pour le moment");
    }

    private String topProf(List<Occuper> occupations) {
        return occupations.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getProf().getNom() + " " + o.getProf().getPrenom(),
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " — " + entry.getValue() + " occupation(s)")
                .orElse("Aucune donnée pour le moment");
    }

    private Map<String, Long> repartitionParGrade() {
        return profRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> (p.getGrade() == null || p.getGrade().isBlank()) ? "Non renseigné" : p.getGrade(),
                        LinkedHashMap::new,   // garde un ordre stable d'affichage
                        Collectors.counting()));
    }
}