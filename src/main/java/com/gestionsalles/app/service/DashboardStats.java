package com.gestionsalles.app.service;

import java.util.Map;

// Une classe simple qui ne fait que transporter des données calculées, sans logique.
// C'est ce qu'on appelle un DTO (Data Transfer Object).
public class DashboardStats {

    private final long totalProfs;
    private final long totalSalles;
    private final long totalOccupations;
    private final String salleLaPlusUtilisee;
    private final String profLePlusActif;
    private final Map<String, Long> repartitionParGrade;

    public DashboardStats(long totalProfs, long totalSalles, long totalOccupations,
                          String salleLaPlusUtilisee, String profLePlusActif,
                          Map<String, Long> repartitionParGrade) {
        this.totalProfs = totalProfs;
        this.totalSalles = totalSalles;
        this.totalOccupations = totalOccupations;
        this.salleLaPlusUtilisee = salleLaPlusUtilisee;
        this.profLePlusActif = profLePlusActif;
        this.repartitionParGrade = repartitionParGrade;
    }

    public long getTotalProfs() { return totalProfs; }
    public long getTotalSalles() { return totalSalles; }
    public long getTotalOccupations() { return totalOccupations; }
    public String getSalleLaPlusUtilisee() { return salleLaPlusUtilisee; }
    public String getProfLePlusActif() { return profLePlusActif; }
    public Map<String, Long> getRepartitionParGrade() { return repartitionParGrade; }
}