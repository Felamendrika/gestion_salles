package com.gestionsalles.app.repository;

import com.gestionsalles.app.model.Occuper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OccuperRepository extends JpaRepository<Occuper, Long> {

    List<Occuper> findAllByOrderByDateCreationDesc();

    // Filtre combinables
    List<Occuper> findByDateOrderByDateCreationDesc(LocalDate date);

    List<Occuper> findBySalle_CodesalOrderByDateCreationDesc(String codesal);

    List<Occuper> findByDateAndSalle_CodesalOrderByDateCreationDesc(LocalDate date, String codesal);

    List<Occuper> findBySalle_CodesalAndDate(String codesal, LocalDate date);
    boolean existsByProf_Codeprof(String codeprof);
    boolean existsBySalle_Codesal(String codesal);
}
