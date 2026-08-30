package com.gestionsalles.app.repository;

import com.gestionsalles.app.model.Prof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProfRepository extends JpaRepository<Prof, String> {
    // <Prof, String> : String car notre cle primaire (codeprof) est un String

//    Liste triee, pus recent en premier
    List<Prof> findAllByOrderByDateCreationDesc();

    // Methodes derivees pour la recherche demandee
    List<Prof> findByNomContainingIgnoreCaseOrderByDateCreationDesc(String nom);

    // Filtre par grade
    List<Prof> findByGradeOrderByDateCreationDesc(String grade);

    // Requete personnalisee (JPQL) pour peupler dynmiquement le menu deroulant
    @Query("SELECT DISTINCT p.grade FROM Prof p ORDER BY p.grade")
    List<String> findDisctinctGrades();
}
