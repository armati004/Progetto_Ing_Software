package grafica;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import gioco.StatoDiGioco;
import grafica.panels.PlayersStatsPanel;
import grafica.panels.GameBoardPanel;
import grafica.panels.CurrentPlayerPanel;
import grafica.panels.MessagePanel;

/**
 * GameBoardUI - Layout che si adatta al resize della finestra
 */
public class GameBoardUI extends StackPane {
    
    private StatoDiGioco stato;
    private PlayersStatsPanel statsPanel;
    private GameBoardPanel boardPanel;
    private CurrentPlayerPanel playerPanel;
    private MessagePanel messagePanel;
    private Label annoLabel;
    
    public GameBoardUI(StatoDiGioco stato) {
        this.stato = stato;
        
        // Layout principale con VBox per distribuire lo spazio
        VBox mainLayout = new VBox(5);
        mainLayout.setStyle("-fx-background-color: #1a1a1a;");
        mainLayout.setPadding(new Insets(5));
        
        // TOP: Anno + Stats (dimensioni fisse)
        HBox topBar = creaTopBar();
        topBar.setMinHeight(45);
        topBar.setPrefHeight(50);
        topBar.setMaxHeight(60);
        
        this.statsPanel = new PlayersStatsPanel(stato);
        statsPanel.setMinHeight(70);
        statsPanel.setPrefHeight(85);
        statsPanel.setMaxHeight(100);
        
        // CENTER: Board (si espande per riempire lo spazio disponibile)
        this.boardPanel = new GameBoardPanel(stato);
        VBox.setVgrow(boardPanel, Priority.ALWAYS);
        
        // BOTTOM: Current Player (dimensioni con range)
        this.playerPanel = new CurrentPlayerPanel(stato);
        playerPanel.setMinHeight(180);
        playerPanel.setPrefHeight(220);
        playerPanel.setMaxHeight(300);
        
        // Aggiungi tutti
        mainLayout.getChildren().addAll(topBar, statsPanel, boardPanel, playerPanel);
        
        // Message Panel overlay
        this.messagePanel = new MessagePanel();
        
        this.getChildren().addAll(mainLayout, messagePanel);
        StackPane.setAlignment(messagePanel, Pos.TOP_RIGHT);
    }
    
    private HBox creaTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(8));
        topBar.setStyle(
            "-fx-background-color: #000000;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 0 0 3 0;"
        );
        
        this.annoLabel = new Label("ANNO " + stato.getAnnoCorrente());
        annoLabel.setTextFill(Color.web("#FFD700"));
        annoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        annoLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255, 215, 0, 0.9), 20, 0, 0, 0);");
        
        topBar.getChildren().add(annoLabel);
        return topBar;
    }
    
    public void aggiorna() {
        if (annoLabel != null) {
            annoLabel.setText("ANNO " + stato.getAnnoCorrente());
        }
        if (statsPanel != null) statsPanel.aggiorna();
        if (boardPanel != null) boardPanel.aggiorna();
        if (playerPanel != null) playerPanel.aggiorna();
    }
    
    public void aggiornaGiocatoreCorrente() {
        if (playerPanel != null) playerPanel.aggiorna();
    }
    
    public void aggiornaTabelone() {
        if (boardPanel != null) boardPanel.aggiorna();
        if (statsPanel != null) statsPanel.aggiorna();
    }
    
    public PlayersStatsPanel getStatsPanel() { return statsPanel; }
    public GameBoardPanel getBoardPanel() { return boardPanel; }
    public CurrentPlayerPanel getPlayerPanel() { return playerPanel; }
    public StatoDiGioco getStato() { return stato; }
    public MessagePanel getMessagePanel() { return messagePanel; }
}