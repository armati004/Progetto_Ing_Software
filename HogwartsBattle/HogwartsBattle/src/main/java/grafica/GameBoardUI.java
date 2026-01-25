package grafica;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import gioco.StatoDiGioco;
import grafica.panels.PlayersStatsPanel;
import grafica.panels.GameBoardPanel;
import grafica.panels.CurrentPlayerPanel;
import grafica.panels.MessagePanel;

/**
 * GameBoardUI - Componente principale che gestisce il layout del gioco
 * Divide lo spazio in 3 sezioni: top (statistiche), center (tabellone), bottom (giocatore corrente)
 * SUPPORTA RESIZE DINAMICO + MessagePanel in overlay
 */
public class GameBoardUI extends StackPane {
    
    private StatoDiGioco stato;
    private PlayersStatsPanel statsPanel;
    private GameBoardPanel boardPanel;
    private CurrentPlayerPanel playerPanel;
    private MessagePanel messagePanel;
    
    /**
     * Costruttore: inizializza i pannelli
     */
    public GameBoardUI(StatoDiGioco stato) {
        this.stato = stato;
        
        // Crea BorderPane per il layout principale
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #666; -fx-border-width: 1;");
        mainLayout.setPadding(new Insets(10));
        
        // Crea i pannelli
        this.statsPanel = new PlayersStatsPanel(stato);
        this.boardPanel = new GameBoardPanel(stato);
        this.playerPanel = new CurrentPlayerPanel(stato);
        
        // Top: Statistiche giocatori (15%)
        VBox topContainer = new VBox(statsPanel);
        topContainer.setPrefHeight(150);
        mainLayout.setTop(topContainer);
        BorderPane.setMargin(topContainer, new Insets(0, 0, 10, 0));
        
        // Center: Tabellone (60%) - ESPANDIBILE
        VBox centerContainer = new VBox(boardPanel);
        VBox.setVgrow(boardPanel, Priority.ALWAYS);
        mainLayout.setCenter(centerContainer);
        BorderPane.setMargin(centerContainer, new Insets(0, 0, 10, 0));
        
        // Bottom: Giocatore corrente (25%)
        VBox bottomContainer = new VBox(playerPanel);
        bottomContainer.setPrefHeight(250);
        bottomContainer.setMinHeight(200);
        mainLayout.setBottom(bottomContainer);
        
        // Crea MessagePanel in overlay
        this.messagePanel = new MessagePanel();
        
        // Aggiungi entrambi allo StackPane
        this.getChildren().addAll(mainLayout, messagePanel);
        
        // Allinea MessagePanel in alto a destra
        StackPane.setAlignment(messagePanel, Pos.TOP_RIGHT);
    }
    
    /**
     * Aggiorna tutti i pannelli
     */
    public void aggiorna() {
        if (statsPanel != null) statsPanel.aggiorna();
        if (boardPanel != null) boardPanel.aggiorna();
        if (playerPanel != null) playerPanel.aggiorna();
    }
    
    /**
     * Aggiorna solo il pannello del giocatore corrente
     */
    public void aggiornaGiocatoreCorrente() {
        if (playerPanel != null) playerPanel.aggiorna();
    }
    
    /**
     * Aggiorna solo il tabellone
     */
    public void aggiornaTabelone() {
        if (boardPanel != null) boardPanel.aggiorna();
        if (statsPanel != null) statsPanel.aggiorna();
    }
    
    // ============================================
    // GETTER
    // ============================================
    
    public PlayersStatsPanel getStatsPanel() {
        return statsPanel;
    }
    
    public GameBoardPanel getBoardPanel() {
        return boardPanel;
    }
    
    public CurrentPlayerPanel getPlayerPanel() {
        return playerPanel;
    }
    
    public StatoDiGioco getStato() {
        return stato;
    }
    
    public MessagePanel getMessagePanel() {
        return messagePanel;
    }
}