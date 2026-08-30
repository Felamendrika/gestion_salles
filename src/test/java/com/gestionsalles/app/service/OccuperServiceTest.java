package com.gestionsalles.app.service;

import com.gestionsalles.app.exception.ConflictException;
import com.gestionsalles.app.exception.ResourceNotFoundException;
import com.gestionsalles.app.model.Occuper;
import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.model.Salle;
import com.gestionsalles.app.repository.OccuperRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OccuperServiceTest {

    @Mock
    private OccuperRepository occuperRepository;

    @Mock
    private ProfService profService; // on mocke le SERVICE, pas le repository

    @Mock
    private SalleService salleService;

    private OccuperService occuperService;

    private Prof profTest;
    private Salle salleTest;
    private LocalDate dateTest;

    @Before
    public void setUp() {
        occuperService = new OccuperService(occuperRepository, profService, salleService);
        profTest = new Prof("P001", "Rakoto", "Jean", "Professeur");
        salleTest = new Salle("S001", "Amphithéâtre A");
        dateTest = LocalDate.of(2026, 9, 2);
    }

    @Test
    public void testCreateOccupation_success() {
        when(profService.getProfByCode("P001")).thenReturn(profTest);
        when(salleService.getSalleByCode("S001")).thenReturn(salleTest);
        when(occuperRepository.findBySalle_CodesalAndDate("S001", dateTest)).thenReturn(Collections.emptyList());

        occuperService.createOccupation("P001", "S001", dateTest);

        verify(occuperRepository, times(1)).save(any(Occuper.class));
    }

    @Test
    public void testCreateOccupation_memeProfMemeSalleMemeDate_leveConflictException() {
        Occuper occupationExistante = new Occuper(profTest, salleTest, dateTest);
        when(profService.getProfByCode("P001")).thenReturn(profTest);
        when(salleService.getSalleByCode("S001")).thenReturn(salleTest);
        when(occuperRepository.findBySalle_CodesalAndDate("S001", dateTest))
                .thenReturn(List.of(occupationExistante));

        try {
            occuperService.createOccupation("P001", "S001", dateTest);
            fail("Une ConflictException aurait dû être levée");
        } catch (ConflictException e) {
            assertTrue(e.getMessage().contains("déjà réservé"));
        }

        verify(occuperRepository, never()).save(any(Occuper.class));
    }

    @Test
    public void testCreateOccupation_salleOccupeeParAutrePprof_leveConflictExceptionAvecNom() {
        Prof autreProf = new Prof("P002", "Rasoa", "Marie", "Maître de Conférences");
        Occuper occupationExistante = new Occuper(autreProf, salleTest, dateTest);

        when(profService.getProfByCode("P001")).thenReturn(profTest);
        when(salleService.getSalleByCode("S001")).thenReturn(salleTest);
        when(occuperRepository.findBySalle_CodesalAndDate("S001", dateTest))
                .thenReturn(List.of(occupationExistante));

        try {
            occuperService.createOccupation("P001", "S001", dateTest);
            fail("Une ConflictException aurait dû être levée");
        } catch (ConflictException e) {
            assertTrue(e.getMessage().contains("Rasoa")); // le nom de l'AUTRE prof doit apparaître
        }
    }

    @Test
    public void testGetOccupationById_introuvable_leveResourceNotFoundException() {
        when(occuperRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            occuperService.getOccupationById(999L);
            fail("Une ResourceNotFoundException aurait dû être levée");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("999"));
        }
    }

    @Test
    public void testUpdateOccupation_excluSoiMeme_pasDeConflit() {
        Long occupationId = 1L;
        Occuper occupationActuelle = new Occuper(profTest, salleTest, dateTest);
        occupationActuelle.setId(occupationId);

        when(occuperRepository.findById(occupationId)).thenReturn(Optional.of(occupationActuelle));
        when(profService.getProfByCode("P001")).thenReturn(profTest);
        when(salleService.getSalleByCode("S001")).thenReturn(salleTest);
        // La seule occupation trouvée en conflit potentiel est CELLE QU'ON MODIFIE ELLE-MÊME
        when(occuperRepository.findBySalle_CodesalAndDate("S001", dateTest))
                .thenReturn(List.of(occupationActuelle));
        when(occuperRepository.save(any(Occuper.class))).thenReturn(occupationActuelle);

        // Ne doit PAS lever d'exception, puisque le seul "conflit" trouvé est l'occupation qu'on modifie elle-même
        occuperService.updateOccupation(occupationId, "P001", "S001", dateTest);

        verify(occuperRepository, times(1)).save(any(Occuper.class));
    }
}
