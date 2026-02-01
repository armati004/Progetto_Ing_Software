package gioco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carte.Encounter;
import carte.Eroe;
import data.GameConfig;
import data.HeroFactory;
import gestoreEffetti.Effetto;
import gestoreEffetti.EncounterManager;
import gestoreEffetti.TipoEffetto;

import java.util.ArrayList;
import java.util.List;

/**
 * Test per EncounterManager - Sistema Encounter (Pack 1-4)
 */
class EncounterManagerTest {
    
    private StatoDiGioco stato;
    private EncounterManager encounterManager;
    private Giocatore giocatore;
    
    @BeforeEach
    void setUp() {
        GameConfig config = new GameConfig();
        config.setAnno(4);
        config.setContieneEncounter(true);
        
        HeroFactory.inizializza();
        Eroe ron = HeroFactory.creaEroe("Ron Weasley", 10);
        giocatore = new Giocatore(ron);
        
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(giocatore);
        
        stato = new StatoDiGioco(config, giocatori);
        encounterManager = stato.getEncounterManager();
    }
    
    @Test
    void testInizializzazione() {
        assertNotNull(encounterManager, "EncounterManager deve essere inizializzato");
    }
    
    // ---------------------------------------------------------------
    // CORREZIONE 1: testResetContatori
    // Prima incrementi i conteggi, poi setti l'encounter, poi resetti,
    // e DOPO il reset verifichi che l'encounter non si completi.
    // ---------------------------------------------------------------
    @Test
    void testResetContatori() {
        Encounter enc = creaEncounterTest("Test Reset", "ACQUISTA_INFLUENZA", 5);
        encounterManager.setEncounterCorrente(enc);
        
        // Incrementa oltre il valore richiesto
        encounterManager.aggiungiInfluenzaAcquistata(10);
        assertTrue(encounterManager.verificaCompletamentoEncounter(),
                "Encounter deve completarsi con 10 influenza (richiesti 5)");
        
        // Riprepara un encounter fresco (non completato)
        Encounter enc2 = creaEncounterTest("Test Reset 2", "ACQUISTA_INFLUENZA", 5);
        encounterManager.setEncounterCorrente(enc2);
        
        // Resetta i conteggi
        encounterManager.resetContatori();
        
        // Dopo il reset i conteggi sono a zero: non deve completarsi
        assertFalse(encounterManager.verificaCompletamentoEncounter(),
                "Encounter non deve completarsi dopo reset dei contatori");
        assertFalse(enc2.isCompletato());
    }
    
    @Test
    void testCompletamentoGiocaCarteCondizione() {
        Encounter encounter = creaEncounterTest("Sneaking in the Halls", "GIOCA_CARTE_TIPO", 4);
        encounterManager.setEncounterCorrente(encounter);
        
        assertFalse(encounter.isCompletato(), "Encounter non deve essere completato inizialmente");
        
        // Aggiungi 3 carte giocate (non sufficiente)
        for (int i = 0; i < 3; i++) {
            encounterManager.aggiungiCartaGiocata();
        }
        
        assertFalse(encounterManager.verificaCompletamentoEncounter(), 
                   "Encounter non deve completarsi con 3/4 carte");
        assertFalse(encounter.isCompletato());
        
        // Aggiungi la 4a carta
        encounterManager.aggiungiCartaGiocata();
        
        assertTrue(encounterManager.verificaCompletamentoEncounter(), 
                  "Encounter deve completarsi con 4/4 carte");
        assertTrue(encounter.isCompletato());
    }
    
    @Test
    void testCompletamentoAcquistaInfluenzaCondizione() {
        Encounter encounter = creaEncounterTest("Ministry Meddling", "ACQUISTA_INFLUENZA", 8);
        encounterManager.setEncounterCorrente(encounter);
        
        // Aggiungi 5 influenza (non sufficiente)
        encounterManager.aggiungiInfluenzaAcquistata(5);
        assertFalse(encounterManager.verificaCompletamentoEncounter());
        
        // Aggiungi altri 3 (totale 8)
        encounterManager.aggiungiInfluenzaAcquistata(3);
        assertTrue(encounterManager.verificaCompletamentoEncounter());
        assertTrue(encounter.isCompletato());
    }
    
    @Test
    void testCompletamentoAcquistaAttaccoCondizione() {
        Encounter encounter = creaEncounterTest("Magic's Might", "ACQUISTA_ATTACCO", 8);
        encounterManager.setEncounterCorrente(encounter);
        
        encounterManager.aggiungiAttaccoAcquistato(4);
        assertFalse(encounterManager.verificaCompletamentoEncounter());
        
        encounterManager.aggiungiAttaccoAcquistato(4);
        assertTrue(encounterManager.verificaCompletamentoEncounter());
        assertTrue(encounter.isCompletato());
    }
    
    @Test
    void testCompletamentoGiocaCarteValoreCondizione() {
        Encounter encounter = creaEncounterTest("Detention Dolores", "GIOCA_CARTE_VALORE", 3);
        encounterManager.setEncounterCorrente(encounter);
        
        encounterManager.aggiungiCartaGiocata();
        encounterManager.aggiungiCartaGiocata();
        assertFalse(encounterManager.verificaCompletamentoEncounter());
        
        encounterManager.aggiungiCartaGiocata();
        assertTrue(encounterManager.verificaCompletamentoEncounter());
    }
    
    // ---------------------------------------------------------------
    // CORREZIONE 2: testEncounterGiaCompletato
    // Messaggio di assertion reso chiaro: il metodo ritorna false
    // perché l'encounter è già stato completato in precedenza.
    // ---------------------------------------------------------------
    @Test
    void testEncounterGiaCompletato() {
        Encounter encounter = creaEncounterTest("Already Done", "ACQUISTA_INFLUENZA", 5);
        encounter.setCompletato(true);
        encounterManager.setEncounterCorrente(encounter);
        
        // Aggiungi influenza oltre il richiesto
        encounterManager.aggiungiInfluenzaAcquistata(10);
        
        // Il metodo ritorna false perché l'encounter è già completato:
        // non deve "completarlo di nuovo"
        assertFalse(encounterManager.verificaCompletamentoEncounter(), 
                   "verificaCompletamento deve ritornare false se l'encounter è già completato");
    }
    
    @Test
    void testEncounterNull() {
        encounterManager.setEncounterCorrente(null);
        
        // Nessuno di questi deve causare NPE
        encounterManager.aggiungiCartaGiocata();
        encounterManager.aggiungiInfluenzaAcquistata(10);
        
        assertFalse(encounterManager.verificaCompletamentoEncounter(), 
                   "Non deve completare se encounter è null");
    }
    
    // ---------------------------------------------------------------
    // CORREZIONE 3: testRisolviEffettoOngoing
    // Aggiunta assertion concreta: dopo risolviEffettoOngoing()
    // l'encounter corrente deve ancora essere accessibile e valido.
    // ---------------------------------------------------------------
    @Test
    void testRisolviEffettoOngoing() {
        Encounter encounter = creaEncounterTest("Test Ongoing", "GIOCA_CARTE_TIPO", 5);
        
        Effetto effetto = new Effetto();
        effetto.setType(TipoEffetto.AGGIUNGERE_MARCHIO_NERO);
        effetto.setQta(1);
        effetto.setTarget("LUOGO");
        
        List<Effetto> effettiContinui = new ArrayList<>();
        effettiContinui.add(effetto);
        encounter.setEffettiContinui(effettiContinui);
        
        encounterManager.setEncounterCorrente(encounter);
        
        // Non deve lanciare eccezioni
        assertDoesNotThrow(() -> encounterManager.risolviEffettoOngoing(),
                "risolviEffettoOngoing non deve lanciare eccezioni");
        
        // L'encounter corrente deve ancora essere il stesso e non completato
        assertFalse(encounter.isCompletato(),
                "L'encounter non deve risultare completato dopo risoluzione effetto ongoing");
    }
    
    // ---------------------------------------------------------------
    // CORREZIONE 4: testConteggiSeparati
    // Ogni sotto-test parte da conteggi puliti (resetContatori)
    // per verificare davvero che ogni tipo di conteggio funzioni
    // in modo indipendente.
    // ---------------------------------------------------------------
    @Test
    void testConteggiSeparati() {
        // --- Test GIOCA_CARTE_TIPO ---
        encounterManager.resetContatori();
        encounterManager.aggiungiCartaGiocata();
        encounterManager.aggiungiCartaGiocata();
        
        Encounter enc1 = creaEncounterTest("Cards", "GIOCA_CARTE_TIPO", 2);
        encounterManager.setEncounterCorrente(enc1);
        assertTrue(encounterManager.verificaCompletamentoEncounter(), 
                "Deve completare con 2 carte giocate");
        
        // --- Test ACQUISTA_INFLUENZA ---
        encounterManager.resetContatori();
        encounterManager.aggiungiInfluenzaAcquistata(5);
        
        Encounter enc2 = creaEncounterTest("Influence", "ACQUISTA_INFLUENZA", 5);
        encounterManager.setEncounterCorrente(enc2);
        assertTrue(encounterManager.verificaCompletamentoEncounter(), 
                "Deve completare con 5 influenza acquistata");
        
        // --- Test ACQUISTA_ATTACCO ---
        encounterManager.resetContatori();
        encounterManager.aggiungiAttaccoAcquistato(3);
        
        Encounter enc3 = creaEncounterTest("Attack", "ACQUISTA_ATTACCO", 3);
        encounterManager.setEncounterCorrente(enc3);
        assertTrue(encounterManager.verificaCompletamentoEncounter(), 
                "Deve completare con 3 attacco acquistato");
    }
    
    @Test
    void testAggiungiEventoArtiOscureRisolto() {
        // Verifica che chiamate multiple non causino errori
        assertDoesNotThrow(() -> {
            encounterManager.aggiungiEventoArtiOscureRisolto();
            encounterManager.aggiungiEventoArtiOscureRisolto();
            encounterManager.aggiungiEventoArtiOscureRisolto();
            encounterManager.resetContatori();
        }, "Chiamate multiple a aggiungiEventoArtiOscureRisolto non devono causare errori");
    }
    
    // ---------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------
    
    private Encounter creaEncounterTest(String nome, String tipoCondizione, int valoreRichiesto) {
        Encounter encounter = new Encounter();
        encounter.setNome(nome);
        encounter.setTipoCondizione(tipoCondizione);
        encounter.setValoreRichiesto(valoreRichiesto);
        encounter.setCompletato(false);
        encounter.setEffettiContinui(new ArrayList<>());
        encounter.setReward(new ArrayList<>());
        return encounter;
    }
}