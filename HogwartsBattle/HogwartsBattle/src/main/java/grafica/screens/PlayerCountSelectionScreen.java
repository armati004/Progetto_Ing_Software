package grafica.screens;

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
 * Schermata per selezionare il numero di giocatori (1-4)
 */
public class PlayerCountSelectionScreen extends StackPane {
    
    private static final double WINDOW_WIDTH = 1920;
    private static final double WINDOW_HEIGHT = 1080;
    
    private Consumer<Integer> onPlayerCountSelected;
    
    /**
     * Costruttore
     * 
     * @param onPlayerCountSelected Callback quando il numero viene selezionato
     */
    public PlayerCountSelectionScreen(Consumer<Integer> onPlayerCountSelected) {
        this.onPlayerCountSelected = onPlayerCountSelected;
        
        inizializzaUI();
    }
    
    /**
     * Inizializza l'interfaccia
     */
    private void inizializzaUI() {
        this.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Sfondo scuro
        Rectangle bg = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT);
        bg.setFill(Color.rgb(15, 10, 30));
        this.getChildren().add(bg);
        
        // Container principale
        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(60));
        
        // Titolo
        Text titleText = new Text("⚡ HOGWARTS BATTLE ⚡");
        titleText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 64));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKRED);
        titleText.setStrokeWidth(4);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setRadius(30);
        titleText.setEffect(titleShadow);
        
        // Sottotitolo
        Text subtitle = new Text("Seleziona il numero di giocatori");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        subtitle.setFill(Color.LIGHTGRAY);
        
        // Container pulsanti
        HBox buttonsBox = new HBox(30);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Crea pulsanti per 1-4 giocatori
        for (int i = 1; i <= 4; i++) {
            final int numeroGiocatori = i;
            VBox button = creaBottoneGiocatore(numeroGiocatori);
            buttonsBox.getChildren().add(button);
        }
        
        // Note
        Text noteText = new Text("💡 Consigliato: 2-4 giocatori per la migliore esperienza");
        noteText.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        noteText.setFill(Color.GRAY);
        
        // Aggiungi tutto al container
        mainContainer.getChildren().addAll(
            titleText,
            subtitle,
            buttonsBox,
            noteText
        );
        
        this.getChildren().add(mainContainer);
    }
    
    /**
     * Crea un bottone per selezionare il numero di giocatori
     */
    private VBox creaBottoneGiocatore(int numero) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(220, 280);
        container.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2a2440, #1a1428);" +
            "-fx-border-color: #8B7355;" +
            "-fx-border-width: 3;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;"
        );
        container.setPadding(new Insets(20));
        
        // Effetto hover
        container.setOnMouseEntered(e -> {
            container.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #3a3450, #2a2438);" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 4;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        container.setOnMouseExited(e -> {
            container.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2a2440, #1a1428);" +
                "-fx-border-color: #8B7355;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;"
            );
        });
        
        // Numero grande
        Text numeroText = new Text(String.valueOf(numero));
        numeroText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 120));
        numeroText.setFill(Color.GOLD);
        numeroText.setStroke(Color.DARKGOLDENROD);
        numeroText.setStrokeWidth(2);
        
        // Label
        Text labelText = new Text(numero == 1 ? "Giocatore" : "Giocatori");
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        labelText.setFill(Color.LIGHTGRAY);
        
        // Icone giocatori
        HBox iconsBox = new HBox(5);
        iconsBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < numero; i++) {
            Text icon = new Text("🧙");
            icon.setFont(Font.font(28));
            iconsBox.getChildren().add(icon);
        }
        
        container.getChildren().addAll(numeroText, labelText, iconsBox);
        
        // Click handler
        container.setOnMouseClicked(e -> {
            System.out.println("✓ Selezionati " + numero + " giocatori");
            if (onPlayerCountSelected != null) {
                onPlayerCountSelected.accept(numero);
            }
        });
        
        return container;
    }
}