package grafica.ui;

import carte.Carta;
import carte.ArteOscura;
import gioco.StatoDiGioco;
import grafica.components.GameBoard;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

/**
 * UIController - Controller centrale per l'interfaccia utente.
 * 
 * Responsabilità:
 * - Mostrare messaggi e notifiche
 * - Gestire popup e dialoghi
 * - Animazioni UI
 * - Preview delle carte
 * - Feedback visivo e sonoro
 * - Sincronizzazione tra UI e stato di gioco
 */
public class UIController {
    
    private StatoDiGioco statoDiGioco;
    private GameBoard gameBoard;
    private Pane rootPane;
    
    // Componenti UI
    private Text messageText;
    private VBox notificationArea;
    private StackPane popupContainer;
    private Pane cardPreviewPane;
    
    // Timeline per animazioni
    private Timeline messageTimeline;
    
    // Stato
    private boolean popupVisible;
    
    /**
     * Costruttore
     */
    public UIController(StatoDiGioco statoDiGioco, GameBoard gameBoard, Pane rootPane) {
        this.statoDiGioco = statoDiGioco;
        this.gameBoard = gameBoard;
        this.rootPane = rootPane;
        this.popupVisible = false;
        
        inizializzaComponenti();
    }
    
    /**
     * Inizializza i componenti UI
     */
    private void inizializzaComponenti() {
        // Area messaggi (bottom-center)
        messageText = new Text("");
        messageText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        messageText.setFill(Color.LIGHTGREEN);
        messageText.setLayoutX(960 - 200); // Centrato
        messageText.setLayoutY(1050);
        messageText.setVisible(false);
        rootPane.getChildren().add(messageText);
        
        // Area notifiche (top-right)
        notificationArea = new VBox(5);
        notificationArea.setAlignment(Pos.TOP_RIGHT);
        notificationArea.setLayoutX(1550);
        notificationArea.setLayoutY(120);
        notificationArea.setPrefWidth(350);
        notificationArea.setMaxHeight(800);
        rootPane.getChildren().add(notificationArea);
        
        // Container per popup
        popupContainer = new StackPane();
        popupContainer.setLayoutX(0);
        popupContainer.setLayoutY(0);
        popupContainer.setPrefSize(1920, 1080);
        popupContainer.setVisible(false);
        popupContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        rootPane.getChildren().add(popupContainer);
        
        // Pane per preview carte (right side)
        cardPreviewPane = new Pane();
        cardPreviewPane.setLayoutX(1600);
        cardPreviewPane.setLayoutY(400);
        cardPreviewPane.setPrefSize(300, 450);
        cardPreviewPane.setVisible(false);
        rootPane.getChildren().add(cardPreviewPane);
        
        System.out.println("✓ UIController inizializzato");
    }
    
    // ========================================================================
    // MESSAGGI E NOTIFICHE
    // ========================================================================
    
    /**
     * Mostra un messaggio temporaneo
     */
    public void mostraMessaggio(String messaggio) {
        mostraMessaggio(messaggio, Color.LIGHTGREEN, 3000);
    }
    
    /**
     * Mostra un messaggio con colore e durata personalizzati
     */
    public void mostraMessaggio(String messaggio, Color colore, int durataMs) {
        messageText.setText(messaggio);
        messageText.setFill(colore);
        messageText.setVisible(true);
        
        // Cancella timeline precedente
        if (messageTimeline != null) {
            messageTimeline.stop();
        }
        
        // Animazione fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), messageText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Timeline per nascondere dopo la durata
        messageTimeline = new Timeline(new KeyFrame(
            Duration.millis(durataMs),
            event -> nascondiMessaggio()
        ));
        
        fadeIn.play();
        messageTimeline.play();
    }
    
    /**
     * Nasconde il messaggio corrente
     */
    private void nascondiMessaggio() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), messageText);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> messageText.setVisible(false));
        fadeOut.play();
    }
    
    /**
     * Mostra un messaggio di errore
     */
    public void mostraErrore(String errore) {
        mostraMessaggio("❌ " + errore, Color.RED, 4000);
        riproduciSuono("errore");
    }
    
    /**
     * Mostra una notifica nell'area notifiche
     */
    public void mostraNotifica(String testo, Color colore) {
        HBox notifica = new HBox(10);
        notifica.setAlignment(Pos.CENTER_RIGHT);
        notifica.setPadding(new javafx.geometry.Insets(8));
        notifica.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); " +
                         "-fx-background-radius: 5; " +
                         "-fx-border-color: " + toHexString(colore) + "; " +
                         "-fx-border-width: 2; " +
                         "-fx-border-radius: 5;");
        
        Text text = new Text(testo);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        text.setFill(colore);
        
        notifica.getChildren().add(text);
        notificationArea.getChildren().add(0, notifica);
        
        // Rimuovi dopo 5 secondi
        Timeline removeTimeline = new Timeline(new KeyFrame(
            Duration.seconds(5),
            event -> {
                FadeTransition fade = new FadeTransition(Duration.millis(500), notifica);
                fade.setToValue(0);
                fade.setOnFinished(e -> notificationArea.getChildren().remove(notifica));
                fade.play();
            }
        ));
        removeTimeline.play();
        
        // Limita numero notifiche
        while (notificationArea.getChildren().size() > 5) {
            notificationArea.getChildren().remove(notificationArea.getChildren().size() - 1);
        }
    }
    
    // ========================================================================
    // POPUP E DIALOGHI
    // ========================================================================
    
    /**
     * Mostra i dettagli di una carta in un popup
     */
    public void mostraDettagliCarta(Carta carta) {
        if (popupVisible) return;
        
        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.setPrefSize(600, 800);
        popup.setStyle("-fx-background-color: #2a2a3e; " +
                      "-fx-background-radius: 15; " +
                      "-fx-border-color: gold; " +
                      "-fx-border-width: 3; " +
                      "-fx-border-radius: 15; " +
                      "-fx-padding: 30;");
        
        // Titolo
        Text titolo = new Text(carta.getNome());
        titolo.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 32));
        titolo.setFill(Color.GOLD);
        
        // Tipo
        Text tipo = new Text(carta.getClasse());
        tipo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        tipo.setFill(Color.LIGHTBLUE);
        
        // Descrizione
        Text descrizione = new Text(carta.getDescrizione());
        descrizione.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        descrizione.setFill(Color.WHITE);
        descrizione.setWrappingWidth(500);
        
        // Costo
        HBox costoBox = new HBox(10);
        costoBox.setAlignment(Pos.CENTER);
        
        Text costoLabel = new Text("Costo: ");
        costoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        costoLabel.setFill(Color.WHITE);
        
        Text costoValue = new Text(String.valueOf(carta.getCosto()));
        costoValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        costoValue.setFill(Color.YELLOW);
        
        costoBox.getChildren().addAll(costoLabel, costoValue);
        
        // Pulsante chiudi
        Text chiudiBtn = new Text("✖ CHIUDI");
        chiudiBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        chiudiBtn.setFill(Color.LIGHTCORAL);
        chiudiBtn.setOnMouseClicked(event -> nascondiPopup());
        chiudiBtn.setOnMouseEntered(event -> chiudiBtn.setFill(Color.RED));
        chiudiBtn.setOnMouseExited(event -> chiudiBtn.setFill(Color.LIGHTCORAL));
        chiudiBtn.setStyle("-fx-cursor: hand;");
        
        popup.getChildren().addAll(titolo, tipo, descrizione, costoBox, chiudiBtn);
        
        mostraPopup(popup);
    }
    
    /**
     * Mostra una Arte Oscura
     */
    public void mostraArteOscura(ArteOscura arteOscura) {
        if (popupVisible) return;
        
        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.setPrefSize(600, 700);
        popup.setStyle("-fx-background-color: #1a0a0a; " +
                      "-fx-background-radius: 15; " +
                      "-fx-border-color: darkred; " +
                      "-fx-border-width: 4; " +
                      "-fx-border-radius: 15; " +
                      "-fx-padding: 30;");
        
        // Icona
        Text icona = new Text("☠️");
        icona.setFont(Font.font(80));
        
        // Titolo
        Text titolo = new Text("ARTE OSCURA!");
        titolo.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 36));
        titolo.setFill(Color.DARKRED);
        
        // Nome
        Text nome = new Text(arteOscura.getNome());
        nome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nome.setFill(Color.RED);
        
        // Descrizione
        Text descrizione = new Text(arteOscura.getDescrizione());
        descrizione.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        descrizione.setFill(Color.WHITE);
        descrizione.setWrappingWidth(500);
        
        // Pulsante continua
        Text continuaBtn = new Text("➤ CONTINUA");
        continuaBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        continuaBtn.setFill(Color.ORANGE);
        continuaBtn.setOnMouseClicked(event -> {
            nascondiPopup();
            // Esegui gli effetti dell'arte oscura
            // TODO: Integrare con il sistema di effetti
        });
        continuaBtn.setOnMouseEntered(event -> continuaBtn.setFill(Color.YELLOW));
        continuaBtn.setOnMouseExited(event -> continuaBtn.setFill(Color.ORANGE));
        continuaBtn.setStyle("-fx-cursor: hand;");
        
        popup.getChildren().addAll(icona, titolo, nome, descrizione, continuaBtn);
        
        mostraPopup(popup);
        riproduciSuono("arte_oscura");
    }
    
    /**
     * Mostra un popup generico
     */
    private void mostraPopup(Pane content) {
        popupContainer.getChildren().clear();
        popupContainer.getChildren().add(content);
        popupContainer.setVisible(true);
        popupVisible = true;
        
        // Animazione fade in
        FadeTransition fade = new FadeTransition(Duration.millis(300), popupContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        
        // Animazione scale del contenuto
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), content);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }
    
    /**
     * Nasconde il popup corrente
     */
    public void nascondiPopup() {
        if (!popupVisible) return;
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), popupContainer);
        fade.setToValue(0);
        fade.setOnFinished(event -> {
            popupContainer.setVisible(false);
            popupContainer.getChildren().clear();
            popupVisible = false;
        });
        fade.play();
    }
    
    // ========================================================================
    // PREVIEW CARTE
    // ========================================================================
    
    /**
     * Mostra la preview di una carta (senza bloccare il gioco)
     */
    public void mostraPreviewCarta(Carta carta) {
        // TODO: Implementare preview laterale
        // Per ora usiamo solo l'effetto hover sulla carta stessa
    }
    
    /**
     * Nasconde la preview della carta
     */
    public void nascondiPreviewCarta() {
        cardPreviewPane.setVisible(false);
        cardPreviewPane.getChildren().clear();
    }
    
    // ========================================================================
    // SELEZIONE CARTE
    // ========================================================================
    
    /**
     * Aggiorna la visualizzazione delle carte selezionate
     */
    public void aggiornaSelezioneCarte(List<Carta> carteSelezionate) {
        // TODO: Evidenziare visivamente le carte selezionate
        String nomi = carteSelezionate.stream()
            .map(Carta::getNome)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        
        if (!nomi.isEmpty()) {
            mostraNotifica("Selezionate: " + nomi, Color.LIGHTBLUE);
        }
    }
    
    // ========================================================================
    // AUDIO
    // ========================================================================
    
    /**
     * Riproduce un effetto sonoro
     */
    public void riproduciSuono(String nomeEffetto) {
        // TODO: Implementare sistema audio
        System.out.println("🔊 Suono: " + nomeEffetto);
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Converte un Color JavaFX in stringa hex
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
    
    /**
     * Aggiorna tutto il board
     */
    public void aggiornaBoard() {
        gameBoard.aggiornaBoard();
    }
    
    // Getters
    
    public boolean isPopupVisible() {
        return popupVisible;
    }
}