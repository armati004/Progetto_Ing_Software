package grafica.screens;

import com.almasb.fxgl.dsl.FXGL;
import data.ProgressionManager;
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
 * Schermata di vittoria con opzione di avanzamento
 */
public class VictoryScreen extends StackPane {
    
    private int annoCompletato;
    private Consumer<String> onChoice;
    
    /**
     * Costruttore
     * 
     * @param annoCompletato Anno appena completato
     * @param onChoice Callback con scelta: "continue" o "menu"
     */
    public VictoryScreen(int annoCompletato, Consumer<String> onChoice) {
        this.annoCompletato = annoCompletato;
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
        
        // Sfondo
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(15, 10, 30));
        this.getChildren().add(bg);
        
        // Container principale
        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(60));
        
        // Titolo vittoria
        Text titleText = new Text("🎉 VITTORIA! 🎉");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKGOLDENROD);
        titleText.setStrokeWidth(4);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setRadius(30);
        titleText.setEffect(titleShadow);
        
        // Anno completato
        Text annoText = new Text("Anno " + annoCompletato + " Completato!");
        annoText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        annoText.setFill(Color.LIGHTBLUE);
        
        // Messaggio
        boolean ultimoAnno = annoCompletato >= 7;
        Text messageText;
        
        if (ultimoAnno) {
            messageText = new Text("🏆 Avete completato tutti e 7 gli anni!\n Hogwarts è salva!");
        } else {
            int prossimoAnno = annoCompletato + 1;
            messageText = new Text("📖 Siete pronti per l'Anno " + prossimoAnno + "?");
        }
        
        messageText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        messageText.setFill(Color.WHITE);
        messageText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Progresso
        String progresso = ProgressionManager.getMessaggioProgresso(annoCompletato);
        Text progressText = new Text(progresso);
        progressText.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        progressText.setFill(Color.LIGHTGRAY);
        
        // Container bottoni
        HBox buttonsBox = new HBox(30);
        buttonsBox.setAlignment(Pos.CENTER);
        
        if (!ultimoAnno) {
            // Pulsante continua
            VBox btnContinua = creaBottone("▶️ ANNO SUCCESSIVO", "continue");
            buttonsBox.getChildren().add(btnContinua);
        }
        
        // Pulsante menu
        VBox btnMenu = creaBottone("🏠 MENU PRINCIPALE", "menu");
        buttonsBox.getChildren().add(btnMenu);
        
        mainContainer.getChildren().addAll(
            titleText,
            annoText,
            messageText,
            progressText,
            buttonsBox
        );
        
        this.getChildren().add(mainContainer);
    }
    
    private VBox creaBottone(String testo, String azione) {
        VBox button = new VBox(10);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(350, 80);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3a2a50, #2a1a40);" +
            "-fx-border-color: #8B7355;" +
            "-fx-border-width: 3;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;"
        );
        button.setPadding(new Insets(15));
        
        Text label = new Text(testo);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        label.setFill(Color.LIGHTGRAY);
        label.setWrappingWidth(330);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        button.getChildren().add(label);
        
        // Effetto hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4a3a60, #3a2a50);" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 4;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;"
            );
            button.setScaleX(1.05);
            button.setScaleY(1.05);
            label.setFill(Color.GOLD);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #3a2a50, #2a1a40);" +
                "-fx-border-color: #8B7355;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;"
            );
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            label.setFill(Color.LIGHTGRAY);
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