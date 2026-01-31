package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import gioco.StatoDiGioco;
import gioco.Giocatore;

/**
 * PlayersStatsPanel - Mostra info complete di tutti i giocatori
 */
public class PlayersStatsPanel extends HBox {
    
    private StatoDiGioco stato;
    
    public PlayersStatsPanel(StatoDiGioco stato) {
        this.stato = stato;
        
        setStyle("-fx-background-color: #1a1a1a;");
        setSpacing(10);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER);
        
        aggiorna();
    }
    
    private VBox creaPlayerCard(int indice) {
        Giocatore g = stato.getGiocatori().get(indice);
        
        VBox card = new VBox(5);
        card.setStyle(
            "-fx-border-width: 2;" +
            "-fx-background-color: #2a2a2a;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: " + (indice == stato.getGiocatoreCorrente() ? "#00FF00" : "#666") + ";"
        );
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        HBox.setHgrow(card, Priority.ALWAYS);
        
        // Nome eroe
        Label nome = new Label(g.getEroe().getNome());
        nome.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nome.setTextFill(Color.web("#FFD700"));
        nome.setWrapText(true);
        nome.setMaxWidth(Double.MAX_VALUE);
        nome.setAlignment(Pos.CENTER);
        
        HBox containerEroi = new HBox(15);
        
        // Salute con label
        VBox saluteRow = new VBox(5);
        saluteRow.setAlignment(Pos.CENTER);
        Label saluteLabel = new Label("Salute:");
        saluteLabel.setFont(Font.font("Arial", 14));
        saluteLabel.setTextFill(Color.web("#CCCCCC"));
        Label saluteValue = new Label("❤️ " + g.getSalute() + "/" + g.getSaluteMax());
        saluteValue.setTextFill(Color.web("#FF6666"));
        saluteValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        saluteRow.getChildren().addAll(saluteLabel, saluteValue);
        
        // Attacco con label
        VBox attaccoRow = new VBox(5);
        attaccoRow.setAlignment(Pos.CENTER);
        Label attaccoLabel = new Label("Attacco:");
        attaccoLabel.setFont(Font.font("Arial", 14));
        attaccoLabel.setTextFill(Color.web("#CCCCCC"));
        Label attaccoValue = new Label("⚔️ " + g.getAttacco());
        attaccoValue.setTextFill(Color.web("#FFAA66"));
        attaccoValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        attaccoRow.getChildren().addAll(attaccoLabel, attaccoValue);
        
        // Influenza con label
        VBox influenzaRow = new VBox(5);
        influenzaRow.setAlignment(Pos.CENTER);
        Label influenzaLabel = new Label("Influenza:");
        influenzaLabel.setFont(Font.font("Arial", 14));
        influenzaLabel.setTextFill(Color.web("#CCCCCC"));
        Label influenzaValue = new Label("🪙 " + g.getGettone());
        influenzaValue.setTextFill(Color.web("#FFD700"));
        influenzaValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        influenzaRow.getChildren().addAll(influenzaLabel, influenzaValue);
        
        containerEroi.setAlignment(Pos.CENTER);
        
        containerEroi.getChildren().addAll(saluteRow, attaccoRow, influenzaRow);
        
        card.getChildren().addAll(nome, containerEroi);
        return card;
    }
    
    public void aggiorna() {
        getChildren().clear();
        for (int i = 0; i < stato.getGiocatori().size(); i++) {
            getChildren().add(creaPlayerCard(i));
        }
    }
}