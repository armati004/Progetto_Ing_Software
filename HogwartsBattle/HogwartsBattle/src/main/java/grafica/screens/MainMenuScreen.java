package grafica.screens;

import com.almasb.fxgl.dsl.FXGL;
import data.SaveManager;
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
 * Schermata menu principale
 * Opzioni: Nuova Partita, Continua, Carica, Esci
 */
public class MainMenuScreen extends StackPane {
    
    private Consumer<String> onMenuChoice;
    
    public MainMenuScreen(Consumer<String> onMenuChoice) {
        this.onMenuChoice = onMenuChoice;
        inizializzaUI();
    }
    
    private void inizializzaUI() {
        // Dimensioni fisse FXGL
        double width = FXGL.getAppWidth();
        double height = FXGL.getAppHeight();
        
        this.setPrefSize(width, height);
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        
        // Sfondo
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(15, 10, 30));
        this.getChildren().add(bg);
        
        // Container principale
        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(60));
        
        // Titolo
        Text titleText = new Text("⚡ HOGWARTS BATTLE ⚡");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKRED);
        titleText.setStrokeWidth(4);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setRadius(30);
        titleText.setEffect(titleShadow);
        
        // Sottotitolo
        Text subtitle = new Text("Deck Building Game");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        subtitle.setFill(Color.LIGHTGRAY);
        
        // Container bottoni
        VBox buttonsBox = new VBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPrefWidth(400);
        
        // Pulsanti
        VBox btnNuovaPartita = creaBottoneMenu("🎮 NUOVA PARTITA", "new_game");
        
        // Verifica se esiste autosave
        boolean esisteAutosave = SaveManager.esisteAutosave();
        VBox btnContinua = creaBottoneMenu("▶️ CONTINUA", "continue");
        if (!esisteAutosave) {
            btnContinua.setOpacity(0.5);
            btnContinua.setDisable(true);
        }
        
        VBox btnCarica = creaBottoneMenu("📂 CARICA PARTITA", "load_game");
        VBox btnEsci = creaBottoneMenu("🚪 ESCI", "exit");
        
        buttonsBox.getChildren().addAll(
            btnNuovaPartita,
            btnContinua,
            btnCarica,
            btnEsci
        );
        
        // Info versione
        Text versionText = new Text("v1.0 - Anno 1-7");
        versionText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        versionText.setFill(Color.GRAY);
        
        mainContainer.getChildren().addAll(
            titleText,
            subtitle,
            buttonsBox,
            versionText
        );
        
        this.getChildren().add(mainContainer);
    }
    
    private VBox creaBottoneMenu(String testo, String azione) {
        VBox button = new VBox(10);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(400, 70);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3a2a50, #2a1a40);" +
            "-fx-border-color: #8B7355;" +
            "-fx-border-width: 3;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        button.setPadding(new Insets(15));
        
        Text label = new Text(testo);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label.setFill(Color.LIGHTGRAY);
        
        button.getChildren().add(label);
        
        // Effetto hover
        button.setOnMouseEntered(e -> {
            if (!button.isDisabled()) {
                button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #4a3a60, #3a2a50);" +
                    "-fx-border-color: gold;" +
                    "-fx-border-width: 4;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;"
                );
                button.setScaleX(1.05);
                button.setScaleY(1.05);
                label.setFill(Color.GOLD);
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #3a2a50, #2a1a40);" +
                    "-fx-border-color: #8B7355;" +
                    "-fx-border-width: 3;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;"
                );
                button.setScaleX(1.0);
                button.setScaleY(1.0);
                label.setFill(Color.LIGHTGRAY);
            }
        });
        
        // Click handler
        button.setOnMouseClicked(e -> {
            if (!button.isDisabled() && onMenuChoice != null) {
                System.out.println("✓ Menu scelta: " + azione);
                onMenuChoice.accept(azione);
            }
        });
        
        return button;
    }
}