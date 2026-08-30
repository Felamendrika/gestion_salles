package com.gestionsalles.app.service;

import com.gestionsalles.app.exception.ConflictException;

import com.gestionsalles.app.exception.EntityInUseException;
import com.gestionsalles.app.exception.ResourceNotFoundException;
import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.repository.OccuperRepository;
import com.gestionsalles.app.repository.ProfRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfService {

    private final ProfRepository profRepository;
    private final OccuperRepository occuperRepository; // nouvelle dépendance

    public ProfService(ProfRepository profRepository, OccuperRepository occuperRepository) {
        this.profRepository = profRepository;
        this.occuperRepository = occuperRepository;
    }

    public List<Prof> getAllProfs() {
        return profRepository.findAllByOrderByDateCreationDesc();
    }

    public Prof getProfByCode(String codeprof) {
        return profRepository.findById(codeprof)
                .orElseThrow(() -> new ResourceNotFoundException("Prof introuvable avec le code : " + codeprof));
    }

    public Prof createProf(Prof prof) {
        if (prof.getCodeprof() == null || prof.getCodeprof().isBlank()) {
            throw new IllegalArgumentException("Le code professeur ne peut pas être vide.");
        }
        if (profRepository.existsById(prof.getCodeprof())) {
            throw new ConflictException("Le code professeur '" + prof.getCodeprof() + "' est déjà utilisé.");
        }
        return profRepository.save(prof);
    }

    public Prof updateProf(String codeprof, Prof updatedProf) {
        Prof prof = getProfByCode(codeprof);
        prof.setNom(updatedProf.getNom());
        prof.setPrenom(updatedProf.getPrenom());
        prof.setGrade(updatedProf.getGrade());
        return profRepository.save(prof);
    }

    public void deleteProf(String codeprof) {
        getProfByCode(codeprof); // vérifie l'existence
        if (occuperRepository.existsByProf_Codeprof(codeprof)) {
            throw new EntityInUseException(
                    "Impossible de supprimer ce professeur : il occupe actuellement au moins une salle. " +
                            "Supprime d'abord ses occupations dans l'onglet Occupations.");
        }
        profRepository.deleteById(codeprof);
    }

    public List<Prof> searchProfs(String query) {
        return profRepository.findById(query)
                .map(List::of)
                .orElseGet(() -> profRepository.findByNomContainingIgnoreCaseOrderByDateCreationDesc(query));
    }

    public List<Prof> filterByGrade(String grade) {
        return profRepository.findByGradeOrderByDateCreationDesc(grade);
    }

    public List<String> getAllGrades() {
        return profRepository.findDisctinctGrades();
    }
}