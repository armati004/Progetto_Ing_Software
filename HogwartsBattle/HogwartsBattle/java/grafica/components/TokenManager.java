package grafica.components;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * TokenManager - Gestisce la visualizzazione dei segnalini sul tabellone.
 * 
 * Tipi di segnalini supportati:
 * - Vita (cuori)
 * - Attacco (fulmini)
 * - Influenza/Gettoni (monete)
 * - Marchio Nero (teschi)
 * - Horcrux (segnalini horcrux)
 * - Vita Horcrux
 * - Attacco Horcrux
 * 
 * Ogni segnalino può essere visualizzato come:
 * - Immagine (se disponibile)
 * - Fallback grafico (cerchio colorato con icona)
 */
public class TokenManager {
    
    // Dimensioni segnalini
    public static final double TOKEN_SIZE_SMALL = 25;
    public static final double TOKEN_SIZE_NORMAL = 35;
    public static final double TOKEN_SIZE_LARGE = 50;
    
    // Cache immagini segnalini
    private static Map<String, Image> tokenImages = new HashMap<>();
    private static boolean imagesLoaded = false;
    
    /**
     * Enum per i tipi di segnalini
     */
    public enum TokenType {
        VITA("Vita.png", "❤️", Color.RED),
        ATTACCO("Attacco.png", "⚔️", Color.ORANGE),
        INFLUENZA("Gettone.png", "⚡", Color.GOLD),
        MARCHIO_NERO("Marchio_nero.png", "💀", Color.DARKRED),
        HORCRUX("Gettone_horcrux.png", "🔮", Color.PURPLE),
        VITA_HORCRUX("Vita_horcrux.png", "❤️", Color.DARKRED),
        ATTACCO_HORCRUX("Attacco_horcrux.png", "⚔️", Color.DARKRED),
        CARTA_HORCRUX("Carta_horcrux.png", "📜", Color.PURPLE);
        
        private final String imageFile;
        private final String icon;
        private final Color color;
        
        TokenType(String imageFile, String icon, Color color) {
            this.imageFile = imageFile;
            this.icon = icon;
            this.color = color;
        }
        
        public String getImageFile() { return imageFile; }
        public String getIcon() { return icon; }
        public Color getColor() { return color; }
    }
    
    /**
     * Carica tutte le immagini dei segnalini
     */
    public static void caricaImmagini() {
        if (imagesLoaded) return;
        
        System.out.println("🎨 Caricamento immagini segnalini...");
        
        for (TokenType type : TokenType.values()) {
            try {
                String path = "Immagini_carte/" + type.getImageFile();
                Image image = caricaImmagine(path);
                tokenImages.put(type.name(), image);
                System.out.println("  ✓ " + type.name());
            } catch (Exception e) {
                System.out.println("  ⚠ " + type.name() + " (usando fallback)");
            }
        }
        
        imagesLoaded = true;
        System.out.println("✓ Segnalini caricati: " + tokenImages.size() + "/" + TokenType.values().length);
    }
    
    /**
     * Crea un singolo segnalino
     */
    public static StackPane creaSegnalino(TokenType type, double size) {
        StackPane token = new StackPane();
        token.setPrefSize(size, size);
        token.setMaxSize(size, size);
        
        // Prova a usare l'immagine
        if (tokenImages.containsKey(type.name())) {
            ImageView imageView = new ImageView(tokenImages.get(type.name()));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            token.getChildren().add(imageView);
        } else {
            // Fallback: cerchio con icona
            Circle circle = new Circle(size / 2);
            circle.setFill(type.getColor());
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            
            Text icon = new Text(type.getIcon());
            icon.setFont(Font.font(size * 0.6));
            
            token.getChildren().addAll(circle, icon);
        }
        
        return token;
    }
    
    /**
     * Crea un gruppo di segnalini con contatore
     */
    public static HBox creaGruppoSegnalini(TokenType type, int quantita, double size) {
        HBox gruppo = new HBox(5);
        gruppo.setAlignment(Pos.CENTER);
        
        // Segnalino
        StackPane token = creaSegnalino(type, size);
        gruppo.getChildren().add(token);
        
        // Numero (se > 1)
        if (quantita > 1) {
            Text count = new Text("×" + quantita);
            count.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.5));
            count.setFill(Color.WHITE);
            count.setStroke(Color.BLACK);
            count.setStrokeWidth(1);
            gruppo.getChildren().add(count);
        }
        
        return gruppo;
    }
    
    /**
     * Crea un display con segnalino e valore (es. 5/10 vite)
     */
    public static HBox creaDisplaySegnalino(TokenType type, int valore, int valoreMassimo, double size) {
        HBox display = new HBox(8);
        display.setAlignment(Pos.CENTER);
        display.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 5;");
        
        // Segnalino
        StackPane token = creaSegnalino(type, size);
        display.getChildren().add(token);
        
        // Valore
        String valueText = valoreMassimo > 0 ? 
                          valore + "/" + valoreMassimo : 
                          String.valueOf(valore);
        
        Text value = new Text(valueText);
        value.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.6));
        value.setFill(getColorForValue(valore, valoreMassimo, type));
        
        display.getChildren().add(value);
        
        return display;
    }
    
    /**
     * Crea una barra di segnalini (es. 5 cuori per 5 vite)
     */
    public static HBox creaBarraSegnalini(TokenType type, int quantita, int quantitaMassima, double size) {
        HBox barra = new HBox(3);
        barra.setAlignment(Pos.CENTER);
        
        // Mostra fino a max 10 segnalini
        int toShow = Math.min(quantitaMassima, 10);
        
        for (int i = 0; i < toShow; i++) {
            StackPane token = creaSegnalino(type, size);
            
            // Opacità ridotta per i segnalini "vuoti"
            if (i >= quantita) {
                token.setOpacity(0.3);
            }
            
            barra.getChildren().add(token);
        }
        
        // Se ci sono più di 10, mostra il contatore
        if (quantitaMassima > 10) {
            Text overflow = new Text("(+" + (quantitaMassima - 10) + ")");
            overflow.setFont(Font.font("Arial", FontWeight.NORMAL, size * 0.5));
            overflow.setFill(Color.LIGHTGRAY);
            barra.getChildren().add(overflow);
        }
        
        return barra;
    }
    
    /**
     * Crea un indicatore di marchi neri per il luogo
     */
    public static HBox creaIndicatoreMarchiNeri(int marchiAttuali, int marchiMassimi) {
        HBox indicatore = new HBox(10);
        indicatore.setAlignment(Pos.CENTER);
        indicatore.setStyle("-fx-background-color: rgba(100, 0, 0, 0.7); " +
                           "-fx-background-radius: 10; " +
                           "-fx-padding: 8;");
        
        // Icona
        Text label = new Text("💀 MARCHI NERI:");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setFill(Color.WHITE);
        indicatore.getChildren().add(label);
        
        // Segnalini
        for (int i = 0; i < marchiMassimi; i++) {
            StackPane token = creaSegnalino(TokenType.MARCHIO_NERO, TOKEN_SIZE_NORMAL);
            
            if (i >= marchiAttuali) {
                token.setOpacity(0.3);
            }
            
            indicatore.getChildren().add(token);
        }
        
        // Contatore
        Text count = new Text(marchiAttuali + "/" + marchiMassimi);
        count.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        count.setFill(marchiAttuali >= marchiMassimi ? Color.RED : Color.ORANGE);
        indicatore.getChildren().add(count);
        
        return indicatore;
    }
    
    /**
     * Crea una sezione di statistiche del giocatore con segnalini
     */
    public static HBox creaStatisticheGiocatore(int vita, int attacco, int influenza) {
        HBox stats = new HBox(15);
        stats.setAlignment(Pos.CENTER);
        stats.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                      "-fx-background-radius: 10; " +
                      "-fx-padding: 10;");
        
        // Vita
        HBox vitaBox = creaDisplaySegnalino(TokenType.VITA, vita, -1, TOKEN_SIZE_NORMAL);
        
        // Attacco
        HBox attaccoBox = creaDisplaySegnalino(TokenType.ATTACCO, attacco, -1, TOKEN_SIZE_NORMAL);
        
        // Influenza
        HBox influenzaBox = creaDisplaySegnalino(TokenType.INFLUENZA, influenza, -1, TOKEN_SIZE_NORMAL);
        
        stats.getChildren().addAll(vitaBox, attaccoBox, influenzaBox);
        
        return stats;
    }
    
    /**
     * Ottiene il colore appropriato per un valore
     */
    private static Color getColorForValue(int valore, int valoreMassimo, TokenType type) {
        if (valoreMassimo <= 0) {
            return Color.WHITE;
        }
        
        double percentuale = (double) valore / valoreMassimo;
        
        if (type == TokenType.VITA || type == TokenType.VITA_HORCRUX) {
            if (percentuale > 0.6) return Color.LIGHTGREEN;
            if (percentuale > 0.3) return Color.ORANGE;
            return Color.RED;
        }
        
        return Color.WHITE;
    }
    
    /**
     * Carica un'immagine provando diversi metodi
     */
    private static Image caricaImmagine(String path) throws Exception {
        String cleanPath = path.replace("../", "").replace("\\", "/");
        
        // Metodo 1: Risorsa dal classpath
        try {
            var stream = TokenManager.class.getClassLoader().getResourceAsStream(cleanPath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            // Ignora
        }
        
        // Metodo 2: File diretto
        try {
            File file = new File(cleanPath);
            if (file.exists()) {
                return new Image(new FileInputStream(file));
            }
        } catch (Exception e) {
            // Ignora
        }
        
        // Metodo 3: Path dal progetto
        try {
            File projectFile = new File("/mnt/project/" + cleanPath);
            if (projectFile.exists()) {
                return new Image(new FileInputStream(projectFile));
            }
        } catch (Exception e) {
            // Ignora
        }
        
        throw new Exception("Impossibile caricare l'immagine: " + path);
    }
}