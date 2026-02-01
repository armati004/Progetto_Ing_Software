package gioco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

import carte.Carta;
import carte.Eroe;
import carte.Alleato;
import carte.Incantesimo;
import carte.Oggetto;
import carte.ArteOscura;
import data.CardFactory;
import data.HeroFactory;

import java.io.File;

/**
 * Test per verificare che i path delle immagini vengano caricati correttamente
 */
public class TestPathImmaginiJUnit {
	@BeforeAll
    static void setupClass() {
        System.out.println("=".repeat(60));
        System.out.println("TEST PATH IMMAGINI - ID CORRETTI");
        System.out.println("=".repeat(60));
        CardFactory.inizializza();
        HeroFactory.inizializza();
    }
    
    // ========================================
    // TEST 1: EROI
    // ========================================
    
    @Test
    @Order(1)
    @DisplayName("1. Eroe Harry Potter - Path NON null")
    void testEroePath() {
        System.out.println("\n🧪 TEST EROE:");
        
        Eroe harry = HeroFactory.creaEroe("Harry Potter", 1);
        
        assertNotNull(harry, "❌ Carta harry1 non trovata!");
        assertEquals("Harry Potter", harry.getNome());
        assertEquals("Eroe", harry.getClasse());
        
        String path = harry.getPathImmagine();
        
        System.out.println("   ✅ Nome: " + harry.getNome());
        System.out.println("   ✅ Classe: " + harry.getClasse());
        System.out.println("   Path: " + (path != null ? path : "NULL ❌"));
        
        assertNotNull(path, "❌ PATH EROE È NULL!");
        assertFalse(path.isEmpty(), "❌ PATH EROE È VUOTO!");
    }
    
    // ========================================
    // TEST 2: ALLEATI
    // ========================================
    
    @Test
    @Order(2)
    @DisplayName("2. Alleato Albus Silente - Path NON null")
    void testAlleatoPath() {
        System.out.println("\n🧪 TEST ALLEATO:");
        
        // ID CORRETTO: albusSilente1 (non albus_silente)
        Carta albus = CardFactory.creaCarta("albusSilente1");
        
        assertNotNull(albus, "❌ Carta albusSilente1 non trovata!");
        
        String path = albus.getPathImmagine();
        
        System.out.println("   ✅ Nome: " + albus.getNome());
        System.out.println("   ✅ Classe: " + albus.getClasse());
        System.out.println("   Path: " + (path != null ? path : "NULL ❌"));
        
        assertNotNull(path, "❌ PATH ALLEATO È NULL!");
        assertFalse(path.isEmpty(), "❌ PATH ALLEATO È VUOTO!");
    }
    
    // ========================================
    // TEST 3: INCANTESIMI (CRITICO!)
    // ========================================
    
    @Test
    @Order(3)
    @DisplayName("3. ⚠️ CRITICO: Incantesimo Descendo - Path NON null")
    void testIncantesimoPath() {
        System.out.println("\n🧪 TEST INCANTESIMO:");
        
        // ID CORRETTO: descendo1 (non descendo)
        Carta descendo = CardFactory.creaCarta("descendo1");
        
        assertNotNull(descendo, "❌ Carta descendo1 non trovata!");
        assertEquals("Incantesimo", descendo.getClasse());
        
        String path = descendo.getPathImmagine();
        
        System.out.println("   ✅ Nome: " + descendo.getNome());
        System.out.println("   ✅ Classe: " + descendo.getClasse());
        System.out.println("   Path: " + (path != null ? path : "NULL ❌❌❌"));
        
        assertNotNull(path, 
            "❌❌❌ PATH INCANTESIMO È NULL!\n" +
            "Carta.java NON ha @SerializedName(value=\"pathImmagine\", alternate={\"path-img\"})");
        assertFalse(path.isEmpty(), "❌ PATH INCANTESIMO È VUOTO!");
        
        System.out.println("\n   ✅✅✅ Path trovato: " + path);
    }
    
    // ========================================
    // TEST 4: OGGETTI
    // ========================================
    
    @Test
    @Order(4)
    @DisplayName("4. Oggetto Attrezzatura Quidditch - Path NON null")
    void testOggettoPath() {
        System.out.println("\n🧪 TEST OGGETTO:");
        
        // ID CORRETTO: attrezzaturaQuidditch1 (non attrezzatura_quidditch)
        Carta oggetto = CardFactory.creaCarta("attrezzaturaQuidditch1");
        
        assertNotNull(oggetto, "❌ Carta attrezzaturaQuidditch1 non trovata!");
        
        String path = oggetto.getPathImmagine();
        
        System.out.println("   ✅ Nome: " + oggetto.getNome());
        System.out.println("   ✅ Classe: " + oggetto.getClasse());
        System.out.println("   Path: " + (path != null ? path : "NULL ❌"));
        
        assertNotNull(path, "❌ PATH OGGETTO È NULL!");
        assertFalse(path.isEmpty(), "❌ PATH OGGETTO È VUOTO!");
    }
    
    // ========================================
    // TEST 5: ANALISI COMPLETA
    // ========================================
    
    @Test
    @Order(5)
    @DisplayName("5. Analisi path di varie carte")
    void testAnalisiCompleta() {
        System.out.println("\n📊 ANALISI COMPLETA:");
        System.out.println("=".repeat(60));
        
        // ID CORRETTI
        String[] carteTest = {
            "harry1",
            "hermione1",
            "albusSilente1",
            "descendo1",         // ← Questo è il test CRITICO
            "expelliarmus2",
            "lumos1",
            "attrezzaturaQuidditch1",
            "boccinoOro1"
        };
        
        int totale = 0;
        int conPath = 0;
        int pathNull = 0;
        
        for (String id : carteTest) {
            Carta carta = CardFactory.creaCarta(id);
            
            if (carta == null) {
                System.out.println("\n❌ ID: " + id + " - NON TROVATA");
                continue;
            }
            
            totale++;
            String path = carta.getPathImmagine();
            
            System.out.println("\n✅ " + id);
            System.out.println("   Nome: " + carta.getNome());
            System.out.println("   Classe: " + carta.getClasse());
            
            if (path == null) {
                System.out.println("   Path: NULL ❌❌❌");
                pathNull++;
            } else if (path.isEmpty()) {
                System.out.println("   Path: VUOTO ❌");
                pathNull++;
            } else {
                System.out.println("   Path: " + path + " ✅");
                conPath++;
                
                // Estrai nome file e verifica esistenza
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                File file = new File("/mnt/project/" + fileName);
                System.out.println("   File exists: " + (file.exists() ? "✅" : "⚠️"));
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📈 STATISTICHE:");
        System.out.println("   Totale carte: " + totale);
        System.out.println("   Con path valido: " + conPath);
        System.out.println("   Con path NULL/vuoto: " + pathNull);
        
        if (pathNull > 0) {
            System.out.println("\n   ❌❌❌ " + pathNull + " CARTE HANNO PATH NULL!");
            System.out.println("   → Carta.java NON compilato con @SerializedName!");
        } else {
            System.out.println("\n   ✅✅✅ Tutte le carte hanno path validi!");
        }
        System.out.println("=".repeat(60));
        
        // Assert finale
        assertEquals(0, pathNull, 
            "❌ " + pathNull + " carte su " + totale + " hanno path null/vuoto!");
    }
    
    // ========================================
    // TEST 6: VERIFICA TIPI DIVERSI
    // ========================================
    
    @Test
    @Order(6)
    @DisplayName("6. Verifica path per ogni tipo di carta")
    void testOgniTipo() {
        System.out.println("\n🔍 VERIFICA PER TIPO:");
        
        // Un esempio per ogni tipo
        String[][] tests = {
            {"albusSilente1", "Alleato"},
            {"descendo1", "Incantesimo"},
            {"attrezzaturaQuidditch1", "Oggetto"}
        };
        
        boolean tuttiOk = true;
        
        for (String[] test : tests) {
            String id = test[0];
            String tipoAtteso = test[1];
            
            Carta carta = CardFactory.creaCarta(id);
            
            if (carta == null) {
                System.out.println("\n❌ " + tipoAtteso + ": carta non trovata (ID: " + id + ")");
                tuttiOk = false;
                continue;
            }
            
            String path = carta.getPathImmagine();
            boolean pathOk = (path != null && !path.isEmpty());
            
            System.out.println("\n" + (pathOk ? "✅" : "❌") + " " + tipoAtteso + ":");
            System.out.println("   ID: " + id);
            System.out.println("   Nome: " + carta.getNome());
            System.out.println("   Path: " + (pathOk ? path : "NULL/VUOTO"));
            
            if (!pathOk) {
                tuttiOk = false;
            }
        }
        
        assertTrue(tuttiOk, "❌ Alcuni tipi di carte hanno problemi con il path!");
    }
    
    @AfterAll
    static void teardown() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("FINE TEST");
        System.out.println("=".repeat(60));
    }
}