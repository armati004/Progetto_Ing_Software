package grafica.components;

import carte.Carta;
import gioco.Giocatore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Pannello che visualizza lo stato completo di un giocatore.
 * Include: eroe, salute, attacco, influenza, mano, mazzo, scarti.
 * Utilizza i segnalini grafici per una visualizzazione fedele al gioco da tavolo.
 */
public class PlayerPanel extends VBox {
    
    private static final double PANEL_WIDTH = 300;
    private static final double PANEL_HEIGHT = 450;
    
    private final Giocatore giocatore;
    private boolean isActive;
    
    // Componenti UI
    private Text heroNameText;
    private HBox healthBox;
    private HBox attackBox;
    private HBox influenceBox;
    private HBox cardCountBox;
    private VBox handPreview;
    
    // Immagini dei segnalini
    private Map<String, Image> tokenImages;
    
    // Effetti
    private DropShadow shadowEffect;
    private DropShadow glowEffect;
    
    /**
     * Costruttore principale
     */
    public PlayerPanel(Giocatore giocatore, boolean isActive) {
        this.giocatore = giocatore;
        this.isActive = isActive;
        
        caricaSegnalini();
        inizializzaEffetti();
        costruisciUI();
    }
    
    /**
     * Costruttore semplificato
     */
    public PlayerPanel(Giocatore giocatore) {
        this(giocatore, false);
    }
    
    /**
     * Carica le immagini dei segnalini
     */
    private void caricaSegnalini() {
        tokenImages = new HashMap<>();
        
        String[] tokenFiles = {
            "Vita.png",
            "Attacco.png", 
            "Gettone.png",
            "Carta_horcrux.png",
            "Marchio_nero.png"
        };
        
        for (String fileName : tokenFiles) {
            try {
                Image img = caricaImmagine("Immagini_carte/" + fileName);
                String key = fileName.replace(".png", "").toLowerCase();
                tokenImages.put(key, img);
                System.out.println("✓ Caricato segnalino: " + fileName);
            } catch (Exception e) {
                System.err.println("✗ Impossibile caricare: " + fileName);
            }
        }
    }
    
    /**
     * Carica un'immagine provando diversi metodi
     */
    private Image caricaImmagine(String path) throws Exception {
        String cleanPath = path.replace("../", "").replace("\\", "/");
        
        // Prova come risorsa
        try {
            var stream = getClass().getClassLoader().getResourceAsStream(cleanPath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception ignored) {}
        
        // Prova come file
        try {
            File file = new File(cleanPath);
            if (file.exists()) {
                return new Image(new FileInputStream(file));
            }
        } catch (Exception ignored) {}
        
        // Prova in /mnt/project
        try {
            File projectFile = new File("/mnt/project/" + cleanPath);
            if (projectFile.exists()) {
                return new Image(new FileInputStream(projectFile));
            }
        } catch (Exception ignored) {}
        
        throw new Exception("Impossibile caricare: " + path);
    }
    
    /**
     * Inizializza gli effetti visivi
     */
    private void inizializzaEffetti() {
        shadowEffect = new DropShadow();
        shadowEffect.setRadius(8);
        shadowEffect.setOffsetX(2);
        shadowEffect.setOffsetY(2);
        shadowEffect.setColor(Color.color(0, 0, 0, 0.5));
        
        glowEffect = new DropShadow();
        glowEffect.setRadius(15);
        glowEffect.setColor(Color.GOLD);
        glowEffect.setSpread(0.4);
    }
    
    /**
     * Costruisce l'interfaccia utente
     */
    private void costruisciUI() {
        this.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.TOP_CENTER);
        
        // Background
        setBackground(isActive);
        this.setEffect(shadowEffect);
        
        // Nome eroe
        creaHeaderEroe();
        
        // Stats con segnalini
        creaStatistiche();
        
        // Preview mano (solo prime 3 carte)
        creaPreviewMano();
        
        // Info mazzo e scarti
        creaInfoMazzi();
    }
    
    /**
     * Imposta lo sfondo del pannello
     */
    private void setBackground(boolean active) {
        BackgroundFill fill;
        
        if (active) {
            // Sfondo dorato per giocatore attivo
            fill = new BackgroundFill(
                Color.rgb(80, 60, 20, 0.9),
                new CornerRadii(15),
                Insets.EMPTY
            );
        } else {
            // Sfondo scuro per giocatori non attivi
            fill = new BackgroundFill(
                Color.rgb(30, 30, 40, 0.9),
                new CornerRadii(15),
                Insets.EMPTY
            );
        }
        
        this.setBackground(new Background(fill));
        
        // Bordo
        this.setBorder(new Border(new BorderStroke(
            isActive ? Color.GOLD : Color.DARKGRAY,
            BorderStrokeStyle.SOLID,
            new CornerRadii(15),
            new BorderWidths(isActive ? 3 : 2)
        )));
    }
    
    /**
     * Crea l'header con il nome dell'eroe
     */
    private void creaHeaderEroe() {
        heroNameText = new Text(giocatore.getEroe().getNome());
        heroNameText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 20));
        heroNameText.setFill(isActive ? Color.GOLD : Color.LIGHTGRAY);
        heroNameText.setTextAlignment(TextAlignment.CENTER);
        heroNameText.setWrappingWidth(PANEL_WIDTH - 30);
        
        if (isActive) {
            Text activeIndicator = new Text("⭐ ATTIVO ⭐");
            activeIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            activeIndicator.setFill(Color.YELLOW);
            
            VBox headerBox = new VBox(5);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.getChildren().addAll(heroNameText, activeIndicator);
            this.getChildren().add(headerBox);
        } else {
            this.getChildren().add(heroNameText);
        }
    }
    
    /**
     * Crea la sezione statistiche con segnalini
     */
    private void creaStatistiche() {
        VBox statsBox = new VBox(8);
        statsBox.setAlignment(Pos.CENTER);
        
        // Salute
        healthBox = creaStatBox("vita", giocatore.getSalute(), giocatore.getSaluteMax(), Color.RED);
        
        // Attacco
        attackBox = creaStatBox("attacco", giocatore.getAttacco(), -1, Color.ORANGERED);
        
        // Influenza (monete/gettoni)
        influenceBox = creaStatBox("gettone", giocatore.getGettone(), -1, Color.GOLD);
        
        statsBox.getChildren().addAll(healthBox, attackBox, influenceBox);
        this.getChildren().add(statsBox);
    }
    
    /**
     * Crea una riga di statistica con segnalino
     */
    private HBox creaStatBox(String tokenKey, int value, int maxValue, Color textColor) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5));
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-background-radius: 8;");
        
        // Segnalino (immagine o cerchio colorato)
        if (tokenImages.containsKey(tokenKey)) {
            ImageView tokenView = new ImageView(tokenImages.get(tokenKey));
            tokenView.setFitWidth(35);
            tokenView.setFitHeight(35);
            tokenView.setPreserveRatio(true);
            box.getChildren().add(tokenView);
        } else {
            // Fallback: cerchio colorato
            Circle token = new Circle(15);
            token.setFill(textColor);
            token.setStroke(Color.WHITE);
            token.setStrokeWidth(2);
            box.getChildren().add(token);
        }
        
        // Valore
        String valueText = maxValue > 0 ? value + "/" + maxValue : String.valueOf(value);
        Text statText = new Text(valueText);
        statText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statText.setFill(textColor);
        
        box.getChildren().add(statText);
        
        // Barra progressiva per la salute
        if (maxValue > 0) {
            ProgressBar healthBar = new ProgressBar();
            healthBar.setProgress((double) value / maxValue);
            healthBar.setPrefWidth(80);
            healthBar.setStyle(
                "-fx-accent: " + toRgbString(textColor) + ";"
            );
            box.getChildren().add(healthBar);
        }
        
        return box;
    }
    
    /**
     * Converte un Color in stringa RGB
     */
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255)
        );
    }
    
    /**
     * Crea una preview della mano (prime 3 carte)
     */
    private void creaPreviewMano() {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        
        Text title = new Text("🃏 Mano (" + giocatore.getMano().size() + ")");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setFill(Color.LIGHTBLUE);
        
        HBox cardsBox = new HBox(5);
        cardsBox.setAlignment(Pos.CENTER);
        
        // Mostra max 3 carte
        int cardsToShow = Math.min(3, giocatore.getMano().size());
        for (int i = 0; i < cardsToShow; i++) {
            Carta carta = giocatore.getMano().get(i);
            
            // Mini carta
            CardView miniCard = new CardView(carta, CardView.CardSize.SMALL);
            miniCard.setScaleX(0.6);
            miniCard.setScaleY(0.6);
            
            cardsBox.getChildren().add(miniCard);
        }
        
        // Se ci sono più di 3 carte, mostra "+X"
        if (giocatore.getMano().size() > 3) {
            Text moreText = new Text("+" + (giocatore.getMano().size() - 3));
            moreText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            moreText.setFill(Color.WHITE);
            cardsBox.getChildren().add(moreText);
        }
        
        container.getChildren().addAll(title, cardsBox);
        this.getChildren().add(container);
        
        this.handPreview = container;
    }
    
    /**
     * Crea le info su mazzo e scarti
     */
    private void creaInfoMazzi() {
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Mazzo
        VBox deckBox = creaInfoBox("📚", "Mazzo", giocatore.getMazzo().size(), Color.LIGHTGREEN);
        
        // Scarti
        VBox discardBox = creaInfoBox("🗑", "Scarti", giocatore.getScarti().size(), Color.LIGHTCORAL);
        
        infoBox.getChildren().addAll(deckBox, discardBox);
        this.getChildren().add(infoBox);
        
        this.cardCountBox = infoBox;
    }
    
    /**
     * Crea una info box con icona, label e numero
     */
    private VBox creaInfoBox(String icon, String label, int count, Color color) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); " +
                    "-fx-background-radius: 5; " +
                    "-fx-padding: 5;");
        box.setPrefWidth(100);
        
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(20));
        
        Text labelText = new Text(label);
        labelText.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        labelText.setFill(Color.LIGHTGRAY);
        
        Text countText = new Text(String.valueOf(count));
        countText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        countText.setFill(color);
        
        box.getChildren().addAll(iconText, labelText, countText);
        return box;
    }
    
    /**
     * Aggiorna tutte le statistiche del pannello
     */
    public void aggiornaStatistiche() {
        // Aggiorna statistiche
        aggiornaStatBox(healthBox, "vita", giocatore.getSalute(), giocatore.getSaluteMax(), Color.RED);
        aggiornaStatBox(attackBox, "attacco", giocatore.getAttacco(), -1, Color.ORANGERED);
        aggiornaStatBox(influenceBox, "gettone", giocatore.getGettone(), -1, Color.GOLD);
        
        // Aggiorna preview mano
        this.getChildren().remove(handPreview);
        creaPreviewMano();
        int insertIndex = this.getChildren().indexOf(cardCountBox);
        this.getChildren().add(insertIndex, handPreview);
        
        // Aggiorna info mazzi
        VBox deckBox = (VBox) cardCountBox.getChildren().get(0);
        ((Text) deckBox.getChildren().get(2)).setText(String.valueOf(giocatore.getMazzo().size()));
        
        VBox discardBox = (VBox) cardCountBox.getChildren().get(1);
        ((Text) discardBox.getChildren().get(2)).setText(String.valueOf(giocatore.getScarti().size()));
    }
    
    /**
     * Aggiorna una stat box
     */
    private void aggiornaStatBox(HBox box, String tokenKey, int value, int maxValue, Color textColor) {
        // Il secondo elemento è il testo del valore
        Text statText = (Text) box.getChildren().get(1);
        String valueText = maxValue > 0 ? value + "/" + maxValue : String.valueOf(value);
        statText.setText(valueText);
        
        // Se c'è una progress bar, aggiornala
        if (box.getChildren().size() > 2 && box.getChildren().get(2) instanceof ProgressBar) {
            ProgressBar bar = (ProgressBar) box.getChildren().get(2);
            bar.setProgress((double) value / maxValue);
        }
    }
    
    /**
     * Imposta se questo giocatore è attivo
     */
    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            setBackground(active);
            
            // Aggiorna il testo del nome
            heroNameText.setFill(active ? Color.GOLD : Color.LIGHTGRAY);
            
            // Aggiorna l'effetto
            if (active) {
                this.setEffect(glowEffect);
            } else {
                this.setEffect(shadowEffect);
            }
            
            // Ricrea l'header per mostrare/nascondere l'indicatore ATTIVO
            VBox parent = (VBox) heroNameText.getParent();
            if (parent != null) {
                int index = this.getChildren().indexOf(parent);
                this.getChildren().remove(parent);
            } else {
                int index = this.getChildren().indexOf(heroNameText);
                this.getChildren().remove(heroNameText);
            }
            creaHeaderEroe();
            
            // Reinserisci l'header all'inizio
            if (this.getChildren().size() > 0) {
                this.getChildren().add(0, this.getChildren().remove(this.getChildren().size() - 1));
            }
        }
    }
    
    /**
     * Restituisce il giocatore associato a questo pannello
     */
    public Giocatore getGiocatore() {
        return giocatore;
    }
    
    /**
     * Verifica se questo giocatore è attivo
     */
    public boolean isActive() {
        return isActive;
    }
}