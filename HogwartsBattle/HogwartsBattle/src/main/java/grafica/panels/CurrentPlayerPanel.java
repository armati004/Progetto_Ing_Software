package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import gioco.StatoDiGioco;
import gioco.Giocatore;
import gioco.FaseTurno;
import carte.Carta;
import grafica.GameController;

/**
 * CurrentPlayerPanel - Mostra l'eroe e la mano del giocatore corrente
 * VERSIONE CORRETTA con carte disposte in fila da 5
 */
public class CurrentPlayerPanel extends HBox {
    
    private StatoDiGioco stato;
    private GameController controller;
    private VBox heroSection;
    private ScrollPane handScrollPane;
    private GridPane handGrid;
    private Button nextPhaseButton;
    
    public CurrentPlayerPanel(StatoDiGioco stato) {
        this.stato = stato;
        this.controller = GameController.getInstance();
        
        setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #FFD700; -fx-border-width: 3;");
        setSpacing(15);
        setPadding(new Insets(15));
        setMinHeight(200);
        
        // SEZIONE SINISTRA: Eroe + Bottone
        this.heroSection = creaSezioneEroe();
        heroSection.setPrefWidth(350);
        heroSection.setMinWidth(300);
        HBox.setHgrow(heroSection, Priority.NEVER);
        
        // SEZIONE DESTRA: Mano di carte in griglia 5 colonne
        this.handGrid = new GridPane();
        handGrid.setHgap(8);
        handGrid.setVgap(8);
        handGrid.setPadding(new Insets(10));
        handGrid.setStyle("-fx-background-color: #1a1a1a;");
        
        this.handScrollPane = new ScrollPane(handGrid);
        handScrollPane.setStyle("-fx-background: #1a1a1a; -fx-border-color: #666; -fx-border-width: 1;");
        handScrollPane.setFitToHeight(true);
        handScrollPane.setFitToWidth(true);
        handScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        handScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        HBox.setHgrow(handScrollPane, Priority.ALWAYS);
        
        aggiornaMano();
        
        getChildren().addAll(heroSection, handScrollPane);
    }
    
    /**
     * Crea sezione sinistra con info eroe
     */
    private VBox creaSezioneEroe() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #FFD700; -fx-border-width: 2;");
        section.setPadding(new Insets(15));
        section.setAlignment(Pos.TOP_CENTER);
        
     // ⭐ DIFESA 1: Verifica stato
        if (stato == null) {
            System.err.println("❌ CurrentPlayerPanel: stato è NULL!");
            Label errorLabel = new Label("⚠️ Errore: stato non valido");
            errorLabel.setTextFill(Color.RED);
            errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            section.getChildren().add(errorLabel);
            return section;
        }
        
        // ⭐ DIFESA 2: Verifica lista giocatori
        if (stato.getGiocatori() == null) {
            System.err.println("❌ CurrentPlayerPanel: lista giocatori è NULL!");
            Label errorLabel = new Label("⚠️ Errore: giocatori non validi");
            errorLabel.setTextFill(Color.RED);
            section.getChildren().add(errorLabel);
            return section;
        }
        
        if (stato.getGiocatori().isEmpty()) {
            System.err.println("❌ CurrentPlayerPanel: lista giocatori vuota!");
            Label errorLabel = new Label("⚠️ Errore: nessun giocatore");
            errorLabel.setTextFill(Color.RED);
            section.getChildren().add(errorLabel);
            return section;
        }
        
        int indiceGiocatore = stato.getGiocatoreCorrente();
        
        // ⭐ DIFESA 3: Verifica indice valido
        if (indiceGiocatore < 0 || indiceGiocatore >= stato.getGiocatori().size()) {
            System.err.println("❌ CurrentPlayerPanel: indice non valido: " + indiceGiocatore + 
                             " (max: " + (stato.getGiocatori().size() - 1) + ")");
            Label errorLabel = new Label("⚠️ Errore: giocatore non valido");
            errorLabel.setTextFill(Color.RED);
            section.getChildren().add(errorLabel);
            return section;
        }
        
        // ⭐ ORA È SICURO
        Giocatore giocatore = stato.getGiocatori().get(indiceGiocatore);
        
        if (giocatore == null || giocatore.getEroe() == null) {
            System.err.println("❌ CurrentPlayerPanel: giocatore o eroe NULL!");
            Label errorLabel = new Label("⚠️ Errore: dati giocatore");
            errorLabel.setTextFill(Color.RED);
            section.getChildren().add(errorLabel);
            return section;
        }
        
        // Nome eroe
        Label heroNameLabel = new Label("⭐ " + giocatore.getEroe().getNome());
        heroNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        heroNameLabel.setTextFill(Color.web("#FFD700"));
        heroNameLabel.setWrapText(true);
        heroNameLabel.setAlignment(Pos.CENTER);
        
        // Statistiche
        VBox statsBox = new VBox(8);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-border-color: #666; -fx-border-width: 0 0 2 0;");
        
        Label healthLabel = new Label("❤️ Salute: " + giocatore.getSalute() + "/" + giocatore.getSaluteMax());
        healthLabel.setTextFill(Color.web("#FF6666"));
        healthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label attackLabel = new Label("⚔️ Attacco: " + giocatore.getAttacco());
        attackLabel.setTextFill(Color.web("#FFAA66"));
        attackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label influenceLabel = new Label("🪙 Influenza: " + giocatore.getGettone());
        influenceLabel.setTextFill(Color.web("#FFD700"));
        influenceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
     // ⭐ NUOVO: Carte nel mazzo
        int carteNelMazzo = giocatore.getMazzo().getCarte().size();
        Label deckLabel = new Label("📚 Carte nel mazzo: " + carteNelMazzo);
        deckLabel.setTextFill(Color.web("#66CCFF"));
        deckLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        statsBox.getChildren().addAll(healthLabel, attackLabel, influenceLabel, deckLabel);
        
        // Fase corrente
        Label faseLabel = new Label("Fase: " + getNomeFase(stato.getFaseCorrente()));
        faseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        faseLabel.setTextFill(Color.web("#66CCFF"));
        faseLabel.setWrapText(true);
        faseLabel.setAlignment(Pos.CENTER);
        
        // Bottone fase successiva
        this.nextPhaseButton = new Button();
        nextPhaseButton.setPrefWidth(320);
        nextPhaseButton.setPrefHeight(50);
        nextPhaseButton.setStyle(
            "-fx-background-color: #0066cc; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px; " +
            "-fx-border-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        aggiornaNomeBottoneFase();
        
        nextPhaseButton.setOnAction(e -> {
            if (controller != null) {
                controller.prossimaFase();
            }
        });
        
        nextPhaseButton.setOnMouseEntered(e -> 
            nextPhaseButton.setStyle(
                "-fx-background-color: #0052a3; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10px; " +
                "-fx-border-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        nextPhaseButton.setOnMouseExited(e -> 
            nextPhaseButton.setStyle(
                "-fx-background-color: #0066cc; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10px; " +
                "-fx-border-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        VBox.setVgrow(statsBox, Priority.ALWAYS);
        
        section.getChildren().addAll(heroNameLabel, statsBox, faseLabel, nextPhaseButton);
        return section;
    }
    
    /**
     * Aggiorna la mano di carte - DISPONE IN GRIGLIA 5 COLONNE
     */
    private void aggiornaMano() {
        handGrid.getChildren().clear();
        
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        int row = 0;
        int col = 0;
        
        for (int i = 0; i < giocatore.getMano().size(); i++) {
            Carta carta = giocatore.getMano().get(i);
            CardButton cardButton = new CardButton(carta, i, giocatore.getMano().size());
            
            handGrid.add(cardButton, col, row);
            
            col++;
            if (col >= 5) {  // ⭐ 5 carte per riga
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Aggiorna il testo del bottone fase
     */
    public void aggiornaNomeBottoneFase() {
        FaseTurno faseAttuale = stato.getFaseCorrente();
        FaseTurno prossimaFase = calcolaProssimaFase(faseAttuale);
        
        String nomeProssima = getNomeFase(prossimaFase);
        nextPhaseButton.setText("▶️ " + nomeProssima);
    }
    
    /**
     * Calcola prossima fase
     */
    private FaseTurno calcolaProssimaFase(FaseTurno faseCorrente) {
        switch (faseCorrente) {
            case ARTI_OSCURE: return FaseTurno.MALVAGI;
            case MALVAGI: return stato.isHasHorcruxes() ? FaseTurno.HORCRUX : FaseTurno.GIOCA_CARTE;
            case HORCRUX: return FaseTurno.GIOCA_CARTE;
            case GIOCA_CARTE: return FaseTurno.ATTACCA;
            case ATTACCA: return FaseTurno.ACQUISTA_CARTE;
            case ACQUISTA_CARTE: return FaseTurno.FINE_TURNO;
            case FINE_TURNO: return FaseTurno.ARTI_OSCURE;
            default: return FaseTurno.GIOCA_CARTE;
        }
    }
    
    /**
     * Nome user-friendly della fase
     */
    private String getNomeFase(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "Arti Oscure";
            case MALVAGI: return "Malvagi";
            case HORCRUX: return "Horcrux";
            case GIOCA_CARTE: return "Gioca Carte";
            case ATTACCA: return "Attacca";
            case ACQUISTA_CARTE: return "Acquista Carte";
            case FINE_TURNO: return "Fine Turno";
            default: return "Prossima Fase";
        }
    }
    
    /**
     * Aggiorna tutto il pannello
     */
    public void aggiorna() {
        getChildren().clear();
        
        this.heroSection = creaSezioneEroe();
        heroSection.setPrefWidth(350);
        heroSection.setMinWidth(300);
        HBox.setHgrow(heroSection, Priority.NEVER);
        
        aggiornaMano();
        
        HBox.setHgrow(handScrollPane, Priority.ALWAYS);
        
        getChildren().addAll(heroSection, handScrollPane);
    }
}