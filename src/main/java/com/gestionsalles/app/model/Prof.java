package com.gestionsalles.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prof")
public class Prof {

    @Id
    private String codeprof;  // Cle primaire 'naturelle'

    private String nom;
    private String prenom;
    private String grade;

    @Column(name ="date_creation", updatable = false)
    private LocalDateTime dateCreation;

//    Callback JPA
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

//    Constructeur vide obligatoire pour Hibernate
    public Prof() {}

    public Prof(String codeprof, String nom, String prenom, String grade) {
        this.codeprof = codeprof;
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
    }

//    Getters et Setters
    public String getCodeprof() { return codeprof; }
    public void setCodeprof(String codeprof) { this.codeprof = codeprof; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
