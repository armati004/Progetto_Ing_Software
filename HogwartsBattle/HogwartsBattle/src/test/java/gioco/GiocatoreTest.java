package gioco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carte.DarkArtsPotion;
import carte.Eroe;
import data.HeroFactory;
import gestoreEffetti.Effetto;
import gestoreEffetti.TipoEffetto;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Test per Giocatore - Nuove funzionalità espansione
 */
class GiocatoreTest {
    
    private Giocatore giocatore;
    private Eroe eroe;
    
    @BeforeEach
    void setUp() {
    	HeroFactory.inizializza();
        eroe = HeroFactory.creaEroe("Neville Longbottom", 10);
        giocatore = new Giocatore(eroe);
    }
    
    // ============================================================================
    // TEST INGREDIENTI
    // ============================================================================
    
    @Test
    void testAggiungiIngrediente() {
        giocatore.aggiungiIngrediente("WILD", 2);
        assertEquals(2, giocatore.getIngredienti().get("WILD"), "Deve avere 2 WILD");
        
        giocatore.aggiungiIngrediente("WILD", 1);
        assertEquals(3, giocatore.getIngredienti().get("WILD"), "Deve avere 3 WILD");
    }
    
    @Test
    void testRimuoviIngrediente() {
        giocatore.aggiungiIngrediente("BICORN_HORN", 3);
        
        assertTrue(giocatore.rimuoviIngrediente("BICORN_HORN", 2), "Deve rimuovere con successo");
        assertEquals(1, giocatore.getIngredienti().get("BICORN_HORN"), "Deve rimanere 1");
        
        assertTrue(giocatore.rimuoviIngrediente("BICORN_HORN", 1), "Deve rimuovere l'ultimo");
        assertEquals(0, giocatore.getIngredienti().get("BICORN_HORN"), "Deve essere 0");
    }
    
    @Test
    void testRimuoviIngredienteInsufficiente() {
        giocatore.aggiungiIngrediente("HELLEBORE", 2);
        
        assertFalse(giocatore.rimuoviIngrediente("HELLEBORE", 3), "Non deve rimuovere se insufficienti");
        assertEquals(2, giocatore.getIngredienti().get("HELLEBORE"), "Deve rimanere invariato");
    }
    
    @Test
    void testRimuoviIngredienteInesistente() {
        assertFalse(giocatore.rimuoviIngrediente("FLOBBER_WORM", 1), 
                   "Non deve rimuovere ingrediente mai aggiunto");
    }
    
    @Test
    void testHaIngredienti() {
        giocatore.aggiungiIngrediente("WILD", 2);
        giocatore.aggiungiIngrediente("BICORN_HORN", 1);
        
        Map<String, Integer> richiesti = new HashMap<>();
        richiesti.put("WILD", 2);
        richiesti.put("BICORN_HORN", 1);
        
        assertTrue(giocatore.haIngredienti(richiesti), "Deve avere tutti gli ingredienti richiesti");
    }
    
    @Test
    void testNonHaIngredienti() {
        giocatore.aggiungiIngrediente("WILD", 1);
        giocatore.aggiungiIngrediente("BICORN_HORN", 1);
        
        Map<String, Integer> richiesti = new HashMap<>();
        richiesti.put("WILD", 2);  // Richiede 2 ma ha solo 1
        richiesti.put("BICORN_HORN", 1);
        
        assertFalse(giocatore.haIngredienti(richiesti), "Non deve avere ingredienti sufficienti");
    }
    
    @Test
    void testConsumaIngredienti() {
        giocatore.aggiungiIngrediente("WILD", 3);
        giocatore.aggiungiIngrediente("MANDRAKE_LEAF", 2);
        
        Map<String, Integer> daConsumare = new HashMap<>();
        daConsumare.put("WILD", 2);
        daConsumare.put("MANDRAKE_LEAF", 1);
        
        assertTrue(giocatore.consumaIngredienti(daConsumare), "Deve consumare con successo");
        
        assertEquals(1, giocatore.getIngredienti().get("WILD"), "Deve rimanere 1 WILD");
        assertEquals(1, giocatore.getIngredienti().get("MANDRAKE_LEAF"), "Deve rimanere 1 MANDRAKE_LEAF");
    }
    
    @Test
    void testConsumaIngredientiInsufficiente() {
        giocatore.aggiungiIngrediente("LACEWING_FLY", 1);
        
        Map<String, Integer> daConsumare = new HashMap<>();
        daConsumare.put("LACEWING_FLY", 2);
        
        assertFalse(giocatore.consumaIngredienti(daConsumare), "Non deve consumare se insufficienti");
        assertEquals(1, giocatore.getIngredienti().get("LACEWING_FLY"), "Ingredienti devono rimanere");
    }
    
    @Test
    void testResetIngredienti() {
        giocatore.aggiungiIngrediente("WILD", 5);
        giocatore.aggiungiIngrediente("BICORN_HORN", 3);
        
        giocatore.resetIngredienti();
        
        assertTrue(giocatore.getIngredienti().isEmpty(), "Ingredienti devono essere vuoti");
    }
    
    // ============================================================================
    // TEST POZIONI BREWATE
    // ============================================================================
    
    @Test
    void testIncrementaPozioniBrewate() {
        assertEquals(0, giocatore.getPozioniBrewateQuestoTurno(), "Inizialmente 0");
        
        giocatore.incrementaPozioniBrewate();
        assertEquals(1, giocatore.getPozioniBrewateQuestoTurno(), "Deve essere 1");
        
        giocatore.incrementaPozioniBrewate();
        giocatore.incrementaPozioniBrewate();
        assertEquals(3, giocatore.getPozioniBrewateQuestoTurno(), "Deve essere 3");
    }
    
    @Test
    void testResetPozioniBrewate() {
        giocatore.incrementaPozioniBrewate();
        giocatore.incrementaPozioniBrewate();
        
        giocatore.resetPozioniBrewate();
        assertEquals(0, giocatore.getPozioniBrewateQuestoTurno(), "Deve essere resettato a 0");
    }
    
    // ============================================================================
    // TEST DARK ARTS POTIONS
    // ============================================================================
    
    @Test
    void testAggiungiDarkArtsPotion() {
        DarkArtsPotion potion = new DarkArtsPotion();
        potion.setNome("Test Dark Potion");
        
        assertFalse(giocatore.hasDarkArtsPotions(), "Inizialmente non deve avere pozioni");
        
        giocatore.aggiungiDarkArtsPotion(potion);
        
        assertTrue(giocatore.hasDarkArtsPotions(), "Deve avere pozioni");
        assertEquals(1, giocatore.getDarkArtsPotionsAttive().size(), "Deve avere 1 pozione");
    }
    
    @Test
    void testAggiungiMultipleDarkArtsPotions() {
        DarkArtsPotion potion1 = new DarkArtsPotion();
        potion1.setNome("Potion 1");
        DarkArtsPotion potion2 = new DarkArtsPotion();
        potion2.setNome("Potion 2");
        
        giocatore.aggiungiDarkArtsPotion(potion1);
        giocatore.aggiungiDarkArtsPotion(potion2);
        
        assertEquals(2, giocatore.getDarkArtsPotionsAttive().size(), "Deve avere 2 pozioni");
    }
    
    @Test
    void testRimuoviDarkArtsPotion() {
        DarkArtsPotion potion = new DarkArtsPotion();
        potion.setNome("Test Potion");
        
        giocatore.aggiungiDarkArtsPotion(potion);
        assertEquals(1, giocatore.getDarkArtsPotionsAttive().size());
        
        giocatore.rimuoviDarkArtsPotion(potion);
        assertEquals(0, giocatore.getDarkArtsPotionsAttive().size(), "Pozione deve essere rimossa");
        assertFalse(giocatore.hasDarkArtsPotions());
    }
    
    @Test
    void testRimuoviTutteDarkArtsPotions() {
        DarkArtsPotion potion1 = new DarkArtsPotion();
        DarkArtsPotion potion2 = new DarkArtsPotion();
        DarkArtsPotion potion3 = new DarkArtsPotion();
        
        giocatore.aggiungiDarkArtsPotion(potion1);
        giocatore.aggiungiDarkArtsPotion(potion2);
        giocatore.aggiungiDarkArtsPotion(potion3);
        
        assertEquals(3, giocatore.getDarkArtsPotionsAttive().size());
        
        giocatore.rimuoviTutteDarkArtsPotions();
        
        assertEquals(0, giocatore.getDarkArtsPotionsAttive().size(), "Tutte devono essere rimosse");
        assertFalse(giocatore.hasDarkArtsPotions());
    }
    
    // ============================================================================
    // TEST INTEGRAZIONE
    // ============================================================================
    
    @Test
    void testScenarioBrewCompleto() {
        // Setup: giocatore raccoglie ingredienti
        giocatore.aggiungiIngrediente("WILD", 2);
        giocatore.aggiungiIngrediente("HELLEBORE", 1);
        
        // Verifica ingredienti disponibili
        Map<String, Integer> richiesti = new HashMap<>();
        richiesti.put("WILD", 2);
        richiesti.put("HELLEBORE", 1);
        
        assertTrue(giocatore.haIngredienti(richiesti));
        
        // Brew pozione (consuma ingredienti)
        assertTrue(giocatore.consumaIngredienti(richiesti));
        giocatore.incrementaPozioniBrewate();
        
        // Verifica stato finale
        assertEquals(0, giocatore.getIngredienti().getOrDefault("WILD", 0));
        assertEquals(0, giocatore.getIngredienti().getOrDefault("HELLEBORE", 0));
        assertEquals(1, giocatore.getPozioniBrewateQuestoTurno());
    }
    
    @Test
    void testScenarioDarkArtsPotionsMultiple() {
        // Giocatore riceve 3 Dark Arts Potions in turni diversi
        DarkArtsPotion p1 = new DarkArtsPotion();
        p1.setNome("Sleeping Draught");
        DarkArtsPotion p2 = new DarkArtsPotion();
        p2.setNome("Confusing Concoction");
        DarkArtsPotion p3 = new DarkArtsPotion();
        p3.setNome("Forgetfulness Potion");
        
        giocatore.aggiungiDarkArtsPotion(p1);
        giocatore.aggiungiDarkArtsPotion(p2);
        giocatore.aggiungiDarkArtsPotion(p3);
        
        assertEquals(3, giocatore.getDarkArtsPotionsAttive().size());
        
        // Giocatore usa reward Encounter per rimuovere tutte
        giocatore.rimuoviTutteDarkArtsPotions();
        
        assertFalse(giocatore.hasDarkArtsPotions());
    }
}
