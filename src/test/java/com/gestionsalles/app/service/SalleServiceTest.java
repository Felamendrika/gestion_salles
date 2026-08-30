package com.gestionsalles.app.service;

import com.gestionsalles.app.exception.ConflictException;
import com.gestionsalles.app.exception.EntityInUseException;
import com.gestionsalles.app.exception.ResourceNotFoundException;
import com.gestionsalles.app.model.Salle;
import com.gestionsalles.app.repository.OccuperRepository;
import com.gestionsalles.app.repository.SalleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalleServiceTest {

    @Mock
    private SalleRepository salleRepository;

    @Mock
    private OccuperRepository occuperRepository;

    private SalleService salleService;

    @Before
    public void setUp() {
        salleService = new SalleService(salleRepository, occuperRepository);
    }

    @Test
    public void testCreateSalle_success() {
        Salle nouvelleSalle = new Salle("S010", "Salle Multimédia");
        when(salleRepository.existsById("S010")).thenReturn(false);
        when(salleRepository.save(nouvelleSalle)).thenReturn(nouvelleSalle);

        Salle result = salleService.createSalle(nouvelleSalle);

        assertEquals("S010", result.getCodesal());
        verify(salleRepository, times(1)).save(nouvelleSalle);
    }

    @Test
    public void testCreateSalle_codeDejaUtilise_leveConflictException() {
        Salle salleExistante = new Salle("S001", "Amphithéâtre A");
        when(salleRepository.existsById("S001")).thenReturn(true);

        try {
            salleService.createSalle(salleExistante);
            fail("Une ConflictException aurait dû être levée");
        } catch (ConflictException e) {
            assertTrue(e.getMessage().contains("S001"));
        }

        verify(salleRepository, never()).save(any(Salle.class));
    }

    @Test
    public void testGetSalleByCode_introuvable_leveResourceNotFoundException() {
        when(salleRepository.findById("S999")).thenReturn(Optional.empty());

        try {
            salleService.getSalleByCode("S999");
            fail("Une ResourceNotFoundException aurait dû être levée");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("S999"));
        }
    }

    @Test
    public void testDeleteSalle_salleOccupee_leveEntityInUseException() {
        Salle salle = new Salle("S001", "Amphithéâtre A");
        when(salleRepository.findById("S001")).thenReturn(Optional.of(salle));
        when(occuperRepository.existsBySalle_Codesal("S001")).thenReturn(true);

        try {
            salleService.deleteSalle("S001");
            fail("Une EntityInUseException aurait dû être levée");
        } catch (EntityInUseException e) {
            assertTrue(e.getMessage().contains("occupée"));
        }

        verify(salleRepository, never()).deleteById(anyString());
    }

    @Test
    public void testDeleteSalle_success() {
        Salle salle = new Salle("S005", "Salle de Conférence");
        when(salleRepository.findById("S005")).thenReturn(Optional.of(salle));
        when(occuperRepository.existsBySalle_Codesal("S005")).thenReturn(false);

        salleService.deleteSalle("S005");

        verify(salleRepository, times(1)).deleteById("S005");
    }

    // Ce test va ÉCHOUER au premier lancement, volontairement — même principe que pour ProfService
    @Test
    public void testCreateSalle_codeVide_leveIllegalArgumentException() {
        Salle salleSansCode = new Salle("", "Salle Test");

        try {
            salleService.createSalle(salleSansCode);
            fail("Une IllegalArgumentException aurait dû être levée pour un code vide");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().toLowerCase().contains("code"));
        }
    }
}
