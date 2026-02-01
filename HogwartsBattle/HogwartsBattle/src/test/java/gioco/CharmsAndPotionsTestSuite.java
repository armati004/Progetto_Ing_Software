package gioco;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite completa per l'espansione Charms & Potions
 * 
 * Esegue tutti i test per verificare:
 * - Sistema Pozioni (Pack 2+)
 * - Sistema Dark Arts Potions (Pack 3+)
 * - Sistema Encounter (Pack 1-4)
 * - Nuove funzionalità Giocatore
 * - Flusso turni aggiornato
 */
@Suite
@SelectClasses({
    PotionManagerTest.class,
    DarkArtsPotionManagerTest.class,
    EncounterManagerTest.class,
    GiocatoreTest.class,
    TurnManagerTest.class
})
public class CharmsAndPotionsTestSuite {
    // Test suite eseguirà tutti i test delle classi specificate
}
