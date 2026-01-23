package grafica.screens;

import carte.Competenza;
import data.ProficiencyFactory;
import grafica.managers.GameFlowManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Schermata di selezione competenze per il Gioco 6.
 * 
 * Permette a ciascun giocatore di scegliere una competenza tra tutte quelle disponibili.
 * Tutte le competenze sono disponibili contemporaneamente.
 */
public class ProficiencySelectionScreen extends StackPane {
    
    private static final double WINDOW_WIDTH = 1920;
    private static final double WINDOW_HEIGHT = 1080;
    
    private int numeroGiocatori;
    private int giocatoreCorrente;
    private List<String> eroiGiocatori; // Nomi degli eroi per visualizzazione
    
    // Selezioni
    private List<String> competenzeSelezionate;
    
    // UI Components
    private VBox mainContainer;
    private Text titleText;
    private Text instructionText;
    private FlowPane proficienciesContainer;
    private VBox selectedInfoBox;
    
    // Callback quando la selezione è completa
    private Consumer<List<String>> onSelectionComplete;
    
    /**
     * Costruttore
     */
    public ProficiencySelectionScreen(List<String> eroiGiocatori, Consumer<List<String>> onComplete) {
        this.numeroGiocatori = eroiGiocatori.size();
        this.eroiGiocatori = eroiGiocatori;
        this.giocatoreCorrente = 0;
        this.competenzeSelezionate = new ArrayList<>();
        this.onSelectionComplete = onComplete;
        
        inizializzaUI();
    }
    
    /**
     * Inizializza l'interfaccia
     */
    private void inizializzaUI() {
        this.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Sfondo
        Rectangle bg = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT);
        bg.setFill(Color.rgb(15, 10, 30));
        this.getChildren().add(bg);
        
        // Container principale
        mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        
        // Titolo
        titleText = new Text("⚡ ANNO 6: SELEZIONE COMPETENZE ⚡");
        titleText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 48));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKRED);
        titleText.setStrokeWidth(3);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setRadius(20);
        titleText.setEffect(titleShadow);
        
        // Sottotitolo
        Text subtitle = new Text("Ogni giocatore sceglie una competenza magica");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        subtitle.setFill(Color.LIGHTGRAY);
        
        // Istruzioni
        String nomeEroe = eroiGiocatori.get(0);
        instructionText = new Text(nomeEroe + " (Giocatore 1): Scegli la tua competenza");
        instructionText.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        instructionText.setFill(Color.CYAN);
        
        // Container competenze (FlowPane per gestire meglio il wrap)
        proficienciesContainer = new FlowPane(20, 20);
        proficienciesContainer.setAlignment(Pos.CENTER);
        proficienciesContainer.setPrefWrapLength(WINDOW_WIDTH - 100);
        
        // Info selezioni
        selectedInfoBox = new VBox(10);
        selectedInfoBox.setAlignment(Pos.CENTER);
        selectedInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); " +
                                "-fx-background-radius: 15; " +
                                "-fx-padding: 20;");
        selectedInfoBox.setPrefWidth(800);
        
        mainContainer.getChildren().addAll(
            titleText,
            subtitle,
            instructionText,
            proficienciesContainer,
            selectedInfoBox
        );
        
        this.getChildren().add(mainContainer);
        
        // Mostra le competenze
        mostraCompetenze();
    }
    
    /**
     * Mostra tutte le competenze disponibili
     */
    private void mostraCompetenze() {
        proficienciesContainer.getChildren().clear();
        
        List<String> competenzeDisponibili = GameFlowManager.getCompetenzeDisponibili();
        
        for (String idCompetenza : competenzeDisponibili) {
            try {
                Competenza comp = ProficiencyFactory.creaCompetenza(idCompetenza);
                VBox compCard = creaCompetenzaCard(comp);
                proficienciesContainer.getChildren().add(compCard);
            } catch (Exception e) {
                System.err.println("Errore caricamento competenza: " + idCompetenza);
            }
        }
        
        aggiornaInfoSelezionati();
    }
    
    /**
     * Crea una card per una competenza
     */
    private VBox creaCompetenzaCard(Competenza competenza) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(240, 380);
        card.setStyle("-fx-background-color: rgba(40, 20, 60, 0.95); " +
                     "-fx-background-radius: 15; " +
                     "-fx-border-color: purple; " +
                     "-fx-border-width: 3; " +
                     "-fx-border-radius: 15; " +
                     "-fx-padding: 15; " +
                     "-fx-cursor: hand;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(12);
        card.setEffect(shadow);
        
        try {
            // Immagine competenza
            Image compImage = caricaImmagine(competenza.getPathImmagine());
            ImageView imageView = new ImageView(compImage);
            imageView.setFitWidth(200);
            imageView.setFitHeight(260);
            imageView.setPreserveRatio(true);
            
            Rectangle clip = new Rectangle(200, 260);
            clip.setArcWidth(12);
            clip.setArcHeight(12);
            imageView.setClip(clip);
            
            // Nome
            Text nameText = new Text(competenza.getNome());
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 17));
            nameText.setFill(Color.LIGHTBLUE);
            nameText.setWrappingWidth(220);
            nameText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            
            card.getChildren().addAll(imageView, nameText);
            
        } catch (Exception e) {
            Text nameText = new Text(competenza.getNome());
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            nameText.setFill(Color.WHITE);
            nameText.setWrappingWidth(220);
            nameText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            card.getChildren().add(nameText);
        }
        
        // Effetti hover
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: rgba(60, 40, 90, 0.98); " +
                         "-fx-background-radius: 15; " +
                         "-fx-border-color: cyan; " +
                         "-fx-border-width: 4; " +
                         "-fx-border-radius: 15; " +
                         "-fx-padding: 15; " +
                         "-fx-cursor: hand;");
            card.setScaleX(1.05);
            card.setScaleY(1.05);
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: rgba(40, 20, 60, 0.95); " +
                         "-fx-background-radius: 15; " +
                         "-fx-border-color: purple; " +
                         "-fx-border-width: 3; " +
                         "-fx-border-radius: 15; " +
                         "-fx-padding: 15; " +
                         "-fx-cursor: hand;");
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });
        
        // Click per selezionare
        card.setOnMouseClicked(e -> {
            selezionaCompetenza(competenza.getId(), competenza.getNome());
        });
        
        return card;
    }
    
    /**
     * Seleziona una competenza
     */
    private void selezionaCompetenza(String idCompetenza, String nomeCompetenza) {
        competenzeSelezionate.add(idCompetenza);
        giocatoreCorrente++;
        
        String nomeEroe = eroiGiocatori.get(giocatoreCorrente - 1);
        System.out.println("✓ " + nomeEroe + " (Giocatore " + giocatoreCorrente + 
                          ") ha scelto: " + nomeCompetenza);
        
        // Verifica se tutti hanno scelto
        if (giocatoreCorrente >= numeroGiocatori) {
            completaSelezione();
        } else {
            // Prossimo giocatore
            String prossimoEroe = eroiGiocatori.get(giocatoreCorrente);
            instructionText.setText(prossimoEroe + " (Giocatore " + (giocatoreCorrente + 1) + 
                                   "): Scegli la tua competenza");
            aggiornaInfoSelezionati();
        }
    }
    
    /**
     * Completa la selezione e chiama il callback
     */
    private void completaSelezione() {
        System.out.println("\n✅ Selezione competenze completata!");
        
        for (int i = 0; i < eroiGiocatori.size(); i++) {
            try {
                Competenza comp = ProficiencyFactory.creaCompetenza(competenzeSelezionate.get(i));
                System.out.println("  " + eroiGiocatori.get(i) + " → " + comp.getNome());
            } catch (Exception e) {
                System.out.println("  " + eroiGiocatori.get(i) + " → " + competenzeSelezionate.get(i));
            }
        }
        
        if (onSelectionComplete != null) {
            onSelectionComplete.accept(competenzeSelezionate);
        }
    }
    
    /**
     * Aggiorna le info sui selezionati
     */
    private void aggiornaInfoSelezionati() {
        selectedInfoBox.getChildren().clear();
        
        Text title = new Text("📜 COMPETENZE SELEZIONATE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setFill(Color.GOLD);
        
        selectedInfoBox.getChildren().add(title);
        
        // Mostra competenze selezionate
        for (int i = 0; i < competenzeSelezionate.size(); i++) {
            try {
                Competenza comp = ProficiencyFactory.creaCompetenza(competenzeSelezionate.get(i));
                String nomeEroe = eroiGiocatori.get(i);
                
                Text playerInfo = new Text(nomeEroe + " → " + comp.getNome());
                playerInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
                playerInfo.setFill(Color.LIGHTBLUE);
                selectedInfoBox.getChildren().add(playerInfo);
            } catch (Exception e) {
                // Ignora
            }
        }
        
        // Se nessuna selezione ancora, mostra messaggio
        if (competenzeSelezionate.isEmpty()) {
            Text emptyText = new Text("Nessuna competenza selezionata");
            emptyText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            emptyText.setFill(Color.GRAY);
            selectedInfoBox.getChildren().add(emptyText);
        }
    }
    
    /**
     * Carica un'immagine
     */
    private Image caricaImmagine(String path) throws Exception {
        String cleanPath = path.replace("../", "").replace("\\", "/");
        
        try {
            var stream = getClass().getClassLoader().getResourceAsStream(cleanPath);
            if (stream != null) return new Image(stream);
        } catch (Exception e) { }
        
        try {
            File file = new File(cleanPath);
            if (file.exists()) return new Image(new FileInputStream(file));
        } catch (Exception e) { }
        
        try {
            File projectFile = new File("/mnt/project/" + cleanPath);
            if (projectFile.exists()) return new Image(new FileInputStream(projectFile));
        } catch (Exception e) { }
        
        throw new Exception("Impossibile caricare: " + path);
    }
}