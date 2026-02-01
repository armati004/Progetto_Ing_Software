package gioco;

import carte.*;
import data.CardFactory;
import data.HeroFactory;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Test SENZA JavaFX per verificare il caricamento delle immagini
 * Verifica solo che i file esistano e siano leggibili
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestJavaFXImageRendering {
    
    @BeforeAll
    static void setup() {
        System.out.println("=".repeat(70));
        System.out.println("TEST IMMAGINI - VERIFICA FILE SYSTEM");
        System.out.println("=".repeat(70));
        
        CardFactory.inizializza();
        HeroFactory.inizializza();
    }
    
    // ========================================
    // TEST 1: Verifica Path e File Esistono
    // ========================================
    
    @Test
    @Order(1)
    @DisplayName("1. Verifica path e file per ogni tipo di carta")
    void testPathEFileEsistono() {
        System.out.println("\n📋 TEST: Verifica Path e File");
        System.out.println("-".repeat(70));
        
        String[][] tests = {
            {"harry1", "Eroe", "HeroFactory"},
            {"albusSilente1", "Alleato", "CardFactory"},
            {"descendo1", "Incantesimo", "CardFactory"},
            {"attrezzaturaQuidditch1", "Oggetto", "CardFactory"}
        };
        
        boolean tuttiOk = true;
        
        for (String[] test : tests) {
            String id = test[0];
            String tipo = test[1];
            String factory = test[2];
            
            System.out.println("\n🔍 " + tipo + " (ID: " + id + ")");
            
            // Carica carta
            Carta carta = null;
            if (factory.equals("HeroFactory")) {
                carta = HeroFactory.creaEroe("Harry Potter", 1);
            } else {
                carta = CardFactory.creaCarta(id);
            }
            
            if (carta == null) {
                System.out.println("   ❌ Carta non trovata via " + factory);
                tuttiOk = false;
                continue;
            }
            
            System.out.println("   ✅ Carta trovata: " + carta.getNome());
            
            // Verifica path
            String path = carta.getPathImmagine();
            if (path == null || path.isEmpty()) {
                System.out.println("   ❌ PATH NULL O VUOTO");
                tuttiOk = false;
                continue;
            }
            
            System.out.println("   ✅ Path: " + path);
            
            // Analizza path
            boolean isRelative = path.startsWith("../");
            boolean isAbsolute = path.startsWith("/");
            
            System.out.println("   Tipo path: " + 
                (isRelative ? "Relativo (../)" : 
                 isAbsolute ? "Assoluto (/)" : "Altri"));
            
            // Estrai nome file
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            System.out.println("   Nome file: " + fileName);
            
            // Prova a trovare il file
            String[] locations = {
                "/mnt/project/" + fileName,
                "/mnt/project/" + path,
                "/mnt/project/" + path.replace("../", "")
            };
            
            boolean fileFound = false;
            String validLocation = null;
            long fileSize = 0;
            
            for (String location : locations) {
                File file = new File(location);
                if (file.exists() && file.isFile()) {
                    fileFound = true;
                    validLocation = location;
                    fileSize = file.length();
                    break;
                }
            }
            
            if (fileFound) {
                System.out.println("   ✅ File trovato: " + validLocation);
                System.out.println("   Dimensione: " + fileSize + " bytes (" + 
                    (fileSize / 1024) + " KB)");
                
                // Verifica leggibilità
                try (InputStream is = new FileInputStream(validLocation)) {
                    byte[] buffer = new byte[4];
                    int read = is.read(buffer);
                    
                    if (read > 0) {
                        System.out.println("   ✅ File leggibile");
                        
                        // Verifica magic bytes PNG
                        if (buffer[0] == (byte)0x89 && buffer[1] == 0x50 && 
                            buffer[2] == 0x4E && buffer[3] == 0x47) {
                            System.out.println("   ✅ Formato: PNG valido");
                        } else if (buffer[0] == (byte)0xFF && buffer[1] == (byte)0xD8) {
                            System.out.println("   ⚠️ Formato: JPEG (ma estensione .png)");
                        } else {
                            System.out.println("   ⚠️ Formato: Sconosciuto");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("   ❌ Errore lettura: " + e.getMessage());
                    tuttiOk = false;
                }
                
            } else {
                System.out.println("   ❌ FILE NON TROVATO in:");
                for (String loc : locations) {
                    System.out.println("      - " + loc);
                }
                tuttiOk = false;
            }
        }
        
        System.out.println("\n" + "-".repeat(70));
        assertTrue(tuttiOk, "Alcuni file hanno problemi");
    }
    
    // ========================================
    // TEST 2: Confronto Path Eroi vs Altri
    // ========================================
    
    @Test
    @Order(2)
    @DisplayName("2. Confronto formato path Eroi vs Incantesimi")
    void testConfrontoFormatoPath() {
        System.out.println("\n⚖️ TEST: Confronto Formato Path");
        System.out.println("-".repeat(70));
        
        // Eroe
        Eroe harry = HeroFactory.creaEroe("Harry Potter", 1);
        String pathEroe = harry != null ? harry.getPathImmagine() : null;
        
        // Incantesimo
        Carta incantesimo = CardFactory.creaCarta("descendo1");
        String pathIncantesimo = incantesimo != null ? incantesimo.getPathImmagine() : null;
        
        System.out.println("🦸 EROE (Harry Potter):");
        if (pathEroe != null) {
            System.out.println("   Path: " + pathEroe);
            System.out.println("   Inizia con '/': " + pathEroe.startsWith("/"));
            System.out.println("   Inizia con '../': " + pathEroe.startsWith("../"));
        } else {
            System.out.println("   ❌ PATH NULL");
        }
        
        System.out.println("\n⚡ INCANTESIMO (Descendo):");
        if (pathIncantesimo != null) {
            System.out.println("   Path: " + pathIncantesimo);
            System.out.println("   Inizia con '/': " + pathIncantesimo.startsWith("/"));
            System.out.println("   Inizia con '../': " + pathIncantesimo.startsWith("../"));
        } else {
            System.out.println("   ❌ PATH NULL");
        }
        
        System.out.println("\n📊 ANALISI:");
        
        if (pathEroe != null && pathIncantesimo != null) {
            boolean stessoFormato = 
                (pathEroe.startsWith("/") && pathIncantesimo.startsWith("/")) ||
                (pathEroe.startsWith("../") && pathIncantesimo.startsWith("../"));
            
            if (stessoFormato) {
                System.out.println("   ✅ Stesso formato di path");
            } else {
                System.out.println("   ⚠️ FORMATI DIVERSI!");
                System.out.println("   → Eroe: " + 
                    (pathEroe.startsWith("/") ? "Assoluto (/)" : "Relativo (../)"));
                System.out.println("   → Incantesimo: " + 
                    (pathIncantesimo.startsWith("/") ? "Assoluto (/)" : "Relativo (../)"));
                System.out.println("\n   🎯 POSSIBILE CAUSA DEL PROBLEMA:");
                System.out.println("   ImageLoader potrebbe non gestire correttamente path relativi (../)");
            }
        }
        
        System.out.println("-".repeat(70));
    }
    
    // ========================================
    // TEST 3: Verifica Directory Immagini
    // ========================================
    
    @Test
    @Order(3)
    @DisplayName("3. Verifica directory e conteggio immagini")
    void testDirectoryImmagini() {
        System.out.println("\n📂 TEST: Directory Immagini");
        System.out.println("-".repeat(70));
        
        String[] directories = {
            "/mnt/project",
            "/mnt/project/Immagini_carte",
            "/mnt/project/Immagini_carte/Eroi",
            "/mnt/project/Immagini_carte/Incantesimo"
        };
        
        for (String dirPath : directories) {
            File dir = new File(dirPath);
            
            System.out.println("\n📁 " + dirPath);
            
            if (!dir.exists()) {
                System.out.println("   ❌ Directory non esiste");
                continue;
            }
            
            if (!dir.isDirectory()) {
                System.out.println("   ❌ Non è una directory");
                continue;
            }
            
            System.out.println("   ✅ Directory esiste");
            
            // Conta file PNG
            File[] pngFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            
            if (pngFiles != null) {
                System.out.println("   File PNG: " + pngFiles.length);
                
                if (pngFiles.length > 0 && pngFiles.length <= 5) {
                    System.out.println("   Esempi:");
                    for (int i = 0; i < Math.min(5, pngFiles.length); i++) {
                        System.out.println("      - " + pngFiles[i].getName());
                    }
                }
            } else {
                System.out.println("   ⚠️ Impossibile leggere directory");
            }
        }
        
        System.out.println("\n" + "-".repeat(70));
        
        // Conta totale PNG in /mnt/project
        File projectDir = new File("/mnt/project");
        File[] allPngs = projectDir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        
        if (allPngs != null && allPngs.length > 0) {
            System.out.println("✅ Totale PNG in /mnt/project: " + allPngs.length);
        } else {
            System.out.println("⚠️ Nessun PNG trovato in /mnt/project root");
            System.out.println("   → Le immagini potrebbero essere in subdirectory");
            System.out.println("   → ImageLoader potrebbe non trovarle se usa path relativi");
        }
        
        System.out.println("-".repeat(70));
    }
    
    // ========================================
    // TEST 4: Suggerimenti Fix
    // ========================================
    
    @Test
    @Order(4)
    @DisplayName("4. Analisi e suggerimenti fix")
    void testSuggerimentiFix() {
        System.out.println("\n💡 SUGGERIMENTI FIX");
        System.out.println("-".repeat(70));
        
        // Verifica se ci sono PNG in subdirectory ma non in root
        File projectRoot = new File("/mnt/project");
        File[] pngsInRoot = projectRoot.listFiles((d, n) -> n.toLowerCase().endsWith(".png"));
        
        int countInRoot = (pngsInRoot != null) ? pngsInRoot.length : 0;
        
        System.out.println("\n📊 Situazione attuale:");
        System.out.println("   PNG in /mnt/project root: " + countInRoot);
        
        if (countInRoot < 10) {
            System.out.println("\n⚠️ PROBLEMA RILEVATO: Poche immagini in root!");
            System.out.println("\n🔧 FIX CONSIGLIATO:");
            System.out.println("   Le immagini sono probabilmente in subdirectory.");
            System.out.println("   Copia tutte le immagini in /mnt/project root:");
            System.out.println();
            System.out.println("   Bash command:");
            System.out.println("   find /mnt/project -name \"*.png\" -exec cp {} /mnt/project/ \\;");
            System.out.println();
            System.out.println("   Questo risolverà i problemi con path relativi (../)");
        } else {
            System.out.println("\n✅ OK: Molte immagini già in root");
        }
        
        System.out.println("-".repeat(70));
    }
    
    @AfterAll
    static void teardown() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📊 RIEPILOGO");
        System.out.println("=".repeat(70));
        System.out.println("✅ Test completato senza JavaFX");
        System.out.println("✅ Verificati: path, file, formati, directory");
        System.out.println();
        System.out.println("🎯 PROSSIMI PASSI:");
        System.out.println("1. Controlla l'output del TEST 2 (confronto path)");
        System.out.println("2. Se i formati sono diversi → Questo è il problema!");
        System.out.println("3. Applica il fix suggerito nel TEST 4");
        System.out.println("=".repeat(70));
    }
}