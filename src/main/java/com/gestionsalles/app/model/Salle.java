package com.gestionsalles.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "salle")
public class Salle {

    @Id
    private String codesal;

    private String designation;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    public Salle() {}

    public Salle(String codesal, String designation) {
        this.codesal = codesal;
        this.designation = designation;
    }

    public String getCodesal() { return codesal; }
    public void setCodesal(String codesal) { this.codesal = codesal; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
