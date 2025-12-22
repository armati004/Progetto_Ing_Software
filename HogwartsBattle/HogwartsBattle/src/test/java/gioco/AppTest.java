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
    @DisplayName("Test Giocata Carta (Comando 'gioca')")
    void TestPlayCard() {
    	
    }
	
}
