package com.gestionsalles.app.repository;

import com.gestionsalles.app.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalleRepository extends JpaRepository<Salle, String> {

    List<Salle> findAllByOrderByDateCreationDesc();
}
