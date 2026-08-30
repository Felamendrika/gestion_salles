package com.gestionsalles.app.service;

import com.gestionsalles.app.exception.ConflictException;
import com.gestionsalles.app.exception.EntityInUseException;
import com.gestionsalles.app.exception.ResourceNotFoundException;
import com.gestionsalles.app.model.Prof;
import com.gestionsalles.app.repository.OccuperRepository;
import com.gestionsalles.app.repository.ProfRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // active le traitement des annotations @Mock ci-dessous
public class ProfServiceTest {

    @Mock
    private ProfRepository profRepository; // faux repository, contrôlé par nous

    @Mock
    private OccuperRepository occuperRepository;

    private ProfService profService;

    @Before
    public void setUp() {
        // Comme dans le TP Money : on prépare les données/objets avant chaque test
        profService = new ProfService(profRepository, occuperRepository);
    }

    @Test
    public void testCreateProf_success() {
        Prof nouveauProf = new Prof("P010", "Rakoto", "Jean", "Assistant");
        when(profRepository.existsById("P010")).thenReturn(false); // on programme le faux repository
        when(profRepository.save(nouveauProf)).thenReturn(nouveauProf);

        Prof result = profService.createProf(nouveauProf);

        assertEquals("P010", result.getCodeprof());
        verify(profRepository, times(1)).save(nouveauProf); // vérifie que save() a bien été appelé une fois
    }

    @Test
    public void testCreateProf_codeDejaUtilise_leveConflictException() {
        Prof profExistant = new Prof("P001", "Rasoa", "Marie", "Professeur");
        when(profRepository.existsById("P001")).thenReturn(true); // le code existe déjà

        try {
            profService.createProf(profExistant);
            fail("Une ConflictException aurait dû être levée"); // équivalent de fail() vu dans ton support théorique
        } catch (ConflictException e) {
            assertTrue(e.getMessage().contains("P001"));
        }

        verify(profRepository, never()).save(any(Prof.class)); // save() ne doit JAMAIS être appelé dans ce cas
    }

    @Test
    public void testGetProfByCode_introuvable_leveResourceNotFoundException() {
        when(profRepository.findById("P999")).thenReturn(Optional.empty());

        try {
            profService.getProfByCode("P999");
            fail("Une ResourceNotFoundException aurait dû être levée");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("P999"));
        }
    }

    @Test
    public void testDeleteProf_profOccupeUneSalle_leveEntityInUseException() {
        Prof prof = new Prof("P001", "Rakoto", "Jean", "Professeur");
        when(profRepository.findById("P001")).thenReturn(Optional.of(prof));
        when(occuperRepository.existsByProf_Codeprof("P001")).thenReturn(true); // ce prof a une occupation active

        try {
            profService.deleteProf("P001");
            fail("Une EntityInUseException aurait dû être levée");
        } catch (EntityInUseException e) {
            assertTrue(e.getMessage().contains("occupe"));
        }

        verify(profRepository, never()).deleteById(anyString());
    }

    @Test
    public void testDeleteProf_success() {
        Prof prof = new Prof("P002", "Rasoa", "Marie", "Maître de Conférences");
        when(profRepository.findById("P002")).thenReturn(Optional.of(prof));
        when(occuperRepository.existsByProf_Codeprof("P002")).thenReturn(false); // aucune occupation

        profService.deleteProf("P002");

        verify(profRepository, times(1)).deleteById("P002");
    }

    @Test
    public void testCreateProf_codeVide_leveIllegalArgumentException() {
        Prof profSansCode = new Prof("", "Razafy", "Hery", "Assistant");

        try {
            profService.createProf(profSansCode);
            fail("Une IllegalArgumentException aurait dû être levée pour un code vide");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().toLowerCase().contains("code"));
        }
    }
}