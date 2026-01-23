package grafica.components;

import carte.Carta;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;

/**
 * Componente grafico per visualizzare una carta del gioco.
 * Le carte hanno immagini complete che includono già nome, descrizione, effetti, costo, ecc.
 * Gestisce solo la visualizzazione dell'immagine e gli effetti interattivi.
 */
public class CardView extends StackPane {
    
    // Dimensioni standard delle carte (proporzioni circa 2:3 come nelle immagini reali)
    public static final double CARD_WIDTH = 180;
    public static final double CARD_HEIGHT = 270;
    
    // Dimensioni ridotte per visualizzazioni compatte (mercato, mano con molte carte)
    public static final double CARD_WIDTH_SMALL = 120;
    public static final double CARD_HEIGHT_SMALL = 180;
    
    // Dimensioni grandi per visualizzazione dettagliata (popup, ingrandimento)
    public static final double CARD_WIDTH_LARGE = 300;
    public static final double CARD_HEIGHT_LARGE = 450;
    
    // Riferimento alla carta
    private final Carta carta;
    
    // Componenti grafici
    private ImageView imageView;
    private Rectangle cardBack;
    private boolean isFaceUp;
    private CardSize currentSize;
    
    // Effetti visivi
    private DropShadow shadowEffect;
    private Glow glowEffect;
    
    // Callback per eventi
    private Runnable onClickCallback;
    private Runnable onHoverCallback;
    
    private boolean isHovered;
    private boolean isSelected;
    private DropShadow hoverShadow;
    
    /**
     * Enum per definire le dimensioni della carta
     */
    public enum CardSize {
        SMALL(CARD_WIDTH_SMALL, CARD_HEIGHT_SMALL),
        NORMAL(CARD_WIDTH, CARD_HEIGHT),
        LARGE(CARD_WIDTH_LARGE, CARD_HEIGHT_LARGE);
        
        private final double width;
        private final double height;
        
        CardSize(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
    }
    
    /**
     * Costruttore principale
     */
    public CardView(Carta carta, boolean isFaceUp, CardSize size) {
        this.carta = carta;
        this.isFaceUp = isFaceUp;
        this.currentSize = size;
        
        inizializzaEffetti();
        inizializzaGrafica();
        aggiungiInterazioni();
    }
    
    /**
     * Costruttore semplificato
     */
    public CardView(Carta carta) {
        this(carta, true, CardSize.NORMAL);
    }
    
    /**
     * Costruttore con dimensioni personalizzate
     */
    public CardView(Carta carta, CardSize size) {
        this(carta, true, size);
    }
    
    /**
     * Inizializza gli effetti visivi
     */
    private void inizializzaEffetti() {
    	shadowEffect = new DropShadow();
        shadowEffect.setRadius(10);
        shadowEffect.setOffsetX(3);
        shadowEffect.setOffsetY(3);
        shadowEffect.setColor(Color.color(0, 0, 0, 0.6));
        
        glowEffect = new Glow();
        glowEffect.setLevel(0.6);
        
        // NUOVO: Shadow per hover
        hoverShadow = new DropShadow();
        hoverShadow.setRadius(20);
        hoverShadow.setOffsetX(0);
        hoverShadow.setOffsetY(0);
        hoverShadow.setColor(Color.GOLD);
        
        isHovered = false;
        isSelected = false;
    }
    
    /**
     * Inizializza la grafica della carta
     */
    private void inizializzaGrafica() {
        this.setPrefSize(currentSize.getWidth(), currentSize.getHeight());
        this.setMaxSize(currentSize.getWidth(), currentSize.getHeight());
        this.setMinSize(currentSize.getWidth(), currentSize.getHeight());
        
        this.setEffect(shadowEffect);
        
        if (isFaceUp) {
            creaFacciaCarta();
        } else {
            creaRetroCarta();
        }
    }
    
    /**
     * Crea la visualizzazione della faccia della carta
     * Le immagini sono complete, quindi mostriamo solo l'immagine
     */
    private void creaFacciaCarta() {
        String imagePath = carta.getPathImmagine();
        
        try {
            Image image = caricaImmagine(imagePath);
            
            imageView = new ImageView(image);
            imageView.setFitWidth(currentSize.getWidth());
            imageView.setFitHeight(currentSize.getHeight());
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            // Bordo arrotondato
            Rectangle clip = new Rectangle(currentSize.getWidth(), currentSize.getHeight());
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);
            
            this.getChildren().add(imageView);
            
        } catch (Exception e) {
            System.err.println("Impossibile caricare l'immagine: " + imagePath);
            e.printStackTrace();
            creaCartaPlaceholder();
        }
    }
    
    /**
     * Carica l'immagine dal classpath gestendo i percorsi relativi del JSON.
     * @param rawPath Il percorso grezzo letto dal JSON (es. "../Immagini_carte/...")
     * @return L'oggetto Image caricato
     * @throws IllegalArgumentException se l'immagine non viene trovata
     */
    private Image caricaImmagine(String rawPath) {
        // Controllo sicurezza
        if (rawPath == null || rawPath.isEmpty()) {
            throw new IllegalArgumentException("Path immagine vuoto o nullo");
        }

        // 1. PULIZIA DEL PERCORSO (Cruciale per i tuoi JSON)
        // Trasforma "../Immagini_carte/..." in "/Immagini_carte/..."
        String cleanPath = rawPath.replace("../", "/");
        
        // Assicuriamoci che inizi con "/" (necessario per getResource)
        if (!cleanPath.startsWith("/")) {
            cleanPath = "/" + cleanPath;
        }

        // 2. DEBUG (Ti aiuta a vedere cosa sta cercando il programma)
        System.out.println("Cerco immagine in: " + cleanPath); 

        // 3. CARICAMENTO
        // getClass().getResource cerca DENTRO la cartella 'src/main/resources'
        java.net.URL url = getClass().getResource(cleanPath);

        // 4. VERIFICA
        if (url == null) {
            // Stampa un errore chiaro in console rossa
            System.err.println("[ERRORE] File non trovato: " + cleanPath);
            System.err.println("         Verifica che la cartella 'Immagini_carte' sia dentro 'src/main/resources'");
            throw new IllegalArgumentException("Risorsa non trovata: " + cleanPath);
        }

        // Restituisci l'immagine
        return new Image(url.toExternalForm());
    }
    
     /**
     * Crea una carta placeholder quando l'immagine non è disponibile
     */
    private void creaCartaPlaceholder() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(currentSize.getWidth(), currentSize.getHeight());
        container.setMaxSize(currentSize.getWidth(), currentSize.getHeight());
        
        Rectangle background = new Rectangle(currentSize.getWidth(), currentSize.getHeight());
        background.setFill(getColorForCardClass(carta.getClasse()));
        background.setStroke(Color.DARKGOLDENROD);
        background.setStrokeWidth(3);
        background.setArcWidth(15);
        background.setArcHeight(15);
        
        Text nameText = new Text(carta.getNome());
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, currentSize == CardSize.SMALL ? 11 : 15));
        nameText.setFill(Color.WHITE);
        nameText.setWrappingWidth(currentSize.getWidth() - 20);
        nameText.setTextAlignment(TextAlignment.CENTER);
        
        Text typeText = new Text(carta.getClasse());
        typeText.setFont(Font.font("Arial", FontWeight.NORMAL, currentSize == CardSize.SMALL ? 9 : 12));
        typeText.setFill(Color.LIGHTGOLDENRODYELLOW);
        
        VBox textBox = new VBox(5);
        textBox.setAlignment(Pos.CENTER);
        textBox.getChildren().addAll(nameText, typeText);
        
        if (carta.getCosto() > 0) {
            Text costText = new Text("⚡ " + carta.getCosto());
            costText.setFont(Font.font("Arial", FontWeight.BOLD, currentSize == CardSize.SMALL ? 12 : 16));
            costText.setFill(Color.GOLD);
            textBox.getChildren().add(costText);
        }
        
        StackPane stack = new StackPane();
        stack.getChildren().addAll(background, textBox);
        
        this.getChildren().add(stack);
    }
    
    /**
     * Crea il retro della carta
     * Ogni tipo di carta ha un retro diverso
     */
    private void creaRetroCarta() {
        Rectangle back = new Rectangle(currentSize.getWidth(), currentSize.getHeight());
        back.setArcWidth(15);
        back.setArcHeight(15);
        
        // Colore base in base al tipo di carta
        Color baseColor = getColorForCardClass(carta.getClasse());
        back.setFill(baseColor.darker());
        back.setStroke(Color.GOLD);
        back.setStrokeWidth(4);
        
        // Simbolo decorativo in base al tipo
        String symbol = getSymbolForCardClass(carta.getClasse());
        Text symbolText = new Text(symbol);
        symbolText.setFont(Font.font("Arial", currentSize == CardSize.SMALL ? 30 : 50));
        symbolText.setFill(Color.GOLD);
        
        StackPane stack = new StackPane();
        stack.getChildren().addAll(back, symbolText);
        
        this.getChildren().add(stack);
        this.cardBack = back;
    }
    
    /**
     * Aggiunge le interazioni con il mouse
     */
    private void aggiungiInterazioni() {
    	// Click
        this.setOnMouseClicked(event -> {
            if (onClickCallback != null) {
                onClickCallback.run();
            }
        });
        
        // Hover
        this.setOnMouseEntered(event -> {
            applicaEffettoHover();
            if (onHoverCallback != null) {
                onHoverCallback.run();
            }
        });
        
        this.setOnMouseExited(event -> {
            rimuoviEffettoHover();
        });
        
        // Cursore
        this.setCursor(javafx.scene.Cursor.HAND);
    }
    
    public void applicaEffettoHover() {
        if (isHovered) return;
        
        isHovered = true;
        
        // Cambia ombra
        this.setEffect(hoverShadow);
        
        // Animazione scale up
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), this);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.play();
        
        // Cursore pointer
        this.setCursor(javafx.scene.Cursor.HAND);
    }
    
    public void rimuoviEffettoHover() {
        if (!isHovered) return;
        
        isHovered = false;
        
        // Ripristina ombra normale (o selezionata)
        if (isSelected) {
            applicaEffettoSelezione();
        } else {
            this.setEffect(shadowEffect);
        }
        
        // Animazione scale down
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), this);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
        
        // Cursore default
        this.setCursor(javafx.scene.Cursor.DEFAULT);
    }
    
    public void applicaEffettoSelezione() {
        isSelected = true;
        
        DropShadow selectShadow = new DropShadow();
        selectShadow.setRadius(15);
        selectShadow.setOffsetX(0);
        selectShadow.setOffsetY(0);
        selectShadow.setColor(Color.CYAN);
        
        this.setEffect(selectShadow);
        
        // Animazione pulsante
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), this);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }
    
    public void rimuoviEffettoSelezione() {
        isSelected = false;
        this.setEffect(shadowEffect);
    }
    
    /**
     * Restituisce un colore basato sulla classe della carta
     */
    private Color getColorForCardClass(String classe) {
        if (classe == null) return Color.GRAY;
        
        switch (classe.toLowerCase()) {
            case "alleato":
                return Color.rgb(45, 85, 170);
            case "incantesimo":
                return Color.rgb(130, 40, 180);
            case "oggetto":
                return Color.rgb(180, 130, 40);
            case "malvagio":
                return Color.rgb(120, 20, 20);
            case "artioscure":
                return Color.rgb(30, 10, 40);
            case "horcrux":
                return Color.rgb(10, 10, 10);
            case "competenza":
                return Color.rgb(20, 120, 90);
            case "eroe":
                return Color.rgb(150, 30, 30);
            default:
                return Color.rgb(60, 60, 60);
        }
    }
    
    /**
     * Restituisce un simbolo per il retro della carta
     */
    private String getSymbolForCardClass(String classe) {
        if (classe == null) return "?";
        
        switch (classe.toLowerCase()) {
            case "alleato":
                return "👤";
            case "incantesimo":
                return "⚡";
            case "oggetto":
                return "🔮";
            case "malvagio":
                return "💀";
            case "artioscure":
                return "🐍";
            case "horcrux":
                return "💎";
            case "competenza":
                return "📖";
            case "eroe":
                return "⭐";
            default:
                return "❓";
        }
    }
    
    /**
     * Gira la carta (fronte/retro)
     */
    public void flip() {
        isFaceUp = !isFaceUp;
        this.getChildren().clear();
        
        if (isFaceUp) {
            creaFacciaCarta();
        } else {
            creaRetroCarta();
        }
        
        aggiungiInterazioni();
    }
    
    /**
     * Cambia le dimensioni della carta
     */
    public void resize(CardSize newSize) {
        this.currentSize = newSize;
        this.getChildren().clear();
        inizializzaGrafica();
        aggiungiInterazioni();
    }
    
    /**
     * Imposta il callback per il click
     */
    public void setOnClick(Runnable callback) {
        this.onClickCallback = callback;
    }
    
    /**
     * Imposta il callback per l'hover
     */
    public void setOnHover(Runnable callback) {
        this.onHoverCallback = callback;
    }
    
    /**
     * Abilita/disabilita l'interazione
     */
    public void setInteractive(boolean interactive) {
        if (interactive) {
            this.setCursor(Cursor.HAND);
            this.setOpacity(1.0);
        } else {
            this.setCursor(Cursor.DEFAULT);
            this.setOpacity(0.7);
        }
        this.setDisable(!interactive);
    }
    
    /**
     * Evidenzia la carta
     */
    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            DropShadow highlightShadow = new DropShadow();
            highlightShadow.setColor(Color.YELLOW);
            highlightShadow.setRadius(20);
            highlightShadow.setSpread(0.5);
            this.setEffect(highlightShadow);
            this.setScaleX(1.05);
            this.setScaleY(1.05);
        } else {
            this.setEffect(shadowEffect);
            this.setScaleX(1.0);
            this.setScaleY(1.0);
        }
    }
    
    // Getters
    public Carta getCarta() {
        return carta;
    }
    
    public boolean isFaceUp() {
        return isFaceUp;
    }
    
    public CardSize getCurrentSize() {
        return currentSize;
    }
}