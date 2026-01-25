package grafica.panels;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MessagePanel extends VBox {
    
    private static final int MAX_MESSAGES = 5;
    private static final double MESSAGE_DURATION = 3.0;
    
    public MessagePanel() {
        this.setAlignment(Pos.TOP_RIGHT);
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setMouseTransparent(true);
        this.setPrefWidth(400);
    }
    
    public void mostraMessaggio(String testo, TipoMessaggio tipo) {
        HBox messageBox = creaMessaggio(testo, tipo);
        
        if (this.getChildren().size() >= MAX_MESSAGES) {
            this.getChildren().remove(0);
        }
        
        this.getChildren().add(messageBox);
        animaEntrata(messageBox);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(MESSAGE_DURATION));
        delay.setOnFinished(e -> animaUscita(messageBox));
        delay.play();
    }
    
    private HBox creaMessaggio(String testo, TipoMessaggio tipo) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 15, 10, 15));
        box.setStyle(
            "-fx-background-color: " + tipo.coloreSfondo + ";" +
            "-fx-border-color: " + tipo.coloreBordo + ";" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 2);"
        );
        
        Text icona = new Text(tipo.icona);
        icona.setFont(Font.font(20));
        
        Text testoLabel = new Text(testo);
        testoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        testoLabel.setFill(Color.WHITE);
        testoLabel.setWrappingWidth(320);
        
        box.getChildren().addAll(icona, testoLabel);
        
        return box;
    }
    
    private void animaEntrata(HBox messageBox) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), messageBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    private void animaUscita(HBox messageBox) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), messageBox);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> this.getChildren().remove(messageBox));
        fade.play();
    }
    
    public enum TipoMessaggio {
        GIOCA_CARTA("🃏", "#2196F3", "#1976D2"),      // Blu
        ACQUISTA_CARTA("💰", "#4CAF50", "#388E3C"),   // Verde
        ATTACCO("⚔️", "#F44336", "#D32F2F"),          // Rosso
        EFFETTO("✨", "#9C27B0", "#7B1FA2"),          // Viola
        TRIGGER("🔔", "#FF9800", "#F57C00"),          // Arancione
        MALVAGIO("👹", "#E91E63", "#C2185B"),         // Rosa scuro
        ARTI_OSCURE("🌑", "#424242", "#212121"),      // Grigio scuro
        HORCRUX("💀", "#000000", "#000000"),          // Nero
        DANNO("💔", "#FF5252", "#D32F2F"),            // Rosso chiaro
        GUARIGIONE("❤️", "#66BB6A", "#43A047"),       // Verde chiaro
        INFO("ℹ️", "#03A9F4", "#0288D1");            // Azzurro
        
        private final String icona;
        private final String coloreSfondo;
        private final String coloreBordo;
        
        TipoMessaggio(String icona, String coloreSfondo, String coloreBordo) {
            this.icona = icona;
            this.coloreSfondo = coloreSfondo;
            this.coloreBordo = coloreBordo;
        }
        
        public String getIcona() { return icona; }
        public String getColoreSfondo() { return coloreSfondo; }
        public String getColoreBordo() { return coloreBordo; }
    }
}