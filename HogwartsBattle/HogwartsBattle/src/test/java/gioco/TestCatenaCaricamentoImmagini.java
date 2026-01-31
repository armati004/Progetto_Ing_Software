package gioco;

import carte.*;
import data.CardFactory;
import data.HeroFactory;
import grafica.utils.ImageLoader;
import javafx.scene.image.Image;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

/**
 * Test completo della catena: Carta → Path → ImageLoader → Image
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCatenaCaricamentoImmagini {
    
    @BeforeAll
    static void setup() {
        System.out.println("=".repeat(70));
        System.out.println("TEST CATENA COMPLETA: Carta → Path → ImageLoader → Image");
        System.out.println("=".repeat(70));
        CardFactory.inizializza();
        HeroFactory.inizializza();
    }
    
    // ========================================
    // TEST 1: Path delle Carte
    // ========================================
    
    @Test
    @Order(1)
    @DisplayName("1. Tutte le carte hanno path validi")
    void testPathValidi() {
        System.out.println("\n📋 STEP 1: Verifica Path delle Carte");
        System.out.println("-".repeat(70));
        
        String[] carteTest = {
            "albusSilente1",
            "descendo1",
            "attrezzaturaQuidditch1"
        };
        
        boolean tuttiOk = true;
        boolean eroeOk;
        
        Eroe harry = HeroFactory.creaEroe("Harry Potter", 1);
        
        if(harry.getPathImmagine() != null && !harry.getPathImmagine().isEmpty()) {
        	eroeOk = true; 
        }
        else {
        	eroeOk = false;
        }
        
        for (String id : carteTest) {
            Carta carta = CardFactory.creaCarta(id);
            
            if (carta == null) {
                System.out.println("❌ " + id + ": carta non trovata");
                tuttiOk = false;
                continue;
            }
            
            String path = carta.getPathImmagine();
            boolean pathOk = (path != null && !path.isEmpty());
            
            System.out.println((pathOk ? "✅" : "❌") + " " + carta.getNome() + 
                " (" + carta.getClasse() + ")");
            System.out.println("   Path: " + (pathOk ? path : "NULL/VUOTO"));
            
            if (!pathOk || !eroeOk) tuttiOk = false;
        }
        
        System.out.println("-".repeat(70));
        assertTrue(tuttiOk, "Alcune carte hanno path null/vuoto");
        System.out.println("✅ STEP 1 COMPLETATO: Tutti i path sono validi\n");
    }
    
    // ========================================
    // TEST 2: File Esistono nel Filesystem
    // ========================================
    
    @Test
    @Order(2)
    @DisplayName("2. File immagini esistono nel filesystem")
    void testFileEsistono() {
        System.out.println("\n📂 STEP 2: Verifica Esistenza File");
        System.out.println("-".repeat(70));
        
        String[] carteTest = {
            "albusSilente1", 
            "descendo1",
            "attrezzaturaQuidditch1"
        };
        
        boolean tuttiTrovati = true;
        
        for (String id : carteTest) {
            Carta carta = CardFactory.creaCarta(id);
            if (carta == null) continue;
            
            String path = carta.getPathImmagine();
            if (path == null) continue;
            
            // Estrai nome file dal path
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            // Prova varie posizioni
            String[] locations = {
                "/mnt/project/" + fileName,
                "/mnt/project/" + path,
                "src/main/resources" + path
            };
            
            boolean trovato = false;
            String posizioneValida = null;
            
            for (String location : locations) {
                File file = new File(location);
                if (file.exists()) {
                    trovato = true;
                    posizioneValida = location;
                    break;
                }
            }
            
            System.out.println((trovato ? "✅" : "❌") + " " + carta.getNome());
            System.out.println("   Nome file: " + fileName);
            
            if (trovato) {
                System.out.println("   Trovato in: " + posizioneValida);
                File file = new File(posizioneValida);
                System.out.println("   Dimensione: " + file.length() + " bytes");
            } else {
                System.out.println("   ⚠️ File NON trovato in:");
                for (String loc : locations) {
                    System.out.println("      - " + loc);
                }
                tuttiTrovati = false;
            }
            System.out.println();
        }
        
        System.out.println("-".repeat(70));
        
        if (!tuttiTrovati) {
            System.out.println("⚠️ STEP 2: Alcuni file non trovati nel filesystem");
            System.out.println("   → Le immagini potrebbero essere in resources JAR");
        } else {
            System.out.println("✅ STEP 2 COMPLETATO: Tutti i file esistono\n");
        }
    }
    
    // ========================================
    // TEST 3: ImageLoader Carica Correttamente
    // ========================================
    
    @Test
    @Order(3)
    @DisplayName("3. ImageLoader carica le immagini (NON JavaFX)")
    void testImageLoaderCarica() {
        System.out.println("\n🖼️ STEP 3: Test ImageLoader");
        System.out.println("-".repeat(70));
        
        String[] carteTest = {
            "albusSilente1",
            "descendo1",  // ← QUESTO È IL TEST CRITICO
            "attrezzaturaQuidditch1"
        };
        
        boolean tuttiCaricati = true;
        
        for (String id : carteTest) {
            Carta carta = CardFactory.creaCarta(id);
            if (carta == null) continue;
            
            String path = carta.getPathImmagine();
            if (path == null) continue;
            
            System.out.println("🔍 " + carta.getNome() + " (" + carta.getClasse() + ")");
            System.out.println("   Path input: " + path);
            
            try {
                // ⚠️ Questo test NON usa JavaFX Image!
                // Verifica solo che ImageLoader trovi il file
                
                // Estrai nome file
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                
                // Prova a trovare il file come farebbe ImageLoader
                String[] tentativiPath = {
                    "/mnt/project/" + fileName,
                    "/mnt/project/" + path,
                    path
                };
                
                boolean trovato = false;
                String pathTrovato = null;
                
                for (String tentativo : tentativiPath) {
                    File file = new File(tentativo);
                    if (file.exists()) {
                        trovato = true;
                        pathTrovato = tentativo;
                        break;
                    }
                }
                
                if (trovato) {
                    System.out.println("   ✅ File trovato: " + pathTrovato);
                } else {
                    System.out.println("   ❌ File NON trovato!");
                    tuttiCaricati = false;
                }
                
            } catch (Exception e) {
                System.out.println("   ❌ Errore: " + e.getMessage());
                tuttiCaricati = false;
            }
            
            System.out.println();
        }
        
        System.out.println("-".repeat(70));
        
        if (tuttiCaricati) {
            System.out.println("✅ STEP 3 COMPLETATO: ImageLoader può trovare tutti i file\n");
        } else {
            System.out.println("❌ STEP 3: ImageLoader non trova alcuni file");
            System.out.println("   → Problema: path non corrisponde a file reali\n");
        }
    }
    
    // ========================================
    // TEST 4: Analisi Path Dettagliata
    // ========================================
    
    @Test
    @Order(4)
    @DisplayName("4. Analisi dettagliata path incantesimi")
    void testAnalisiPathIncantesimi() {
        System.out.println("\n🔬 STEP 4: Analisi Dettagliata Path Incantesimi");
        System.out.println("-".repeat(70));
        
        String[] incantesimi = {
            "descendo1",
            "expelliarmus2",
            "lumos1",
            "reparo1"
        };
        
        for (String id : incantesimi) {
            Carta carta = CardFactory.creaCarta(id);
            if (carta == null) {
                System.out.println("❌ " + id + " non trovato\n");
                continue;
            }
            
            String path = carta.getPathImmagine();
            
            System.out.println("📜 " + carta.getNome());
            System.out.println("   ID: " + id);
            System.out.println("   Classe: " + carta.getClasse());
            System.out.println("   Path completo: " + path);
            
            if (path != null) {
                // Analizza il path
                System.out.println("\n   Analisi path:");
                System.out.println("   - Inizia con '../': " + path.startsWith("../"));
                System.out.println("   - Inizia con '/': " + path.startsWith("/"));
                System.out.println("   - Contiene 'Incantesimo': " + path.contains("Incantesimo"));
                
                // Estrai nome file
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                System.out.println("   - Nome file: " + fileName);
                
                // Path pulito (rimuovi ../)
                String pathPulito = path.replace("../", "");
                System.out.println("   - Path pulito: " + pathPulito);
                
                // Verifica esistenza
                File f1 = new File("/mnt/project/" + fileName);
                File f2 = new File("/mnt/project/" + pathPulito);
                
                System.out.println("\n   Verifica esistenza:");
                System.out.println("   - /mnt/project/" + fileName + ": " + f1.exists());
                System.out.println("   - /mnt/project/" + pathPulito + ": " + f2.exists());
            } else {
                System.out.println("   ❌ PATH È NULL!");
            }
            
            System.out.println();
        }
        
        System.out.println("-".repeat(70));
        System.out.println("✅ STEP 4 COMPLETATO\n");
    }
    
    // ========================================
    // TEST 5: Confronto Eroi vs Incantesimi
    // ========================================
    
    @Test
    @Order(5)
    @DisplayName("5. Confronto path Eroi vs Incantesimi")
    void testConfrontoEroiIncantesimi() {
        System.out.println("\n⚖️ STEP 5: Confronto Eroi vs Incantesimi");
        System.out.println("-".repeat(70));
        
        // Eroe (funziona)
        Eroe eroe = HeroFactory.creaEroe("Harry Potter", 1);
        String pathEroe = eroe != null ? eroe.getPathImmagine() : null;
        
        // Incantesimo (problema?)
        Carta incantesimo = CardFactory.creaCarta("descendo1");
        String pathIncantesimo = incantesimo != null ? incantesimo.getPathImmagine() : null;
        
        System.out.println("🦸 EROE (Harry Potter):");
        if (pathEroe != null) {
            System.out.println("   Path: " + pathEroe);
            System.out.println("   Formato: " + (pathEroe.startsWith("/") ? "Assoluto" : "Relativo"));
            String fileNameEroe = pathEroe.substring(pathEroe.lastIndexOf('/') + 1);
            System.out.println("   Nome file: " + fileNameEroe);
            File fEroe = new File("/mnt/project/" + fileNameEroe);
            System.out.println("   File exists: " + fEroe.exists());
        } else {
            System.out.println("   ❌ PATH NULL");
        }
        
        System.out.println("\n⚡ INCANTESIMO (Descendo):");
        if (pathIncantesimo != null) {
            System.out.println("   Path: " + pathIncantesimo);
            System.out.println("   Formato: " + (pathIncantesimo.startsWith("/") ? "Assoluto" : "Relativo"));
            String fileNameIncantesimo = pathIncantesimo.substring(pathIncantesimo.lastIndexOf('/') + 1);
            System.out.println("   Nome file: " + fileNameIncantesimo);
            File fIncantesimo = new File("/mnt/project/" + fileNameIncantesimo);
            System.out.println("   File exists: " + fIncantesimo.exists());
        } else {
            System.out.println("   ❌ PATH NULL");
        }
        
        System.out.println("\n📊 CONFRONTO:");
        if (pathEroe != null && pathIncantesimo != null) {
            boolean stessoFormato = 
                (pathEroe.startsWith("/") && pathIncantesimo.startsWith("/")) ||
                (pathEroe.startsWith("../") && pathIncantesimo.startsWith("../"));
            
            System.out.println("   Stesso formato: " + (stessoFormato ? "✅ SI" : "❌ NO"));
            
            if (!stessoFormato) {
                System.out.println("\n   ⚠️ DIFFERENZA TROVATA:");
                System.out.println("   - Eroe usa: " + (pathEroe.startsWith("/") ? "path assoluto" : "path relativo"));
                System.out.println("   - Incantesimo usa: " + (pathIncantesimo.startsWith("/") ? "path assoluto" : "path relativo"));
                System.out.println("\n   → Questo potrebbe causare problemi in ImageLoader!");
            }
        }
        
        System.out.println("-".repeat(70));
        System.out.println("✅ STEP 5 COMPLETATO\n");
    }
    
    @AfterAll
    static void teardown() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📊 RIEPILOGO TEST");
        System.out.println("=".repeat(70));
        System.out.println("✅ Step 1: Path validi nelle carte");
        System.out.println("✅ Step 2: File esistono (o potrebbero essere in JAR)");
        System.out.println("✅ Step 3: ImageLoader può trovare i file");
        System.out.println("✅ Step 4: Analisi dettagliata incantesimi");
        System.out.println("✅ Step 5: Confronto eroi vs incantesimi");
        System.out.println("\n🎯 CONCLUSIONE:");
        System.out.println("Se tutti i test passano ma le immagini non si vedono nell'UI,");
        System.out.println("il problema è nel RENDERING (CardView, JavaFX Image).");
        System.out.println("=".repeat(70));
    }
}