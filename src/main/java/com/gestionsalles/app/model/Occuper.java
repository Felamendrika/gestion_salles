package com.gestionsalles.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "occuper",
        uniqueConstraints = @UniqueConstraint(columnNames = {"codeprof", "codesal", "date_occupation"})
)
public class Occuper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Long id; // cle technique

    @ManyToOne
    @JoinColumn(name = "codeprof")  // colonne de cle etrangere vers PROF
    private Prof prof;

    @ManyToOne
    @JoinColumn(name = "codesal")
    private Salle salle;

    @Column(name = "date_occupation")
    private LocalDate date;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    public Occuper() {}

    public Occuper(Prof prof, Salle salle, LocalDate date) {
        this.prof = prof;
        this.salle = salle;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Prof getProf() { return prof; }
    public void setProf(Prof prof) { this.prof = prof; }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
