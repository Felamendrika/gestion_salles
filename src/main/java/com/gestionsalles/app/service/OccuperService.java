package com.gestionsalles.app.service;

import com.gestionsalles.app.exception.ConflictException;
import com.gestionsalles.app.exception.ResourceNotFoundException;
import com.gestionsalles.app.model.Occuper;
import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.model.Salle;
import com.gestionsalles.app.repository.OccuperRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OccuperService {

    private final OccuperRepository occuperRepository;
    private final ProfService profService;
    private final SalleService salleService;

    public OccuperService(OccuperRepository occuperRepository,
                          ProfService profService,
                          SalleService salleService) {
        this.occuperRepository = occuperRepository;
        this.profService = profService;
        this.salleService = salleService;
    }

    public List<Occuper> getAllOccupations() {
        return occuperRepository.findAllByOrderByDateCreationDesc();
    }

    public Occuper getOccupationById(Long id) {
        return occuperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Occupation introuvable avec l'id : " + id));
    }

    public List<Occuper> filterOccupations(LocalDate date, String codesal) {
        if (date != null && codesal != null) {
            return occuperRepository.findByDateAndSalle_CodesalOrderByDateCreationDesc(date, codesal);
        } else if (date != null) {
            return occuperRepository.findByDateOrderByDateCreationDesc(date);
        } else if (codesal != null) {
            return occuperRepository.findBySalle_CodesalOrderByDateCreationDesc(codesal);
        } else {
            return getAllOccupations();
        }
    }

    public Occuper createOccupation(String codeprof, String codesal, LocalDate date) {
        Prof prof = profService.getProfByCode(codeprof);
        Salle salle = salleService.getSalleByCode(codesal);

        checkConflict(codesal, date, codeprof, null);

        try {
            return occuperRepository.save(new Occuper(prof, salle, date));
        } catch (DataIntegrityViolationException ex) {
            // Filet de sécurité si un conflit apparaît malgré tout (ex: 2 clics simultanés)
            throw new ConflictException("Cette occupation existe déjà.");
        }
    }

    @Transactional
    public Occuper updateOccupation(Long id, String codeprof, String codesal, LocalDate date) {
        Occuper occupation = getOccupationById(id);
        Prof prof = profService.getProfByCode(codeprof);
        Salle salle = salleService.getSalleByCode(codesal);

        checkConflict(codesal, date, codeprof, id); // on exclut l'occupation qu'on est en train de modifier

        occupation.setProf(prof);
        occupation.setSalle(salle);
        occupation.setDate(date);
        return occuperRepository.save(occupation);
    }

    public void deleteOccupation(Long id) {
        getOccupationById(id); // vérifie l'existence, sinon message clair
        occuperRepository.deleteById(id);
    }

    // Vérifie si la salle est déjà occupée à cette date, en excluant éventuellement
    // l'occupation qu'on est en train de modifier (excludeId)
    private void checkConflict(String codesal, LocalDate date, String codeprof, Long excludeId) {
        List<Occuper> conflicts = occuperRepository.findBySalle_CodesalAndDate(codesal, date).stream()
                .filter(o -> excludeId == null || !o.getId().equals(excludeId))
                .toList();

        if (!conflicts.isEmpty()) {
            Occuper conflict = conflicts.get(0);
            if (conflict.getProf().getCodeprof().equals(codeprof)) {
                throw new ConflictException("Ce professeur a déjà réservé la salle " + codesal + " à cette date.");
            } else {
                throw new ConflictException("La salle " + codesal + " est déjà occupée à cette date par "
                        + conflict.getProf().getNom() + " " + conflict.getProf().getPrenom() + ".");
            }
        }
    }
}