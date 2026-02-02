package gioco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carte.Eroe;
import data.PotionFactory;
import carte.Pozione;
import data.GameConfig;
import data.HeroFactory;
import gestoreEffetti.Effetto;
import gestoreEffetti.PotionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Test per PotionManager - Sistema Pozioni (Pack 2+)
 */
class PotionManagerTest {
    
    private StatoDiGioco stato;
    private PotionManager potionManager;
    private Giocatore giocatore;
    
    @BeforeEach
    void setUp() {
        // Setup base
        GameConfig config = new GameConfig();
        config.setAnno(5);
        config.setContienePozioni(true);
        
        HeroFactory.inizializza();
        Eroe harry = HeroFactory.creaEroe("Harry Potter", 10);
        giocatore = new Giocatore(harry);
        
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(giocatore);
        
        stato = new StatoDiGioco(config, giocatori);
        potionManager = stato.getPotionManager();
    }
    
    @Test
    void testInizializzazioneScaffali() {
        assertNotNull(potionManager.getScaffaleA(), "Scaffale A deve essere inizializzato");
        assertNotNull(potionManager.getScaffaleB(), "Scaffale B deve essere inizializzato");
        assertEquals("A", potionManager.getLatoCorrente(), "Lato iniziale deve essere A");
    }
    
    @Test
    void testRuotaScaffale() {
        assertEquals("A", potionManager.getLatoCorrente());
        
        potionManager.ruotaScaffale();
        assertEquals("B", potionManager.getLatoCorrente(), "Dopo rotazione deve essere B");
        
        potionManager.ruotaScaffale();
        assertEquals("A", potionManager.getLatoCorrente(), "Dopo seconda rotazione deve tornare ad A");
    }
    
    @Test
    void testAggiungiIngrediente() {
        potionManager.aggiungiIngrediente(giocatore, "WILD");
        assertEquals(1, giocatore.getIngredienti().get("WILD"), "Giocatore deve avere 1 WILD");
        
        potionManager.aggiungiIngrediente(giocatore, "WILD");
        assertEquals(2, giocatore.getIngredienti().get("WILD"), "Giocatore deve avere 2 WILD");
        
        potionManager.aggiungiIngrediente(giocatore, "BICORN_HORN");
        assertEquals(1, giocatore.getIngredienti().get("BICORN_HORN"), "Giocatore deve avere 1 BICORN_HORN");
    }
    
    @Test
    void testBrewPozioneSenzaIngredienti() {
        // Crea pozione test che richiede 2 WILD
        Pozione pozione = new Pozione();
        pozione.setNome("Test Potion");
        List<String> ingredienti = new ArrayList<>();
        ingredienti.add("WILD");
        ingredienti.add("WILD");
        pozione.setIngredienteRichiesti(ingredienti);
        
        // Aggiungi solo 1 WILD (insufficiente)
        giocatore.aggiungiIngrediente("WILD", 1);
        
        boolean result = potionManager.brewPozione(giocatore, pozione, false);
        assertFalse(result, "Brew deve fallire con ingredienti insufficienti");
        assertEquals(1, giocatore.getIngredienti().get("WILD"), "Ingredienti non devono essere consumati");
    }
    
    @Test
    void testBrewPozioneConIngredienti() {
        // Crea pozione test
        Pozione pozione = new Pozione();
        pozione.setNome("Test Potion");
        List<String> ingrediente = new ArrayList<>();
        ingrediente.add("WILD");
        ingrediente.add("WILD");
        pozione.setIngredienteRichiesti(ingrediente);
        
        // Aggiungi ingredienti sufficienti
        giocatore.aggiungiIngrediente("WILD", 2);
        
        // Aggiungi pozione allo scaffale corrente
        potionManager.getScaffaleCorrente().add(pozione);
        
        boolean result = potionManager.brewPozione(giocatore, pozione, false);
        assertTrue(result, "Brew deve avere successo con ingredienti sufficienti");
        assertEquals(0, giocatore.getIngredienti().getOrDefault("WILD", 0), "Ingredienti devono essere consumati");
        assertEquals(1, giocatore.getPozioniBrewateQuestoTurno(), "Contatore pozioni brewate deve essere 1");
    }
    
    @Test
    void testBrewPozioneBanish() {
        // Crea pozione test
        Pozione pozione = new Pozione();
        pozione.setNome("Test Potion Banish");
        List<String> ingredienti = new ArrayList<>();
        ingredienti.add("BICORN_HORN");
        pozione.setIngredienteRichiesti(ingredienti);
        
        giocatore.aggiungiIngrediente("BICORN_HORN", 1);
        potionManager.getScaffaleCorrente().add(pozione);
        
        int mazzoSizeBefore = giocatore.getMazzo().getCarte().size();
        
        boolean result = potionManager.brewPozione(giocatore, pozione, true);
        assertTrue(result, "Brew banish deve avere successo");
        
        // Con banish la pozione NON va nel mazzo
        assertEquals(mazzoSizeBefore, giocatore.getMazzo().getCarte().size(), 
                    "Pozione banish non deve essere aggiunta al mazzo");
    }
    
    @Test
    void testScaffaleVieneSostituito() {
        // Aggiungi 5 pozioni al mazzo
        for (int i = 0; i < 5; i++) {
            Pozione p = new Pozione();
            p.setNome("Potion " + i);
            potionManager.aggiungiPozioneAlMazzo(p);
        }
        
        potionManager.inizializza();
        
        int scaffaleASize = potionManager.getScaffaleA().size();
        int scaffaleBSize = potionManager.getScaffaleB().size();
        
        assertTrue(scaffaleASize <= 3, "Scaffale A non deve avere più di 3 pozioni");
        assertTrue(scaffaleBSize <= 3, "Scaffale B non deve avere più di 3 pozioni");
        assertTrue(scaffaleASize + scaffaleBSize <= 5, "Totale scaffali non deve superare pozioni disponibili");
    }
    
    @Test
    void testIngredientiMultipli() {
        // Test con diversi tipi di ingredienti
        giocatore.aggiungiIngrediente("WILD", 2);
        giocatore.aggiungiIngrediente("BICORN_HORN", 1);
        giocatore.aggiungiIngrediente("HELLEBORE", 1);
        
        assertEquals(2, giocatore.getIngredienti().get("WILD"));
        assertEquals(1, giocatore.getIngredienti().get("BICORN_HORN"));
        assertEquals(1, giocatore.getIngredienti().get("HELLEBORE"));
        
        // Rimuovi ingrediente
        assertTrue(giocatore.rimuoviIngrediente("WILD", 1));
        assertEquals(1, giocatore.getIngredienti().get("WILD"));
        
        // Tenta rimozione con quantità insufficiente
        assertFalse(giocatore.rimuoviIngrediente("HELLEBORE", 2));
        assertEquals(1, giocatore.getIngredienti().get("HELLEBORE"), "Ingrediente non deve cambiare");
    }
}
