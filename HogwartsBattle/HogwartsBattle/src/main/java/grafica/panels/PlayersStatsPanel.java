package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import gioco.StatoDiGioco;
import gioco.Giocatore;

/**
 * PlayersStatsPanel - Mostra le statistiche di tutti i giocatori
 * Altezza: 15% dello schermo
 * Larghezza: 100%
 */
public class PlayersStatsPanel extends HBox {
    
    private StatoDiGioco stato;
    private VBox[] playerCards;
    
    public PlayersStatsPanel(StatoDiGioco stato) {
        this.stato = stato;
        
        // Configurazione del pannello
        setStyle("-fx-background-color: #1a1a1a;");
        setSpacing(15);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        
        // Crea una card per ogni giocatore
        playerCards = new VBox[stato.getGiocatori().size()];
        
        for (int i = 0; i < stato.getGiocatori().size(); i++) {
            playerCards[i] = creaPlayerCard(i);
            getChildren().add(playerCards[i]);
        }
    }
    
    /**
     * Crea una card con le statistiche del giocatore
     */
    private VBox creaPlayerCard(int indiceGiocatore) {
        Giocatore giocatore = stato.getGiocatori().get(indiceGiocatore);
        
        VBox card = new VBox();
        card.setStyle("-fx-border-color: #666; -fx-border-width: 2; " +
                     "-fx-background-color: #2a2a2a; -fx-border-radius: 5;");
        card.setPadding(new Insets(8));
        card.setSpacing(5);
        card.setPrefWidth(150);
        
        // Nome eroe
        Label nomeLabel = new Label(giocatore.getEroe().getNome());
        nomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nomeLabel.setTextFill(Color.web("#FFD700")); // Oro
        
        // Salute
        HBox saluteBox = creaStatRow("â¤ï¸", "Salute: " + giocatore.getSalute() + "/10");
        
        // Attacchi
        HBox attacchiBox = creaStatRow("âš”ï¸", "Attacchi: " + giocatore.getAttacco());
        
        // Influenza (Gettoni)
        HBox influenzaBox = creaStatRow("ðŸª™", "Influenza: " + giocatore.getGettone());
        
        card.getChildren().addAll(nomeLabel, saluteBox, attacchiBox, influenzaBox);
        
        // Evidenzia il giocatore corrente
        if (indiceGiocatore == stato.getGiocatoreCorrente()) {
            card.setStyle("-fx-border-color: #00FF00; -fx-border-width: 3; " +
                         "-fx-background-color: #1a3a1a; -fx-border-radius: 5;");
        }
        
        return card;
    }
    
    /**
     * Crea una riga con icona e etichetta
     */
    private HBox creaStatRow(String icon, String testo) {
        HBox row = new HBox();
        row.setSpacing(5);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 12));
        
        Label textLabel = new Label(testo);
        textLabel.setFont(Font.font("Arial", 11));
        textLabel.setTextFill(Color.WHITE);
        
        row.getChildren().addAll(iconLabel, textLabel);
        return row;
    }
    
    /**
     * Aggiorna tutte le statistiche dei giocatori
     */
    public void aggiorna() {
        for (int i = 0; i < stato.getGiocatori().size(); i++) {
            aggiornaPlayerCard(i);
        }
    }
    
    /**
     * Aggiorna una singola card del giocatore
     */
    private void aggiornaPlayerCard(int indiceGiocatore) {
        Giocatore giocatore = stato.getGiocatori().get(indiceGiocatore);
        VBox card = playerCards[indiceGiocatore];
        
        // Ricostruisci la card
        card.getChildren().clear();
        
        Label nomeLabel = new Label(giocatore.getEroe().getNome());
        nomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nomeLabel.setTextFill(Color.web("#FFD700"));
        
        HBox saluteBox = creaStatRow("â¤ï¸", "Salute: " + giocatore.getSalute() + "/10");
        HBox attacchiBox = creaStatRow("âš”ï¸", "Attacchi: " + giocatore.getAttacco());
        HBox influenzaBox = creaStatRow("ðŸª™", "Influenza: " + giocatore.getGettone());
        
        card.getChildren().addAll(nomeLabel, saluteBox, attacchiBox, influenzaBox);
        
        // Evidenzia il giocatore corrente
        if (indiceGiocatore == stato.getGiocatoreCorrente()) {
            card.setStyle("-fx-border-color: #00FF00; -fx-border-width: 3; " +
                         "-fx-background-color: #1a3a1a; -fx-border-radius: 5;");
        } else {
            card.setStyle("-fx-border-color: #666; -fx-border-width: 2; " +
                         "-fx-background-color: #2a2a2a; -fx-border-radius: 5;");
        }
    }
}