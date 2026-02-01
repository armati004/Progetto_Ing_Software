package gioco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carte.DarkArtsPotion;
import carte.Eroe;
import data.GameConfig;
import data.HeroFactory;
import gestoreEffetti.DarkArtsPotionManager;
import gestoreEffetti.Effetto;
import gestoreEffetti.TipoEffetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Test per DarkArtsPotionManager - Dark Arts Potions (Pack 3+)
 */
class DarkArtsPotionManagerTest {
    
    private StatoDiGioco stato;
    private DarkArtsPotionManager darkArtsManager;
    private Giocatore giocatore;
    
    @BeforeEach
    void setUp() {
        GameConfig config = new GameConfig();
        config.setAnno(6);
        // ✅ CORREZIONE 1: questo funziona solo se in GameConfig hai corretto il setter:
        //    public void setContieneDarkArtsPozioni(boolean contieneDarkArtsPozioni) {
        //        this.contieneDarkArtsPozioni = contieneDarkArtsPozioni;
        //    }
        config.setContieneDarkArtsPozioni(true);
        
        HeroFactory.inizializza();
        Eroe hermione = HeroFactory.creaEroe("Hermione Granger", 10);
        giocatore = new Giocatore(hermione);
        
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(giocatore);
        
        stato = new StatoDiGioco(config, giocatori);
        darkArtsManager = stato.getDarkArtsPotionManager();
    }
    
    @Test
    void testAssegnaPozioneAlGiocatore() {
        DarkArtsPotion potion = creaDarkArtsPotionTest("Sleeping Draught");
        
        assertFalse(giocatore.hasDarkArtsPotions(), "Giocatore non deve avere pozioni inizialmente");
        
        darkArtsManager.assegnaPozioneAlGiocatore(potion, giocatore);
        
        assertTrue(giocatore.hasDarkArtsPotions(), "Giocatore deve avere pozioni");
        assertEquals(1, giocatore.getDarkArtsPotionsAttive().size(), "Giocatore deve avere 1 pozione");
        assertEquals("Sleeping Draught", giocatore.getDarkArtsPotionsAttive().get(0).getNome());
    }
    
    @Test
    void testAssegnaMultiplePotions() {
        DarkArtsPotion potion1 = creaDarkArtsPotionTest("Sleeping Draught");
        DarkArtsPotion potion2 = creaDarkArtsPotionTest("Confusing Concoction");
        
        darkArtsManager.assegnaPozioneAlGiocatore(potion1, giocatore);
        darkArtsManager.assegnaPozioneAlGiocatore(potion2, giocatore);
        
        assertEquals(2, giocatore.getDarkArtsPotionsAttive().size(), "Giocatore deve avere 2 pozioni");
    }
    
    @Test
    void testRimuoviPozione() {
        DarkArtsPotion potion = creaDarkArtsPotionTest("Test Potion");
        
        darkArtsManager.assegnaPozioneAlGiocatore(potion, giocatore);
        assertEquals(1, giocatore.getDarkArtsPotionsAttive().size());
        
        giocatore.rimuoviDarkArtsPotion(potion);
        assertEquals(0, giocatore.getDarkArtsPotionsAttive().size(), "Pozione deve essere rimossa");
        assertFalse(giocatore.hasDarkArtsPotions());
    }
    
    @Test
    void testRimuoviTuttePotions() {
        DarkArtsPotion potion1 = creaDarkArtsPotionTest("Potion 1");
        DarkArtsPotion potion2 = creaDarkArtsPotionTest("Potion 2");
        DarkArtsPotion potion3 = creaDarkArtsPotionTest("Potion 3");
        
        darkArtsManager.assegnaPozioneAlGiocatore(potion1, giocatore);
        darkArtsManager.assegnaPozioneAlGiocatore(potion2, giocatore);
        darkArtsManager.assegnaPozioneAlGiocatore(potion3, giocatore);
        
        assertEquals(3, giocatore.getDarkArtsPotionsAttive().size());
        
        darkArtsManager.rimuoviTuttePozioni(giocatore);
        
        assertEquals(0, giocatore.getDarkArtsPotionsAttive().size(), "Tutte le pozioni devono essere rimosse");
        assertFalse(giocatore.hasDarkArtsPotions());
    }
    
    @Test
    void testPuoGiocareAlleati() {
        // Senza pozioni che bloccano alleati
        assertTrue(darkArtsManager.puoGiocareAlleati(giocatore), "Deve poter giocare alleati");
        
        // Con pozione che blocca alleati
        DarkArtsPotion potionBloccante = creaDarkArtsPotionTest("Blocking Potion");
        potionBloccante.setBloccaAlleati(true);
        
        darkArtsManager.assegnaPozioneAlGiocatore(potionBloccante, giocatore);
        
        assertFalse(darkArtsManager.puoGiocareAlleati(giocatore), "Non deve poter giocare alleati");
    }
    
    @Test
    void testRisolviEffettiOngoing() {
        // Crea pozione con effetto ongoing che fa perdere vita
        DarkArtsPotion potion = creaDarkArtsPotionTest("Damaging Potion");
        
        Effetto effetto = new Effetto();
        effetto.setType(TipoEffetto.PERDERE_VITA);
        effetto.setQta(1);
        effetto.setTarget("EROE_ATTIVO");
        
        List<Effetto> effetti = new ArrayList<>();
        effetti.add(effetto);
        potion.setEffettiOngoing(effetti);
        
        int vitaIniziale = giocatore.getEroe().getVita();
        
        darkArtsManager.assegnaPozioneAlGiocatore(potion, giocatore);
        darkArtsManager.risolviEffettiOngoing(giocatore);
        
        assertEquals(vitaIniziale - 1, giocatore.getEroe().getVita(), 
                    "Giocatore deve perdere 1 vita dall'effetto ongoing");
    }
    
    @Test
    void testRisolviMultipleEffettiOngoing() {
        DarkArtsPotion potion1 = creaDarkArtsPotionTest("Potion 1");
        DarkArtsPotion potion2 = creaDarkArtsPotionTest("Potion 2");
        
        Effetto effetto1 = new Effetto();
        effetto1.setType(TipoEffetto.PERDERE_VITA);
        effetto1.setQta(1);
        List<Effetto> effetti1 = new ArrayList<>();
        effetti1.add(effetto1);
        potion1.setEffettiOngoing(effetti1);
        
        Effetto effetto2 = new Effetto();
        effetto2.setType(TipoEffetto.PERDERE_VITA);
        effetto2.setQta(2);
        List<Effetto> effetti2 = new ArrayList<>();
        effetti2.add(effetto2);
        potion2.setEffettiOngoing(effetti2);
        
        int vitaIniziale = giocatore.getEroe().getVita();
        
        darkArtsManager.assegnaPozioneAlGiocatore(potion1, giocatore);
        darkArtsManager.assegnaPozioneAlGiocatore(potion2, giocatore);
        darkArtsManager.risolviEffettiOngoing(giocatore);
        
        assertEquals(vitaIniziale - 3, giocatore.getEroe().getVita(), 
                    "Giocatore deve perdere 3 vita totali (1+2)");
    }
    
    @Test
    void testPuoBrew() {
        // Attualmente sempre true, ma preparato per logica futura
        assertTrue(darkArtsManager.puoBrew(giocatore));
        
        DarkArtsPotion potion = creaDarkArtsPotionTest("Some Potion");
        darkArtsManager.assegnaPozioneAlGiocatore(potion, giocatore);
        
        assertTrue(darkArtsManager.puoBrew(giocatore));
    }
    
    // Helper methods
    
    // ✅ CORREZIONE 2: ultimo parametro cambiato da null a new ArrayList<>()
    private DarkArtsPotion creaDarkArtsPotionTest(String nome) {
        DarkArtsPotion potion = new DarkArtsPotion(nome, nome, nome, nome, 0, nome, null, null, new ArrayList<>());
        potion.setNome(nome);
        potion.setDescrizione("Test potion");
        potion.setEffettiOngoing(new ArrayList<>());
        potion.setBloccaAlleati(false);
        return potion;
    }
}
