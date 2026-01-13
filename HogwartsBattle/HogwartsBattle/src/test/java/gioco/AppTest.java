package gioco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import data.*;
import carte.*;
import gestoreEffetti.*;
import gioco.InputController;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private StatoDiGioco stato;
    private InputController controller;
    private Giocatore harry;
    
    @BeforeAll
    static void setupGlobal() {
        // Inizializza le Factory una volta sola
        CardFactory.inizializza();
        VillainFactory.inizializza();
        StarterPackLoader.inizializza(); // Se lo hai implementato
        ProficiencyFactory.inizializza();
        HorcruxFactory.inizializza();
        HeroFactory.inizializza();
    }
    
    @BeforeEach
    void setupGame() {
    	List<Giocatore> giocatori = new ArrayList<>();
    	harry = new Giocatore(HeroFactory.creaEroe("Harry Potter", 1));
    	giocatori.add(harry);
    	giocatori.add(new Giocatore(HeroFactory.creaEroe("Ron Weasley", 1)));
    	GameLoader loader = new GameLoader();
    	GameConfig config = loader.caricaConfigurazione(1);
    	
    	stato = new StatoDiGioco(config, giocatori);
    	controller = new InputController(stato);
    	
    	stato.setFaseCorrente(FaseTurno.GIOCA_CARTE);
    }
    
    @Test
    @DisplayName("Test Inizializzazione Partita")
    void testGameInit() {
    	assertNotNull(stato.getMercato(), "Il mercato non deve essere nullo");
    	assertEquals(6, stato.getMercato().size(), "Il mercato deve avere 6 carte");
    	assertEquals(1, stato.getMalvagiAttivi().size(), "Anno 1 inizia con 1 malvagio");
    	assertEquals("Harry Potter", stato.getGiocatori().get(stato.getGiocatoreCorrente()).getEroe().getNome());
    }
    
    @Test
    @DisplayName("Test Giocata Carta")
    void testPlayCard() {
        // 1. CHIEDI CHI È IL GIOCATORE ATTIVO (Non assumere sia Harry)
        Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        // 2. PREPARAZIONE SU QUEL GIOCATORE
        giocatoreAttivo.getMano().clear(); // Pulisci la sua mano
        stato.setFaseCorrente(FaseTurno.GIOCA_CARTE); // Forza la fase

        // 3. AGGIUNGI LA CARTA AL GIOCATORE ATTIVO
        Carta card = CardFactory.creaCarta("incendio1");
        giocatoreAttivo.getMano().add(card);
        
        // 4. ESEGUI IL COMANDO
        // Nota: usiamo 1 perché l'utente conta da 1
        String response = controller.processaComando("gioca 1");
        
        // 5. DEBUG (Se fallisce ancora)
        System.out.println("Giocatore attivo: " + giocatoreAttivo.getEroe().getNome());
        System.out.println("Risposta: " + response);

        // 6. ASSERT
        assertTrue(response.contains("Hai giocato"), "Dovrebbe confermare la giocata");
        assertFalse(giocatoreAttivo.getMano().contains(card), "Carta rimossa dalla mano");
        assertTrue(giocatoreAttivo.getScarti().getCarte().contains(card), "Carta negli scarti");
    }
    
    @Test
    @DisplayName("Test Acquisto Carta (Comando 'compra')")
    void testBuyCard() {
    	
    	harry.setGettone(harry.getGettone() + 100);
    	
    	stato.setFaseCorrente(FaseTurno.ACQUISTA_CARTE);
    	
    	Carta target = stato.getMercato().get(0);
    	
    	String cardName = target.getNome();
        
        // Azione
        String response = controller.processaComando("compra 0");
        
        // Verifiche
        assertTrue(response.contains("Hai comprato"), "Messaggio di successo atteso");
        assertTrue(harry.getScarti().getCarte().contains(target), "La carta deve essere negli scarti");
        assertNotEquals(cardName, stato.getMercato().get(0).getNome(), "Il mercato deve essersi ricaricato con una carta diversa");
    }
    
    @Test
    @DisplayName("Test attacco Malvagio")
    void testAttackVillain() {
    	stato.setFaseCorrente(FaseTurno.ATTACCA);
    	harry.setAttacco(harry.getAttacco() + 1);
    	Malvagio m = stato.getMalvagiAttivi().get(0);
    	
    	int initialHealth = m.getDanno();
        
        // Azione
        String response = controller.processaComando("attacca 0");
        
        // Verifiche
        assertTrue(response.contains("Colpito"), "Messaggio colpito atteso");
        assertEquals(initialHealth + 1, m.getDanno(), "Il malvagio deve aver perso 1 vita");
        assertEquals(0, harry.getAttacco(), "Il giocatore deve aver speso l'attacco");
    }
    
    @Test
    @DisplayName("Test Flusso Fasi Turno")
    void testTurnPhases() {
        stato.setFaseCorrente(FaseTurno.GIOCA_CARTE);
        
        controller.processaComando("next");
        assertEquals(FaseTurno.ATTACCA, stato.getFaseCorrente());
        
        controller.processaComando("next");
        assertEquals(FaseTurno.ACQUISTA_CARTE, stato.getFaseCorrente());
        
        controller.processaComando("next");
        // Dopo BUY, il cleanup imposta a DARK_ARTS e cambia giocatore
        assertEquals(FaseTurno.ARTI_OSCURE, stato.getFaseCorrente());
        assertNotEquals(harry, stato.getGiocatori().get(stato.getGiocatoreCorrente()), "Il giocatore attivo deve essere cambiato");
    }
	
}