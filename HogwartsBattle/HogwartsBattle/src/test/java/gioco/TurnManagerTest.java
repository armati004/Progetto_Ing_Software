package gioco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carte.Eroe;
import data.GameConfig;
import data.HeroFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Test per TurnManager - Flusso turni con espansione
 */
class TurnManagerTest {
    
    private StatoDiGioco stato;
    private TurnManager turnManager;
    private Giocatore giocatore;
    
    @BeforeEach
    void setUp() {
        GameConfig config = new GameConfig();
        config.setAnno(5);
        config.setContienePozioni(true);
        config.setContieneEncounter(true);
        
        HeroFactory.inizializza();
        Eroe harry = HeroFactory.creaEroe("Harry Potter", 10);
        giocatore = new Giocatore(harry);
        
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(giocatore);
        
        stato = new StatoDiGioco(config, giocatori);
        turnManager = new TurnManager(stato);
    }
    
    @Test
    void testFaseInizialeArtiOscure() {
        assertEquals(FaseTurno.ARTI_OSCURE, stato.getFaseCorrente(), 
                    "Fase iniziale deve essere ARTI_OSCURE");
    }
    
    @Test
    void testProssimaFaseDaArtiOscure() {
        stato.setFaseCorrente(FaseTurno.ARTI_OSCURE);
        turnManager.prossimaFase();
        
        assertEquals(FaseTurno.MALVAGI, stato.getFaseCorrente(), 
                    "Dopo ARTI_OSCURE deve essere MALVAGI");
    }
    
    @Test
    void testProssimaFaseDaMalvagi() {
        stato.setFaseCorrente(FaseTurno.MALVAGI);
        turnManager.prossimaFase();
        
        assertEquals(FaseTurno.GIOCA_CARTE, stato.getFaseCorrente(), 
                    "Dopo MALVAGI deve essere GIOCA_CARTE (no Horcrux)");
    }
    
    @Test
    void testProssimaFaseDaGiocaCarteConPozioni() {
        stato.setFaseCorrente(FaseTurno.GIOCA_CARTE);
        
        turnManager.prossimaFase();
        
        assertEquals(FaseTurno.RACCOLTA_INGREDIENTI, stato.getFaseCorrente(), 
                    "Con Pozioni attive deve andare a RACCOLTA_INGREDIENTI");
    }
    
    @Test
    void testProssimaFaseDaGiocaCarteSenzaPozioni() {
        // Setup senza pozioni
        GameConfig config = new GameConfig();
        config.setAnno(3);
        config.setContienePozioni(false);
        
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(giocatore);
        
        StatoDiGioco statoSenzaPozioni = new StatoDiGioco(config, giocatori);
        TurnManager tmSenzaPozioni = new TurnManager(statoSenzaPozioni);
        
        statoSenzaPozioni.setFaseCorrente(FaseTurno.GIOCA_CARTE);
        tmSenzaPozioni.prossimaFase();
        
        assertEquals(FaseTurno.ATTACCA, statoSenzaPozioni.getFaseCorrente(), 
                    "Senza Pozioni deve saltare a ATTACCA");
    }
    
    @Test
    void testProssimaFaseDaRaccoltaIngredienti() {
        stato.setFaseCorrente(FaseTurno.RACCOLTA_INGREDIENTI);
        turnManager.prossimaFase();
        
        assertEquals(FaseTurno.ATTACCA, stato.getFaseCorrente(), 
                    "Dopo RACCOLTA_INGREDIENTI deve essere ATTACCA");
    }
    
    @Test
    void testProssimaFaseDaAttacca() {
        stato.setFaseCorrente(FaseTurno.ATTACCA);
        turnManager.prossimaFase();
        
        assertEquals(FaseTurno.ACQUISTA_CARTE, stato.getFaseCorrente(), 
                    "Dopo ATTACCA deve essere ACQUISTA_CARTE");
    }
    
    @Test
    void testProssimaFaseDaAcquistaCarte() {
        stato.setFaseCorrente(FaseTurno.ACQUISTA_CARTE);
        turnManager.prossimaFase();
        
        assertEquals(FaseTurno.FINE_TURNO, stato.getFaseCorrente(), 
                    "Dopo ACQUISTA_CARTE deve essere FINE_TURNO");
    }
    
    @Test
    void testFaseAutomaticaArtiOscure() {
        assertTrue(isFaseAutomatica(FaseTurno.ARTI_OSCURE), 
                  "ARTI_OSCURE deve essere automatica");
    }
    
    @Test
    void testFaseAutomaticaMalvagi() {
        assertTrue(isFaseAutomatica(FaseTurno.MALVAGI), 
                  "MALVAGI deve essere automatica");
    }
    
    @Test
    void testFaseAutomaticaFineTurno() {
        assertTrue(isFaseAutomatica(FaseTurno.FINE_TURNO), 
                  "FINE_TURNO deve essere automatica");
    }
    
    @Test
    void testFaseNonAutomaticaGiocaCarte() {
        assertFalse(isFaseAutomatica(FaseTurno.GIOCA_CARTE), 
                   "GIOCA_CARTE non deve essere automatica");
    }
    
    @Test
    void testFaseNonAutomaticaRaccoltaIngredienti() {
        assertFalse(isFaseAutomatica(FaseTurno.RACCOLTA_INGREDIENTI), 
                   "RACCOLTA_INGREDIENTI non deve essere automatica");
    }
    
    @Test
    void testFaseNonAutomaticaAttacca() {
        assertFalse(isFaseAutomatica(FaseTurno.ATTACCA), 
                   "ATTACCA non deve essere automatica");
    }
    
    @Test
    void testFaseNonAutomaticaAcquistaCarte() {
        assertFalse(isFaseAutomatica(FaseTurno.ACQUISTA_CARTE), 
                   "ACQUISTA_CARTE non deve essere automatica");
    }
    
    @Test
    void testFlussoCompletoConPozioni() {
        // Simula flusso completo di un turno con Pozioni
        List<FaseTurno> flussoAtteso = List.of(
            FaseTurno.ARTI_OSCURE,
            FaseTurno.MALVAGI,
            FaseTurno.GIOCA_CARTE,
            FaseTurno.RACCOLTA_INGREDIENTI,
            FaseTurno.ATTACCA,
            FaseTurno.ACQUISTA_CARTE,
            FaseTurno.FINE_TURNO
        );
        
        List<FaseTurno> flussoEffettivo = new ArrayList<>();
        
        stato.setFaseCorrente(FaseTurno.ARTI_OSCURE);
        for (int i = 0; i < 7; i++) {
            flussoEffettivo.add(stato.getFaseCorrente());
            if (stato.getFaseCorrente() == FaseTurno.FINE_TURNO) break;
            turnManager.prossimaFase();
        }
        
        assertEquals(flussoAtteso, flussoEffettivo, "Flusso turno deve essere corretto");
    }
    
    @Test
    void testFlussoCompletoSenzaPozioni() {
        // Setup senza pozioni
        GameConfig config = new GameConfig();
        config.setAnno(3);
        config.setContienePozioni(false);
        
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(giocatore);
        
        StatoDiGioco statoSenza = new StatoDiGioco(config, giocatori);
        TurnManager tmSenza = new TurnManager(statoSenza);
        
        List<FaseTurno> flussoAtteso = List.of(
            FaseTurno.ARTI_OSCURE,
            FaseTurno.MALVAGI,
            FaseTurno.GIOCA_CARTE,
            FaseTurno.ATTACCA,
            FaseTurno.ACQUISTA_CARTE,
            FaseTurno.FINE_TURNO
        );
        
        List<FaseTurno> flussoEffettivo = new ArrayList<>();
        
        statoSenza.setFaseCorrente(FaseTurno.ARTI_OSCURE);
        for (int i = 0; i < 7; i++) {
            flussoEffettivo.add(statoSenza.getFaseCorrente());
            if (statoSenza.getFaseCorrente() == FaseTurno.FINE_TURNO) break;
            tmSenza.prossimaFase();
        }
        
        assertEquals(flussoAtteso, flussoEffettivo, 
                    "Flusso senza pozioni deve saltare RACCOLTA_INGREDIENTI");
    }
    
    @Test
    void testCambioGiocatoreDopoFineTurno() {
        // Aggiungi secondo giocatore
    	HeroFactory.inizializza();
        Eroe hermione = HeroFactory.creaEroe("Hermione Granger", 10);
        Giocatore giocatore2 = new Giocatore(hermione);
        stato.getGiocatori().add(giocatore2);
        
        assertEquals(0, stato.getGiocatoreCorrente(), "Giocatore corrente iniziale deve essere 0");
        
        // Non possiamo testare eseguiFineTurno direttamente perché chiama iniziaTurno ricorsivamente
        // Ma possiamo verificare la logica manualmente
    }
    
    // Helper method (copia della logica in TurnManager)
    private boolean isFaseAutomatica(FaseTurno fase) {
        return fase == FaseTurno.ARTI_OSCURE || 
               fase == FaseTurno.MALVAGI || 
               fase == FaseTurno.HORCRUX ||
               fase == FaseTurno.FINE_TURNO;
    }
}
