package grafica.screens;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Consumer;

/**
 * Schermata di Game Over (Sconfitta)
 */
public class GameOverScreen extends StackPane {
    
    private int annoFallito;
    private String motivoSconfitta;
    private Consumer<String> onChoice;
    
    /**
     * Costruttore
     * 
     * @param annoFallito Anno in cui si è perso
     * @param motivoSconfitta Motivo della sconfitta
     * @param onChoice Callback con scelta: "retry" o "menu"
     */
    public GameOverScreen(int annoFallito, String motivoSconfitta, Consumer<String> onChoice) {
        this.annoFallito = annoFallito;
        this.motivoSconfitta = motivoSconfitta;
        this.onChoice = onChoice;
        inizializzaUI();
    }
    
    private void inizializzaUI() {
        // Dimensioni fisse FXGL
        double width = FXGL.getAppWidth();
        double height = FXGL.getAppHeight();
        
        this.setPrefSize(width, height);
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        
        // Sfondo scuro
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(20, 5, 5)); // Rosso molto scuro
        this.getChildren().add(bg);
        
        // Container principale
        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(60));
        
        // Titolo sconfitta
        Text titleText = new Text("💀 GAME OVER 💀");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        titleText.setFill(Color.DARKRED);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(4);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.RED);
        titleShadow.setRadius(30);
        titleText.setEffect(titleShadow);
        
        // Anno fallito
        Text annoText = new Text("Anno " + annoFallito + " Fallito");
        annoText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        annoText.setFill(Color.LIGHTCORAL);
        
        // Motivo sconfitta
        Text motivoText = new Text(motivoSconfitta);
        motivoText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        motivoText.setFill(Color.WHITE);
        motivoText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        motivoText.setWrappingWidth(800);
        
        // Messaggio incoraggiamento
        Text infoText = new Text("Non arrendetevi! Provate ancora!");
        infoText.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        infoText.setFill(Color.LIGHTGRAY);
        
        // Container bottoni
        HBox buttonsBox = new HBox(30);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Pulsante riprova
        VBox btnRetry = creaBottone("🔄 RIPROVA", "retry");
        
        // Pulsante menu
        VBox btnMenu = creaBottone("🏠 MENU PRINCIPALE", "menu");
        
        buttonsBox.getChildren().addAll(btnRetry, btnMenu);
        
        mainContainer.getChildren().addAll(
            titleText,
            annoText,
            motivoText,
            infoText,
            buttonsBox
        );
        
        this.getChildren().add(mainContainer);
    }
    
    private VBox creaBottone(String testo, String azione) {
        VBox button = new VBox(10);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(350, 80);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4a1a1a, #2a0a0a);" +
            "-fx-border-color: #8B0000;" +
            "-fx-border-width: 3;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;"
        );
        button.setPadding(new Insets(15));
        
        Text label = new Text(testo);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        label.setFill(Color.LIGHTCORAL);
        label.setWrappingWidth(330);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        button.getChildren().add(label);
        
        // Effetto hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #6a2a2a, #4a1a1a);" +
                "-fx-border-color: red;" +
                "-fx-border-width: 4;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;"
            );
            button.setScaleX(1.05);
            button.setScaleY(1.05);
            label.setFill(Color.RED);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4a1a1a, #2a0a0a);" +
                "-fx-border-color: #8B0000;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;"
            );
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            label.setFill(Color.LIGHTCORAL);
        });
        
        // Click handler
        button.setOnMouseClicked(e -> {
            if (onChoice != null) {
                System.out.println("✓ Scelta: " + azione);
                onChoice.accept(azione);
            }
        });
        
        return button;
    }
}