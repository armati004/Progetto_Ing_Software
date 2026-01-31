package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import gioco.StatoDiGioco;
import gioco.Giocatore;
import gioco.FaseTurno;
import carte.Carta;
import grafica.GameController;

/**
 * CurrentPlayerPanel SEMPLICE - zero binding
 */
public class CurrentPlayerPanel extends HBox {
    
    private StatoDiGioco stato;
    private GameController controller;
    private Button nextPhaseButton;
    
    public CurrentPlayerPanel(StatoDiGioco stato) {
        this.stato = stato;
        this.controller = GameController.getInstance();
        
        setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #FFD700; -fx-border-width: 3;");
        setSpacing(10);
        setPadding(new Insets(10));
        
        aggiorna();
    }
    
    private VBox creaHeroSection() {
        VBox section = new VBox(6);
        section.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #FFD700; -fx-border-width: 2;");
        section.setPadding(new Insets(10));
        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(280);
        
        Giocatore g = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        Label nome = new Label("⭐ " + g.getEroe().getNome());
        nome.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nome.setTextFill(Color.web("#FFD700"));
        nome.setWrapText(true);
        
        Label desc = new Label(g.getEroe().getDescrizione());
        desc.setFont(Font.font("Arial", 10));
        desc.setTextFill(Color.web("#CCCCCC"));
        desc.setWrapText(true);
        desc.setMaxHeight(50);
        desc.setStyle("-fx-padding: 5; -fx-background-color: rgba(255,255,255,0.05);");
        
        VBox stats = new VBox(3);
        Label hp = new Label("❤️ Vita: " + g.getSalute() + "/" + g.getSaluteMax());
        hp.setTextFill(Color.web("#FF6666"));
        hp.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label atk = new Label("⚔️ Attacchi: " + g.getAttacco());
        atk.setTextFill(Color.web("#FFAA66"));
        atk.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label inf = new Label("🪙 Influenza: " + g.getGettone());
        inf.setTextFill(Color.web("#FFD700"));
        inf.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label deck = new Label("📚 Carte nel mazzo: " + g.getMazzo().getCarte().size());
        deck.setTextFill(Color.web("#66CCFF"));
        deck.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        stats.getChildren().addAll(hp, atk, inf, deck);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Label fase = new Label("Fase: " + getNomeFase(stato.getFaseCorrente()));
        fase.setFont(Font.font("Arial", 10));
        fase.setTextFill(Color.web("#66CCFF"));
        fase.setWrapText(true);
        
        this.nextPhaseButton = new Button("▶️ " + getNomeFase(calcolaProssimaFase()));
        nextPhaseButton.setMaxWidth(Double.MAX_VALUE);
        nextPhaseButton.setPrefHeight(40);
        nextPhaseButton.setStyle(
            "-fx-background-color: #0066cc; -fx-text-fill: white;" +
            "-fx-font-size: 12px; -fx-font-weight: bold;"
        );
        nextPhaseButton.setOnAction(e -> { if (controller != null) controller.prossimaFase(); });
        
        section.getChildren().addAll(nome, desc, stats, spacer, fase, nextPhaseButton);
        return section;
    }
    
    private ScrollPane creaHandSection() {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(8));
        
        Giocatore g = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        int col = 0, row = 0;
        
        for (int i = 0; i < g.getMano().size(); i++) {
            Carta carta = g.getMano().get(i);
            CardButton btn = new CardButton(carta, i, g.getMano().size());
            grid.add(btn, col, row);
            col++;
            if (col >= 5) { col = 0; row++; }
        }
        
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setStyle("-fx-background: #1a1a1a;");
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        HBox.setHgrow(scroll, Priority.ALWAYS);
        
        return scroll;
    }
    
    /**
     * Aggiorna il testo del bottone fase
     */
    public void aggiornaNomeBottoneFase() {
        FaseTurno prossimaFase = calcolaProssimaFase();
        
        String nomeProssima = getNomeFase(prossimaFase);
        nextPhaseButton.setText("▶️ " + nomeProssima);
    }
    
    private FaseTurno calcolaProssimaFase() {
        switch (stato.getFaseCorrente()) {
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
    
    private String getNomeFase(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "Arti Oscure";
            case MALVAGI: return "Malvagi";
            case HORCRUX: return "Horcrux";
            case GIOCA_CARTE: return "Gioca Carte";
            case ATTACCA: return "Attacca";
            case ACQUISTA_CARTE: return "Acquista";
            case FINE_TURNO: return "Fine Turno";
            default: return "Prossima Fase";
        }
    }
    
    public void aggiorna() {
        getChildren().clear();
        getChildren().addAll(creaHeroSection(), creaHandSection());
    }
}