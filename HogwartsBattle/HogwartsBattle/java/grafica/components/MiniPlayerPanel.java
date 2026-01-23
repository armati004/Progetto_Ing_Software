package grafica.components;

import gioco.Giocatore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;

/**
 * MiniPlayerPanel - Pannello compatto per mostrare le statistiche essenziali
 * di un giocatore nella barra superiore.
 * 
 * Mostra:
 * - Avatar/Icona eroe
 * - Nome
 * - Salute
 * - Attacco
 * - Influenza
 * - Numero carte in mano
 */
public class MiniPlayerPanel extends HBox {
    
    private static final double PANEL_WIDTH = 280;
    private static final double PANEL_HEIGHT = 80;
    private static final double AVATAR_SIZE = 60;
    
    private final Giocatore giocatore;
    
    // Componenti
    private ImageView avatarView;
    private VBox statsBox;
    
    /**
     * Costruttore
     */
    public MiniPlayerPanel(Giocatore giocatore) {
        this.giocatore = giocatore;
        
        costruisciUI();
    }
    
    /**
     * Costruisce l'interfaccia del mini pannello
     */
    private void costruisciUI() {
        this.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setPadding(new Insets(8));
        
        // Sfondo
        this.setStyle("-fx-background-color: rgba(30, 30, 50, 0.8); " +
                     "-fx-background-radius: 8; " +
                     "-fx-border-color: #8b7355; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 8;");
        
        // Effetto ombra
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        this.setEffect(shadow);
        
        // Avatar
        creaAvatar();
        
        // Statistiche
        creaStats();
    }
    
    /**
     * Crea l'avatar del giocatore
     */
    private void creaAvatar() {
        StackPane avatarContainer = new StackPane();
        avatarContainer.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
        
        try {
            // Prova a caricare l'immagine dell'eroe
            String imagePath = giocatore.getEroe().getPathImmagine();
            Image heroImage = caricaImmagine(imagePath);
            
            avatarView = new ImageView(heroImage);
            avatarView.setFitWidth(AVATAR_SIZE);
            avatarView.setFitHeight(AVATAR_SIZE);
            avatarView.setPreserveRatio(true);
            
            // Clip circolare
            Circle clip = new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2);
            avatarView.setClip(clip);
            
            avatarContainer.getChildren().add(avatarView);
            
        } catch (Exception e) {
            // Fallback: cerchio colorato con iniziale
            Circle circle = new Circle(AVATAR_SIZE / 2);
            circle.setFill(getColorForHero(giocatore.getEroe().getNome()));
            circle.setStroke(Color.GOLD);
            circle.setStrokeWidth(2);
            
            Text initial = new Text(giocatore.getEroe().getNome().substring(0, 1));
            initial.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            initial.setFill(Color.WHITE);
            
            avatarContainer.getChildren().addAll(circle, initial);
        }
        
        this.getChildren().add(avatarContainer);
    }
    
    /**
     * Crea la sezione delle statistiche
     */
    private void creaStats() {
        statsBox = new VBox(3);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Nome eroe
        Text nameText = new Text(giocatore.getEroe().getNome());
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameText.setFill(Color.GOLD);
        
        // Statistiche in riga
        HBox statsRow1 = new HBox(12);
        statsRow1.setAlignment(Pos.CENTER_LEFT);
        
        // Salute
        HBox healthBox = creaStat("❤️", String.valueOf(giocatore.getSalute()), Color.RED);
        
        // Attacco
        HBox attackBox = creaStat("⚔️", String.valueOf(giocatore.getAttacco()), Color.ORANGE);
        
        // Influenza
        HBox influenceBox = creaStat("⚡", String.valueOf(giocatore.getGettone()), Color.YELLOW);
        
        statsRow1.getChildren().addAll(healthBox, attackBox, influenceBox);
        
        // Seconda riga: carte
        HBox statsRow2 = new HBox(12);
        statsRow2.setAlignment(Pos.CENTER_LEFT);
        
        // Mano
        HBox handBox = creaStat("✋", String.valueOf(giocatore.getMano().size()), Color.LIGHTBLUE);
        
        // Mazzo
        HBox deckBox = creaStat("📚", String.valueOf(giocatore.getMazzo().size()), Color.LIGHTGREEN);
        
        statsRow2.getChildren().addAll(handBox, deckBox);
        
        statsBox.getChildren().addAll(nameText, statsRow1, statsRow2);
        
        this.getChildren().add(statsBox);
    }
    
    /**
     * Crea un elemento statistico con icona e valore
     */
    private HBox creaStat(String icon, String value, Color color) {
        HBox box = new HBox(3);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(12));
        
        Text valueText = new Text(value);
        valueText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        valueText.setFill(color);
        
        box.getChildren().addAll(iconText, valueText);
        
        return box;
    }
    
    /**
     * Ottiene un colore basato sul nome dell'eroe
     */
    private Color getColorForHero(String heroName) {
        switch (heroName) {
            case "Harry Potter":
                return Color.rgb(220, 20, 60); // Rosso Grifondoro
            case "Hermione Granger":
                return Color.rgb(174, 0, 1); // Rosso scuro
            case "Ron Weasley":
                return Color.rgb(255, 140, 0); // Arancione
            case "Neville Longbottom":
                return Color.rgb(139, 69, 19); // Marrone
            default:
                return Color.rgb(128, 128, 128); // Grigio default
        }
    }
    
    /**
     * Carica un'immagine provando diversi metodi
     */
    private Image caricaImmagine(String path) throws Exception {
        String cleanPath = path.replace("../", "").replace("\\", "/");
        
        // Metodo 1: Risorsa dal classpath
        try {
            var stream = getClass().getClassLoader().getResourceAsStream(cleanPath);
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
    
    /**
     * Aggiorna le statistiche visualizzate
     */
    public void aggiorna() {
        // Ricostruisci la sezione stats
        this.getChildren().remove(statsBox);
        creaStats();
    }
}