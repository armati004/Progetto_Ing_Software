package grafica.utils;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility per caricare immagini con strategia multi-livello
 * VERSIONE CORRETTA: NON usa path hardcodati, segue i path dai JSON
 */
public class ImageLoader {
    
    private static final boolean DEBUG = false;
    private static List<String> immaginiMancanti = new ArrayList<>();
    private static List<String> immaginiCaricate = new ArrayList<>();
    
    /**
     * Carica un'immagine provando diverse strategie
     */
    public static Image caricaImmagine(String path) {
        if (path == null || path.isEmpty()) {
            if (DEBUG) System.out.println("   Path null o vuoto");
            return createPlaceholder(100, 150);
        }
        
        if (DEBUG) {
            System.out.println("  Caricamento immagine: " + path);
        }
        
        String cleanPath = pulisciPath(path);
        if (DEBUG) System.out.println("   Path pulito: " + cleanPath);
        
        // STRATEGIA 1: Classpath (resources)
        Image img = caricaDaClasspath(cleanPath);
        if (img != null) {
            immaginiCaricate.add(path);
            if (DEBUG) System.out.println("     Caricata da: classpath");
            return img;
        }
        
        // STRATEGIA 2: Path esatto come indicato nel JSON
        img = caricaDaJSON(path, cleanPath);
        if (img != null) {
            immaginiCaricate.add(path);
            return img;
        }
        
        // STRATEGIA 3: Working directory
        img = caricaDaWorkingDir(path, cleanPath);
        if (img != null) {
            immaginiCaricate.add(path);
            return img;
        }
        
        // STRATEGIA 4: Solo nome file in posizioni comuni
        img = caricaSoloNomeFile(path);
        if (img != null) {
            immaginiCaricate.add(path);
            return img;
        }
        
        // FALLIMENTO: Nessuna strategia ha funzionato
        if (DEBUG) System.out.println("     ERRORE: Immagine non trovata");
        immaginiMancanti.add(path);
        return createPlaceholder(100, 150);
    }
    
    /**
     * Pulisce il path rimuovendo caratteri problematici
     */
    private static String pulisciPath(String path) {
        if (path == null) return "";
        
        // Rimuove spazi
        path = path.trim();
        
        // Normalizza separatori
        path = path.replace("\\", "/");
        
        // Rimuove slash multipli
        path = path.replaceAll("/+", "/");
        
        return path;
    }
    
    /**
     * Estrae solo il nome del file dal path
     */
    private static String estraiNomeFile(String path) {
        if (path == null) return "";
        
        String cleanPath = pulisciPath(path);
        int lastSlash = cleanPath.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < cleanPath.length() - 1) {
            return cleanPath.substring(lastSlash + 1);
        }
        
        return cleanPath;
    }
    
    /**
     * STRATEGIA 1: Carica da classpath (resources/)
     */
    private static Image caricaDaClasspath(String path) {
        if (DEBUG) System.out.println("     Strategia 1: Classpath");
        
        try {
            // Prova con slash
            String resourcePath = "/" + path;
            InputStream stream = ImageLoader.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                if (DEBUG) System.out.println("        Trovato con: " + resourcePath);
                return new Image(stream);
            }
            
            // Prova senza slash
            stream = ImageLoader.class.getResourceAsStream(path);
            if (stream != null) {
                if (DEBUG) System.out.println("        Trovato: " + path);
                return new Image(stream);
            }
            
            // Prova con ClassLoader
            stream = ImageLoader.class.getClassLoader().getResourceAsStream(path);
            if (stream != null) {
                if (DEBUG) System.out.println("        Trovato via ClassLoader");
                return new Image(stream);
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("        Classpath fallito");
        }
        return null;
    }
    
    /**
     * STRATEGIA 2: Path esatto come nei JSON
     * NON usa path hardcoded - prova il path così com'è
     */
    private static Image caricaDaJSON(String originalPath, String cleanPath) {
        if (DEBUG) System.out.println("     Strategia 2: Path dai JSON");
        
        try {
            // 1. Path originale esatto
            if (DEBUG) System.out.println("      Tento: " + originalPath);
            File file = new File(originalPath);
            if (file.exists() && file.isFile()) {
                if (DEBUG) System.out.println("        TROVATO con path originale!");
                return new Image(new FileInputStream(file));
            }
            
            // 2. Path pulito
            if (!originalPath.equals(cleanPath)) {
                if (DEBUG) System.out.println("      Tento: " + cleanPath);
                file = new File(cleanPath);
                if (file.exists() && file.isFile()) {
                    if (DEBUG) System.out.println("        TROVATO con path pulito!");
                    return new Image(new FileInputStream(file));
                }
            }
            
            // 3. Se path inizia con "../", rimuovilo e prova
            if (originalPath.startsWith("../")) {
                String withoutDotDot = originalPath.substring(3);
                if (DEBUG) System.out.println("      Tento senza ../: " + withoutDotDot);
                file = new File(withoutDotDot);
                if (file.exists() && file.isFile()) {
                    if (DEBUG) System.out.println("        TROVATO senza ../!");
                    return new Image(new FileInputStream(file));
                }
                
                // Prova come assoluto
                String assoluto = "/" + withoutDotDot;
                if (DEBUG) System.out.println("      Tento assoluto: " + assoluto);
                file = new File(assoluto);
                if (file.exists() && file.isFile()) {
                    if (DEBUG) System.out.println("        TROVATO come assoluto!");
                    return new Image(new FileInputStream(file));
                }
            }
            
        } catch (Exception e) {
            if (DEBUG) System.out.println("        Errore: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * STRATEGIA 3: Working directory
     */
    private static Image caricaDaWorkingDir(String originalPath, String cleanPath) {
        if (DEBUG) System.out.println("     Strategia 3: Working directory");
        
        try {
            String workingDir = System.getProperty("user.dir");
            if (DEBUG) System.out.println("      Working dir: " + workingDir);
            
            // 1. Working dir + path originale
            String fullPath = workingDir + "/" + originalPath;
            if (DEBUG) System.out.println("      Tento: " + fullPath);
            File file = new File(fullPath);
            if (file.exists() && file.isFile()) {
                if (DEBUG) System.out.println("        TROVATO!");
                return new Image(new FileInputStream(file));
            }
            
            // 2. Working dir + path pulito
            fullPath = workingDir + "/" + cleanPath;
            if (DEBUG) System.out.println("      Tento: " + fullPath);
            file = new File(fullPath);
            if (file.exists() && file.isFile()) {
                if (DEBUG) System.out.println("        TROVATO!");
                return new Image(new FileInputStream(file));
            }
            
            // 3. Working dir + path senza ../
            if (originalPath.startsWith("../")) {
                fullPath = workingDir + "/" + originalPath.substring(3);
                if (DEBUG) System.out.println("      Tento: " + fullPath);
                file = new File(fullPath);
                if (file.exists() && file.isFile()) {
                    if (DEBUG) System.out.println("        TROVATO!");
                    return new Image(new FileInputStream(file));
                }
            }
            
        } catch (Exception e) {
            if (DEBUG) System.out.println("        Errore: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * STRATEGIA 4: Solo nome file in varie posizioni
     */
    private static Image caricaSoloNomeFile(String path) {
        if (DEBUG) System.out.println("    Strategia 4: Solo nome file");
        
        String fileName = estraiNomeFile(path);
        if (DEBUG) System.out.println("      Nome file: " + fileName);
        
        try {
            String workingDir = System.getProperty("user.dir");
            
            // Posizioni comuni da provare
            String[] locations = {
                fileName,                              // File nella directory corrente
                "./" + fileName,                       // Esplicitamente nella directory corrente
                workingDir + "/" + fileName,           // Working directory
                "Immagini_carte/" + fileName,          // Subdirectory comune
                "./Immagini_carte/" + fileName,
                workingDir + "/Immagini_carte/" + fileName
            };
            
            for (String location : locations) {
                if (DEBUG) System.out.println("      Tento: " + location);
                File file = new File(location);
                if (file.exists() && file.isFile()) {
                    if (DEBUG) System.out.println("      TROVATO!");
                    return new Image(new FileInputStream(file));
                }
            }
            
        } catch (Exception e) {
            if (DEBUG) System.out.println("Errore: " + e.getMessage());
        }
        
        if (DEBUG) System.out.println("Non trovato in nessuna posizione");
        return null;
    }
    
    /**
     * Crea immagine placeholder personalizzata
     */
    private static Image createPlaceholder(int width, int height) {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(width, height);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Sfondo grigio scuro
        gc.setFill(javafx.scene.paint.Color.rgb(45, 45, 55));
        gc.fillRect(0, 0, width, height);
        
        // Bordo rosso
        gc.setStroke(javafx.scene.paint.Color.rgb(200, 50, 50));
        gc.setLineWidth(3);
        gc.strokeRect(2, 2, width - 4, height - 4);
        
        // Icona "?" grande
        gc.setFill(javafx.scene.paint.Color.rgb(150, 150, 160));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 60));
        gc.fillText("?", width/2 - 20, height/2 + 20);
        
        // Testo "IMMAGINE MANCANTE" piccolo
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.setFill(javafx.scene.paint.Color.rgb(180, 180, 190));
        gc.fillText("IMMAGINE", width/2 - 30, height - 30);
        gc.fillText("MANCANTE", width/2 - 30, height - 18);
        
        // Converti canvas in Image
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }
    
    /**
     * Stampa report delle immagini caricate/mancanti
     */
    public static void stampaReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("REPORT CARICAMENTO IMMAGINI");
        System.out.println("=".repeat(60));
        System.out.println("Caricate: " + immaginiCaricate.size());
        System.out.println("Mancanti: " + immaginiMancanti.size());
        
        if (!immaginiMancanti.isEmpty()) {
            System.out.println("Immagini non trovate:");
            for (String path : immaginiMancanti) {
                System.out.println("   - " + path);
            }
        }
        System.out.println("=".repeat(60));
    }
}