package com.gestionsalles.app.service;

import com.gestionsalles.app.exception.ConflictException;

import com.gestionsalles.app.exception.EntityInUseException;
import com.gestionsalles.app.exception.ResourceNotFoundException;
import com.gestionsalles.app.model.Salle;
import com.gestionsalles.app.repository.OccuperRepository;
import com.gestionsalles.app.repository.SalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalleService {

    private final SalleRepository salleRepository;
    private final OccuperRepository occuperRepository;

    public SalleService(SalleRepository salleRepository, OccuperRepository occuperRepository) {
        this.salleRepository = salleRepository;
        this.occuperRepository = occuperRepository;
    }

    public List<Salle> getAllSalles() {
        return salleRepository.findAllByOrderByDateCreationDesc();
    }

    public Salle getSalleByCode(String codesal) {
        return salleRepository.findById(codesal)
                .orElseThrow(() -> new ResourceNotFoundException("Salle introuvable avec le code : " + codesal));
    }

    public Salle createSalle(Salle salle) {
        if (salle.getCodesal() == null || salle.getCodesal().isBlank()) {
            throw new IllegalArgumentException("Le code salle ne peut pas etre vide.");
        }
        if (salleRepository.existsById(salle.getCodesal())) {
            throw new ConflictException("Le code salle '" + salle.getCodesal() + "' est déjà utilisé.");
        }
        return salleRepository.save(salle);
    }

    public Salle updateSalle(String codesal, Salle updatedSalle) {
        Salle salle = getSalleByCode(codesal);
        salle.setDesignation(updatedSalle.getDesignation());
        return salleRepository.save(salle);
    }

    public void deleteSalle(String codesal) {
        getSalleByCode(codesal);
        if (occuperRepository.existsBySalle_Codesal(codesal)) {
            throw new EntityInUseException(
                    "Impossible de supprimer cette salle : elle est actuellement occupée par au moins un professeur. " +
                            "Supprime d'abord les occupations associées dans l'onglet Occupations.");
        }
        salleRepository.deleteById(codesal);
    }
}