package grafica.screens;

import com.almasb.fxgl.dsl.FXGL;
import data.GameSaveData;
import data.SaveManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Schermata per caricare partite salvate
 */
public class LoadGameScreen extends StackPane {
    
    private Consumer<GameSaveData> onLoadGame;
    private Consumer<Void> onBack;
    
    /**
     * Costruttore
     * 
     * @param onLoadGame Callback quando si carica una partita
     * @param onBack Callback per tornare al menu
     */
    public LoadGameScreen(Consumer<GameSaveData> onLoadGame, Consumer<Void> onBack) {
        this.onLoadGame = onLoadGame;
        this.onBack = onBack;
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
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setPrefWidth(1200);
        
        // Titolo
        Text titleText = new Text("📂 CARICA PARTITA");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        titleText.setFill(Color.LIGHTBLUE);
        titleText.setStroke(Color.DARKBLUE);
        titleText.setStrokeWidth(3);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setRadius(20);
        titleText.setEffect(titleShadow);
        
        // Carica lista salvataggi
        List<String> nomiSalvataggi = SaveManager.listaSalvataggi();
        
        VBox contentBox;
        
        if (nomiSalvataggi.isEmpty()) {
            // Nessun salvataggio
            contentBox = creaNessunSalvataggio();
        } else {
            // Lista salvataggi
            contentBox = creaListaSalvataggi(nomiSalvataggi);
        }
        
        // Pulsante indietro
        VBox btnIndietro = creaBottone("🔙 INDIETRO", () -> {
            if (onBack != null) {
                onBack.accept(null);
            }
        });
        
        mainContainer.getChildren().addAll(
            titleText,
            contentBox,
            btnIndietro
        );
        
        this.getChildren().add(mainContainer);
    }
    
    /**
     * Crea messaggio quando non ci sono salvataggi
     */
    private VBox creaNessunSalvataggio() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPrefHeight(400);
        
        Text messaggio = new Text("📭 Nessuna partita salvata");
        messaggio.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        messaggio.setFill(Color.GRAY);
        
        Text suggerimento = new Text("Inizia una nuova partita per creare un salvataggio");
        suggerimento.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        suggerimento.setFill(Color.DARKGRAY);
        
        box.getChildren().addAll(messaggio, suggerimento);
        
        return box;
    }
    
    /**
     * Crea lista scrollabile dei salvataggi
     */
    private VBox creaListaSalvataggi(List<String> nomiSalvataggi) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);
        
        // Container per le card dei salvataggi
        VBox savesList = new VBox(15);
        savesList.setAlignment(Pos.TOP_CENTER);
        savesList.setPadding(new Insets(10));
        
        // Crea card per ogni salvataggio
        for (String nomeSalvataggio : nomiSalvataggi) {
            GameSaveData saveData = SaveManager.getInfoSalvataggio(nomeSalvataggio);
            if (saveData != null) {
                VBox saveCard = creaSaveCard(nomeSalvataggio, saveData);
                savesList.getChildren().add(saveCard);
            }
        }
        
        // ScrollPane per lista
        ScrollPane scrollPane = new ScrollPane(savesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle(
            "-fx-background: #1a1a2e;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: #4a4a6a;" +
            "-fx-border-width: 2;"
        );
        
        // Nascondi barra orizzontale
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        container.getChildren().add(scrollPane);
        
        return container;
    }
    
    /**
     * Crea card per un singolo salvataggio
     */
    private VBox creaSaveCard(String nomeSalvataggio, GameSaveData saveData) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(1000);
        card.setPrefHeight(120);
        card.setStyle(
            "-fx-background-color: linear-gradient(to right, #2a2a4a, #1a1a3a);" +
            "-fx-border-color: #5a5a7a;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-cursor: hand;"
        );
        
        // Header con nome e data
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Nome partita
        Text nomeText = new Text("🎮 " + (saveData.getNomePartita() != null ? 
            saveData.getNomePartita() : nomeSalvataggio));
        nomeText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nomeText.setFill(Color.GOLD);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Data
        Text dataText = new Text("📅 " + saveData.getDataOra());
        dataText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        dataText.setFill(Color.LIGHTGRAY);
        
        header.getChildren().addAll(nomeText, spacer, dataText);
        
        // Info partita
        HBox info = new HBox(30);
        info.setAlignment(Pos.CENTER_LEFT);
        
        // Anno
        Text annoText = new Text("📚 Anno " + saveData.getAnnoCorrente());
        annoText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        annoText.setFill(Color.LIGHTBLUE);
        
        // Numero giocatori
        Text giocatoriText = new Text("👥 " + saveData.getNumeroGiocatori() + " Giocatori");
        giocatoriText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        giocatoriText.setFill(Color.LIGHTGREEN);
        
        // Stato
        String statoIcon = saveData.isVittoriaUltimaPartita() ? "🏆" : "⚔️";
        String statoTesto = saveData.isVittoriaUltimaPartita() ? "Vinto" : "In corso";
        Text statoText = new Text(statoIcon + " " + statoTesto);
        statoText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        statoText.setFill(saveData.isVittoriaUltimaPartita() ? Color.GOLD : Color.ORANGE);
        
        info.getChildren().addAll(annoText, giocatoriText, statoText);
        
        card.getChildren().addAll(header, info);
        
        // Effetto hover
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to right, #3a3a5a, #2a2a4a);" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 20;" +
                "-fx-cursor: hand;"
            );
            card.setScaleX(1.02);
            card.setScaleY(1.02);
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to right, #2a2a4a, #1a1a3a);" +
                "-fx-border-color: #5a5a7a;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 20;" +
                "-fx-cursor: hand;"
            );
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });
        
        // Click handler
        card.setOnMouseClicked(e -> {
            if (onLoadGame != null) {
                System.out.println("✓ Caricamento: " + nomeSalvataggio);
                onLoadGame.accept(saveData);
            }
        });
        
        return card;
    }
    
    /**
     * Crea bottone generico
     */
    private VBox creaBottone(String testo, Runnable azione) {
        VBox button = new VBox(10);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(300, 70);
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
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setFill(Color.LIGHTGRAY);
        
        button.getChildren().add(label);
        
        // Effetto hover
        button.setOnMouseEntered(e -> {
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
        });
        
        button.setOnMouseExited(e -> {
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
        });
        
        // Click handler
        button.setOnMouseClicked(e -> {
            if (azione != null) {
                azione.run();
            }
        });
        
        return button;
    }
}