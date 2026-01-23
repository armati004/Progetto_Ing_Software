package grafica.components;

import carte.*;
import gioco.*;
import grafica.components.TokenManager.TokenType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * GameBoard - Il tabellone principale di gioco
 * 
 * Layout ottimizzato:
 * - TOP: Barra con statistiche degli altri giocatori (mini pannelli)
 * - CENTRO: Tabellone con luogo, malvagi, mercato, mazzi
 * - BOTTOM: Pannello dettagliato del giocatore corrente (mano completa + statistiche)
 */
public class GameBoard extends Pane {
    
    // Dimensioni finestra
    private static final double WINDOW_WIDTH = 1920;
    private static final double WINDOW_HEIGHT = 1080;
    
    // Dimensioni e posizione tabellone (centrato, ridotto per lasciare spazio)
    private static final double BOARD_WIDTH = 1200;  // 756 * 1.58
    private static final double BOARD_HEIGHT = 890;  // 560 * 1.58
    private static final double BOARD_X = (WINDOW_WIDTH - BOARD_WIDTH) / 2;
    private static final double BOARD_Y = 120; // Sotto la barra superiore
    
    // Barra superiore (altri giocatori)
    private static final double TOP_BAR_HEIGHT = 100;
    private static final double TOP_BAR_Y = 10;
    
    // Pannello giocatore corrente (bottom)
    private static final double CURRENT_PLAYER_HEIGHT = 250;
    private static final double CURRENT_PLAYER_Y = WINDOW_HEIGHT - CURRENT_PLAYER_HEIGHT - 10;
    
    // Aree del tabellone (coordinate relative al tabellone)
    // Sinistra - Area Luogo
    private static final double LOCATION_X = BOARD_X + 40;
    private static final double LOCATION_Y = BOARD_Y + 20;
    private static final double LOCATION_WIDTH = 350;
    private static final double LOCATION_HEIGHT = 220;
    
    // Centro-Sinistra - Area Malvagi (sotto il luogo)
    private static final double VILLAINS_X = BOARD_X + 60;
    private static final double VILLAINS_Y = BOARD_Y + 270;
    private static final double VILLAIN_SPACING = 130;
    
    // Destra - Area Mercato (6 carte in 2 colonne)
    private static final double MARKET_X = BOARD_X + BOARD_WIDTH - 280;
    private static final double MARKET_Y = BOARD_Y + 60;
    private static final double MARKET_CARD_SPACING = 10;
    
    // Centro-Bottom - Area Mazzi
    private static final double DECKS_X = BOARD_X + BOARD_WIDTH / 2 - 200;
    private static final double DECKS_Y = BOARD_Y + BOARD_HEIGHT - 150;
    
    // Stato del gioco
    private StatoDiGioco statoDiGioco;
    
    // Componenti UI
    private ImageView tabelloneImage;
    private Pane locationArea;
    private HBox villainsArea;
    private VBox marketArea;
    private HBox topBar;
    private Pane currentPlayerArea;
    private VBox decksArea;
    
    // Liste componenti
    private List<CardView> marketCards;
    private List<CardView> villainCards;
    private List<MiniPlayerPanel> otherPlayerPanels;
    private PlayerPanel currentPlayerPanel;
    
    /**
     * Costruttore
     */
    public GameBoard(StatoDiGioco statoDiGioco) {
        this.statoDiGioco = statoDiGioco;
        this.marketCards = new ArrayList<>();
        this.villainCards = new ArrayList<>();
        this.otherPlayerPanels = new ArrayList<>();
        
        // Carica le immagini dei segnalini
        TokenManager.caricaImmagini();
        
        inizializzaBoard();
    }
    
    /**
     * Inizializza il tabellone e tutti i suoi componenti
     */
    private void inizializzaBoard() {
        // Sfondo
        this.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setStyle("-fx-background-color: #0f0f1e;");
        
        // Carica e posiziona l'immagine del tabellone
        caricaTabellone();
        
        // Crea le aree di gioco
        creaBarraSuperiore();
        creaAreaLuogo();
        creaAreaMalvagi();
        creaAreaMercato();
        creaAreaMazzi();
        creaAreaGiocatoreCorrente();
        
        // Aggiorna con i dati dello stato
        aggiornaBoard();
    }
    
    /**
     * Carica e posiziona l'immagine del tabellone
     */
    private void caricaTabellone() {
        try {
            Image tabellone = caricaImmagine("tabellone.png");
            
            tabelloneImage = new ImageView(tabellone);
            tabelloneImage.setFitWidth(BOARD_WIDTH);
            tabelloneImage.setFitHeight(BOARD_HEIGHT);
            tabelloneImage.setPreserveRatio(true);
            tabelloneImage.setSmooth(true);
            tabelloneImage.setLayoutX(BOARD_X);
            tabelloneImage.setLayoutY(BOARD_Y);
            
            // Effetto ombra
            DropShadow shadow = new DropShadow();
            shadow.setRadius(25);
            shadow.setColor(Color.color(0, 0, 0, 0.7));
            tabelloneImage.setEffect(shadow);
            
            this.getChildren().add(tabelloneImage);
            
            System.out.println("✓ Tabellone caricato");
            
        } catch (Exception e) {
            System.err.println("✗ Errore nel caricamento del tabellone: " + e.getMessage());
            
            // Crea un rettangolo placeholder
            Rectangle placeholder = new Rectangle(BOARD_WIDTH, BOARD_HEIGHT);
            placeholder.setFill(Color.rgb(139, 90, 43, 0.3));
            placeholder.setStroke(Color.DARKGOLDENROD);
            placeholder.setStrokeWidth(5);
            placeholder.setArcWidth(15);
            placeholder.setArcHeight(15);
            placeholder.setLayoutX(BOARD_X);
            placeholder.setLayoutY(BOARD_Y);
            this.getChildren().add(placeholder);
        }
    }
    
    /**
     * Crea la barra superiore con gli altri giocatori
     */
    private void creaBarraSuperiore() {
        topBar = new HBox(20);
        topBar.setLayoutX(50);
        topBar.setLayoutY(TOP_BAR_Y);
        topBar.setPrefHeight(TOP_BAR_HEIGHT);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        // Sfondo barra
        Rectangle bg = new Rectangle(WINDOW_WIDTH - 100, TOP_BAR_HEIGHT);
        bg.setFill(Color.rgb(0, 0, 0, 0.6));
        bg.setStroke(Color.GOLD);
        bg.setStrokeWidth(2);
        bg.setArcWidth(10);
        bg.setArcHeight(10);
        
        Pane bgPane = new Pane(bg);
        bgPane.setLayoutX(50);
        bgPane.setLayoutY(TOP_BAR_Y);
        this.getChildren().add(bgPane);
        
        // Titolo
        Text title = new Text("👥 ALTRI GIOCATORI");
        title.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 16));
        title.setFill(Color.GOLD);
        topBar.getChildren().add(title);
        
        this.getChildren().add(topBar);
    }
    
    /**
     * Crea l'area del luogo attuale
     */
    private void creaAreaLuogo() {
        locationArea = new Pane();
        locationArea.setLayoutX(LOCATION_X);
        locationArea.setLayoutY(LOCATION_Y);
        locationArea.setPrefSize(LOCATION_WIDTH, LOCATION_HEIGHT);
        
        // Sfondo semi-trasparente
        Rectangle bg = new Rectangle(LOCATION_WIDTH, LOCATION_HEIGHT);
        bg.setFill(Color.rgb(0, 0, 0, 0.5));
        bg.setStroke(Color.GOLD);
        bg.setStrokeWidth(3);
        bg.setArcWidth(10);
        bg.setArcHeight(10);
        locationArea.getChildren().add(bg);
        
        // Titolo area
        Text title = new Text("🏰 LUOGO");
        title.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 16));
        title.setFill(Color.GOLD);
        title.setLayoutX(10);
        title.setLayoutY(25);
        locationArea.getChildren().add(title);
        
        this.getChildren().add(locationArea);
    }
    
    /**
     * Crea l'area dei malvagi attivi
     */
    private void creaAreaMalvagi() {
        villainsArea = new HBox(15);
        villainsArea.setLayoutX(VILLAINS_X);
        villainsArea.setLayoutY(VILLAINS_Y);
        villainsArea.setAlignment(Pos.CENTER_LEFT);
        
        // Titolo area
        VBox titleBox = new VBox(3);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPrefWidth(100);
        titleBox.setStyle("-fx-background-color: rgba(100, 0, 0, 0.7); " +
                         "-fx-background-radius: 8; " +
                         "-fx-padding: 8;");
        
        Text icon = new Text("⚔️");
        icon.setFont(Font.font(32));
        
        Text subtitle = new Text("MALVAGI");
        subtitle.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 12));
        subtitle.setFill(Color.DARKRED);
        
        titleBox.getChildren().addAll(icon, subtitle);
        villainsArea.getChildren().add(titleBox);
        
        this.getChildren().add(villainsArea);
    }
    
    /**
     * Crea l'area del mercato
     */
    private void creaAreaMercato() {
        marketArea = new VBox(8);
        marketArea.setLayoutX(MARKET_X);
        marketArea.setLayoutY(MARKET_Y);
        marketArea.setAlignment(Pos.TOP_CENTER);
        
        // Titolo
        Text title = new Text("🛒 MERCATO");
        title.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 16));
        title.setFill(Color.LIGHTBLUE);
        marketArea.getChildren().add(title);
        
        this.getChildren().add(marketArea);
    }
    
    /**
     * Crea l'area centrale dei mazzi
     */
    private void creaAreaMazzi() {
        decksArea = new VBox(12);
        decksArea.setLayoutX(DECKS_X);
        decksArea.setLayoutY(DECKS_Y);
        decksArea.setAlignment(Pos.CENTER);
        
        this.getChildren().add(decksArea);
    }
    
    /**
     * Crea l'area del giocatore corrente (bottom)
     */
    private void creaAreaGiocatoreCorrente() {
        currentPlayerArea = new Pane();
        currentPlayerArea.setLayoutX(0);
        currentPlayerArea.setLayoutY(CURRENT_PLAYER_Y);
        currentPlayerArea.setPrefSize(WINDOW_WIDTH, CURRENT_PLAYER_HEIGHT);
        
        // Sfondo
        Rectangle bg = new Rectangle(WINDOW_WIDTH, CURRENT_PLAYER_HEIGHT);
        bg.setFill(Color.rgb(20, 20, 40, 0.95));
        bg.setStroke(Color.GOLD);
        bg.setStrokeWidth(3);
        currentPlayerArea.getChildren().add(bg);
        
        this.getChildren().add(currentPlayerArea);
    }
    
    /**
     * Aggiorna tutto il board con i dati dello stato di gioco
     */
    public void aggiornaBoard() {
        if (statoDiGioco == null) return;
        
        aggiornaLuogo();
        aggiornaMalvagi();
        aggiornaMercato();
        aggiornaMazzi();
        aggiornaBarraSuperiore();
        aggiornaGiocatoreCorrente();
    }
    
    /**
     * Aggiorna l'area del luogo
     */
    private void aggiornaLuogo() {
        Luogo luogo = statoDiGioco.getLuogoAttuale();
        if (luogo == null) return;
        
        // Pulisci area (mantieni bg e title)
        while (locationArea.getChildren().size() > 2) {
            locationArea.getChildren().remove(2);
        }
        
        try {
            // Immagine luogo (ridotta)
            Image locationImg = caricaImmagine(luogo.getPathImmagine());
            ImageView locationView = new ImageView(locationImg);
            locationView.setFitWidth(LOCATION_WIDTH - 20);
            locationView.setFitHeight(130);
            locationView.setPreserveRatio(true);
            locationView.setLayoutX(10);
            locationView.setLayoutY(40);
            
            Rectangle clip = new Rectangle(LOCATION_WIDTH - 20, 130);
            clip.setArcWidth(8);
            clip.setArcHeight(8);
            locationView.setClip(clip);
            
            locationArea.getChildren().add(locationView);
            
            // Nome luogo
            Text locationName = new Text(luogo.getNome());
            locationName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            locationName.setFill(Color.WHITE);
            locationName.setLayoutX(10);
            locationName.setLayoutY(190);
            locationArea.getChildren().add(locationName);
            
            // Barra salute
            double healthPercent = (double) luogo.getNumeroMarchiNeri() / luogo.getMarchiNeriMax();
            double barWidth = LOCATION_WIDTH - 20;
            
            // Sfondo barra
            Rectangle healthBg = new Rectangle(barWidth, 20);
            healthBg.setFill(Color.rgb(50, 50, 50));
            healthBg.setStroke(Color.WHITE);
            healthBg.setStrokeWidth(1);
            healthBg.setLayoutX(10);
            healthBg.setLayoutY(200);
            locationArea.getChildren().add(healthBg);
            
            // Barra salute riempita
            Rectangle healthBar = new Rectangle(barWidth * healthPercent, 20);
            Color healthColor = healthPercent > 0.5 ? Color.LIGHTGREEN : 
                              healthPercent > 0.25 ? Color.ORANGE : Color.RED;
            healthBar.setFill(healthColor);
            healthBar.setLayoutX(10);
            healthBar.setLayoutY(200);
            locationArea.getChildren().add(healthBar);
            
            // Testo salute
            Text healthText = new Text("❤️ " + luogo.getNumeroMarchiNeri() + " / " + luogo.getMarchiNeriMax());
            healthText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            healthText.setFill(Color.WHITE);
            healthText.setLayoutX(10 + barWidth / 2 - 30);
            healthText.setLayoutY(213);
            locationArea.getChildren().add(healthText);
            
        } catch (Exception e) {
            System.err.println("Errore nel caricamento del luogo: " + e.getMessage());
        }
    }
    
    /**
     * Aggiorna l'area dei malvagi
     */
    private void aggiornaMalvagi() {
        // Pulisci (mantieni il titolo)
        while (villainsArea.getChildren().size() > 1) {
            villainsArea.getChildren().remove(1);
        }
        villainCards.clear();
        
        List<Malvagio> malvagi = statoDiGioco.getMalvagiAttivi();
        
        for (Malvagio malvagio : malvagi) {
            VBox villainBox = new VBox(5);
            villainBox.setAlignment(Pos.CENTER);
            
            // Carta malvagio
            CardView cardView = new CardView(malvagio, CardView.CardSize.SMALL);
            
            // Info malvagio
            HBox infoBox = new HBox(10);
            infoBox.setAlignment(Pos.CENTER);
            infoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); " +
                           "-fx-background-radius: 5; " +
                           "-fx-padding: 5;");
            infoBox.setPrefWidth(CardView.CardSize.SMALL.getWidth());
            
            Text healthText = new Text("❤️ " + malvagio.getVita());
            healthText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            healthText.setFill(Color.RED);
            
            Text attackText = new Text("⚔️ " + malvagio.getDanno());
            attackText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            attackText.setFill(Color.ORANGE);
            
            infoBox.getChildren().addAll(healthText, attackText);
            
            villainBox.getChildren().addAll(cardView, infoBox);
            
            villainCards.add(cardView);
            villainsArea.getChildren().add(villainBox);
        }
    }
    
    /**
     * Aggiorna il mercato
     */
    private void aggiornaMercato() {
        // Rimuovi il grid esistente e mantieni solo il titolo
        while (marketArea.getChildren().size() > 1) {
            marketArea.getChildren().remove(1);
        }
        marketCards.clear();
        
        GridPane marketGrid = new GridPane();
        marketGrid.setHgap(MARKET_CARD_SPACING);
        marketGrid.setVgap(MARKET_CARD_SPACING);
        marketGrid.setAlignment(Pos.CENTER);
        
        List<Carta> mercato = statoDiGioco.getMercato();
        
        // Disponi le carte in 2 colonne x 3 righe
        for (int i = 0; i < mercato.size() && i < 6; i++) {
            Carta carta = mercato.get(i);
            if (carta != null) {
                CardView cardView = new CardView(carta, CardView.CardSize.SMALL);
                marketCards.add(cardView);
                
                int row = i / 2;
                int col = i % 2;
                
                marketGrid.add(cardView, col, row);
            }
        }
        
        marketArea.getChildren().add(marketGrid);
    }
    
    /**
     * Aggiorna i contatori dei mazzi
     */
    private void aggiornaMazzi() {
        decksArea.getChildren().clear();
        
        HBox decksInfo = new HBox(20);
        decksInfo.setAlignment(Pos.CENTER);
        
        // Conta le carte nei mazzi
        int shopCount = statoDiGioco.getMazzoNegozio().size();
        int darkCount = statoDiGioco.getMazzoArtiOscure().size();
        int villainCount = statoDiGioco.getMazzoMalvagi().size();
        
        VBox shopDeck = creaMazzoDeck("🏪", "Negozio", shopCount);
        VBox darkDeck = creaMazzoDeck("☠️", "Arti Oscure", darkCount);
        VBox villainDeck = creaMazzoDeck("😈", "Malvagi", villainCount);
        
        decksInfo.getChildren().addAll(shopDeck, darkDeck, villainDeck);
        decksArea.getChildren().add(decksInfo);
    }
    
    /**
     * Crea un indicatore di mazzo
     */
    private VBox creaMazzoDeck(String icon, String label, int count) {
        VBox deck = new VBox(4);
        deck.setAlignment(Pos.CENTER);
        deck.setPrefWidth(90);
        deck.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                     "-fx-background-radius: 8; " +
                     "-fx-padding: 8;");
        
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(28));
        
        Text labelText = new Text(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        labelText.setFill(Color.WHITE);
        
        Text countText = new Text(String.valueOf(count));
        countText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        countText.setFill(Color.LIGHTGREEN);
        
        deck.getChildren().addAll(iconText, labelText, countText);
        
        return deck;
    }
    
    /**
     * Aggiorna la barra superiore con gli altri giocatori
     */
    private void aggiornaBarraSuperiore() {
        // Rimuovi i pannelli esistenti (mantieni il titolo)
        while (topBar.getChildren().size() > 1) {
            topBar.getChildren().remove(1);
        }
        otherPlayerPanels.clear();
        
        List<Giocatore> giocatori = statoDiGioco.getGiocatori();
        int currentIndex = statoDiGioco.getGiocatoreCorrente();
        
        // Aggiungi tutti i giocatori TRANNE quello corrente
        for (int i = 0; i < giocatori.size(); i++) {
            if (i != currentIndex) {
                Giocatore giocatore = giocatori.get(i);
                MiniPlayerPanel miniPanel = new MiniPlayerPanel(giocatore);
                otherPlayerPanels.add(miniPanel);
                topBar.getChildren().add(miniPanel);
            }
        }
    }
    
    /**
     * Aggiorna l'area del giocatore corrente
     */
    private void aggiornaGiocatoreCorrente() {
        // Pulisci (mantieni solo lo sfondo)
        while (currentPlayerArea.getChildren().size() > 1) {
            currentPlayerArea.getChildren().remove(1);
        }
        
        Giocatore currentPlayer = statoDiGioco.getGiocatori().get(
            statoDiGioco.getGiocatoreCorrente()
        );
        
        // Crea il pannello completo del giocatore
        currentPlayerPanel = new PlayerPanel(currentPlayer, true);
        currentPlayerPanel.setLayoutX(20);
        currentPlayerPanel.setLayoutY(10);
        
        currentPlayerArea.getChildren().add(currentPlayerPanel);
        
        // Aggiungi visualizzazione mano (se c'è spazio)
        aggiungiVisMano(currentPlayer);
    }
    
    /**
     * Aggiunge la visualizzazione della mano del giocatore corrente
     */
    private void aggiungiVisMano(Giocatore giocatore) {
        HBox manoBox = new HBox(10);
        manoBox.setLayoutX(350);
        manoBox.setLayoutY(20);
        manoBox.setAlignment(Pos.CENTER_LEFT);
        
        Text manoLabel = new Text("✋ MANO:");
        manoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        manoLabel.setFill(Color.GOLD);
        manoBox.getChildren().add(manoLabel);
        
        // Mostra le prime 5 carte della mano
        int carteVisibili = Math.min(5, giocatore.getMano().size());
        for (int i = 0; i < carteVisibili; i++) {
            Carta carta = giocatore.getMano().get(i);
            CardView miniCard = new CardView(carta, CardView.CardSize.SMALL);
            miniCard.setScaleX(0.7);
            miniCard.setScaleY(0.7);
            manoBox.getChildren().add(miniCard);
        }
        
        // Se ci sono più carte
        if (giocatore.getMano().size() > carteVisibili) {
            Text moreText = new Text("+" + (giocatore.getMano().size() - carteVisibili));
            moreText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            moreText.setFill(Color.WHITE);
            manoBox.getChildren().add(moreText);
        }
        
        currentPlayerArea.getChildren().add(manoBox);
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
    
    // Getters
    
    public List<CardView> getMarketCards() {
        return marketCards;
    }
    
    public List<CardView> getVillainCards() {
        return villainCards;
    }
    
    public PlayerPanel getCurrentPlayerPanel() {
        return currentPlayerPanel;
    }
    
    public List<MiniPlayerPanel> getOtherPlayerPanels() {
        return otherPlayerPanels;
    }
}